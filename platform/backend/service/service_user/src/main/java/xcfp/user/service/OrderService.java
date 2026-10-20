package xcfp.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import xcfp.model.Orderinfo;
import xcfp.vo.order.OrderCreateRequest;
import xcfp.vo.order.ShipRequest;

/**
 * @author Kerr
 * @create 2021-10-27 1:55
 */
public interface OrderService extends IService<Orderinfo> {

    Orderinfo createOrder(Long userId, OrderCreateRequest request);

    Page<Orderinfo> pageForUser(Long userId, long current, long limit, String state);

    Orderinfo getUserOrder(Long userId, String orderid);

    Orderinfo pay(Long userId, String orderid);

    Orderinfo cancel(Long userId, String orderid);

    Orderinfo confirm(Long userId, String orderid);

    Page<Orderinfo> pageForAdmin(long current, long limit, String state, String orderid);

    Orderinfo getAdminOrder(String orderid);

    Orderinfo ship(String orderid, ShipRequest request);
}
