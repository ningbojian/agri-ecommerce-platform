package xcfp.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.Orderinfo;
import xcfp.model.PaymentRecord;
import xcfp.model.UserAddress;
import xcfp.user.client.ProductClient;
import xcfp.user.mapper.OrderMapper;
import xcfp.user.mapper.PaymentRecordMapper;
import xcfp.user.payment.PaymentGateway;
import xcfp.user.payment.PaymentResult;
import xcfp.user.service.OrderService;
import xcfp.user.service.UserAddressService;
import xcfp.vo.internal.ReserveVO;
import xcfp.vo.order.OrderCreateRequest;
import xcfp.vo.order.ShipRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Kerr
 * @create 2021-10-27 1:55
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orderinfo> implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final DateTimeFormatter ORDER_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter PAY_NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private PaymentGateway paymentGateway;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orderinfo createOrder(Long userId, OrderCreateRequest request) {
        UserAddress address = userAddressService.getOwned(request.getAddressId(), userId);

        // 一次调用完成：校验可售、原子扣库存、返回商品快照，避免先查后扣的竞态
        ReserveVO snapshot = productClient.reserve(request.getProductId(), request.getNum());

        Orderinfo order = new Orderinfo();
        order.setUserid(userId);
        order.setSupid(snapshot.getSupid());
        order.setOrderid(generateOrderId());
        order.setProductId(snapshot.getId());
        order.setProname(snapshot.getProname());
        order.setPicurl(snapshot.getPicurl());
        order.setSupname(snapshot.getSupname());
        order.setPrice(snapshot.getPrice());
        order.setNum(request.getNum());
        order.setPay(snapshot.getPrice()
                .multiply(BigDecimal.valueOf(request.getNum()))
                .setScale(2, RoundingMode.HALF_UP));
        order.setAddid(address.getId());
        order.setReceiverName(address.getAddname());
        order.setReceiverTel(address.getAddtel());
        order.setReceiverAddress(address.getAddress());
        order.setState(Orderinfo.STATE_CREATED);
        order.setIsDelete(0);

        try {
            save(order);
        } catch (RuntimeException e) {
            // 订单落库失败，补偿恢复已预占的库存
            try {
                productClient.release(snapshot.getId(), request.getNum());
            } catch (Exception releaseException) {
                log.error("Compensating stock release failed: productId={}, num={}",
                        snapshot.getId(), request.getNum(), releaseException);
            }
            throw e;
        }
        return order;
    }

    @Override
    public Page<Orderinfo> pageForUser(Long userId, long current, long limit, String state) {
        Page<Orderinfo> page = new Page<>(Math.max(current, 1), Math.min(Math.max(limit, 1), 50));
        QueryWrapper<Orderinfo> wrapper = new QueryWrapper<>();
        wrapper.eq("userid", userId);
        if (StringUtils.hasText(state)) {
            wrapper.eq("state", state.trim());
        }
        wrapper.orderByDesc("create_time").orderByDesc("id");
        return page(page, wrapper);
    }

    @Override
    public Orderinfo getUserOrder(Long userId, String orderid) {
        Orderinfo order = getByOrderId(orderid);
        if (order == null) {
            throw new XcfpException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        if (userId == null || !userId.equals(order.getUserid())) {
            throw new XcfpException(ResultCodeEnum.PERMISSION);
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orderinfo pay(Long userId, String orderid) {
        Orderinfo order = getUserOrder(userId, orderid);

        // 幂等：已支付或更后续状态的订单直接返回
        if (Orderinfo.STATE_PAID.equals(order.getState())
                || Orderinfo.STATE_SHIPPED.equals(order.getState())
                || Orderinfo.STATE_COMPLETED.equals(order.getState())) {
            return order;
        }
        if (!Orderinfo.STATE_CREATED.equals(order.getState())) {
            throw new XcfpException(ResultCodeEnum.ORDER_STATE_ERROR);
        }

        PaymentResult payment = paymentGateway.pay(order);
        if (!payment.isSuccess()) {
            throw new XcfpException("支付失败，请稍后重试", ResultCodeEnum.SERVICE_ERROR.getCode());
        }

        LocalDateTime now = LocalDateTime.now();
        order.setState(Orderinfo.STATE_PAID);
        order.setPayTime(now);
        updateById(order);

        PaymentRecord record = new PaymentRecord();
        record.setPayNo(generatePayNo());
        record.setOrderid(order.getOrderid());
        record.setUserid(userId);
        record.setAmount(order.getPay());
        record.setChannel(payment.getChannel());
        record.setStatus("SUCCESS");
        record.setTransactionId(payment.getTransactionId());
        record.setPayTime(now);
        paymentRecordMapper.insert(record);

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orderinfo cancel(Long userId, String orderid) {
        Orderinfo order = getUserOrder(userId, orderid);
        if (Orderinfo.STATE_CANCELLED.equals(order.getState())) {
            return order;
        }
        if (!Orderinfo.STATE_CREATED.equals(order.getState())) {
            throw new XcfpException(ResultCodeEnum.ORDER_STATE_ERROR);
        }

        order.setState(Orderinfo.STATE_CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        updateById(order);

        // 释放失败会抛出异常并回滚本地事务，订单仍保持待付款，用户可重试
        productClient.release(order.getProductId(), order.getNum());
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orderinfo confirm(Long userId, String orderid) {
        Orderinfo order = getUserOrder(userId, orderid);
        if (!Orderinfo.STATE_SHIPPED.equals(order.getState())) {
            throw new XcfpException(ResultCodeEnum.ORDER_STATE_ERROR);
        }
        order.setState(Orderinfo.STATE_COMPLETED);
        order.setCompleteTime(LocalDateTime.now());
        updateById(order);
        return order;
    }

    @Override
    public Page<Orderinfo> pageForAdmin(long current, long limit, String state, String orderid) {
        Page<Orderinfo> page = new Page<>(Math.max(current, 1), Math.min(Math.max(limit, 1), 50));
        QueryWrapper<Orderinfo> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(state)) {
            wrapper.eq("state", state.trim());
        }
        if (StringUtils.hasText(orderid)) {
            wrapper.like("orderid", orderid.trim());
        }
        wrapper.orderByDesc("create_time").orderByDesc("id");
        return page(page, wrapper);
    }

    @Override
    public Orderinfo getAdminOrder(String orderid) {
        Orderinfo order = getByOrderId(orderid);
        if (order == null) {
            throw new XcfpException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orderinfo ship(String orderid, ShipRequest request) {
        Orderinfo order = getAdminOrder(orderid);
        if (!Orderinfo.STATE_PAID.equals(order.getState())) {
            throw new XcfpException(ResultCodeEnum.ORDER_STATE_ERROR);
        }
        order.setState(Orderinfo.STATE_SHIPPED);
        order.setExpress(request.getExpress().trim());
        order.setShipTime(LocalDateTime.now());
        updateById(order);
        return order;
    }

    private Orderinfo getByOrderId(String orderid) {
        return getOne(new QueryWrapper<Orderinfo>().eq("orderid", orderid));
    }

    private String generateOrderId() {
        return "ORD" + LocalDateTime.now().format(ORDER_ID_FORMAT)
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    private String generatePayNo() {
        return "PAY" + LocalDateTime.now().format(PAY_NO_FORMAT)
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }
}
