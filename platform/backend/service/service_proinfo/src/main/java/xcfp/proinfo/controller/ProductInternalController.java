package xcfp.proinfo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.Result;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.ProductInfo;
import xcfp.proinfo.mapper.ProductInfoMapper;
import xcfp.proinfo.service.ProductInfoService;
import xcfp.vo.internal.ReserveRequest;
import xcfp.vo.internal.ReserveVO;

import javax.validation.Valid;

/**
 * 服务间库存接口，由 X-Internal-Token 保护。
 */
@Api(tags = "内部-库存")
@Validated
@RestController
@RequestMapping("/internal/product")
public class ProductInternalController {

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @ApiOperation("预占库存并返回商品快照")
    @PostMapping("reserve")
    public Result reserve(@Valid @RequestBody ReserveRequest request) {
        int rows = productInfoMapper.deductStock(request.getProductId(), request.getNum());
        if (rows == 0) {
            ProductInfo product = productInfoService.getById(request.getProductId());
            if (product == null
                    || !Integer.valueOf(1).equals(product.getPublishStatus())
                    || !Integer.valueOf(1).equals(product.getAuditStatus())) {
                throw new XcfpException(ResultCodeEnum.PRODUCT_UNAVAILABLE);
            }
            throw new XcfpException(ResultCodeEnum.STOCK_NOT_ENOUGH);
        }

        ProductInfo product = productInfoService.getById(request.getProductId());
        if (product == null) {
            throw new XcfpException(ResultCodeEnum.PRODUCT_UNAVAILABLE);
        }
        ReserveVO snapshot = new ReserveVO();
        snapshot.setId(product.getId());
        snapshot.setProname(product.getProname());
        snapshot.setPicurl(product.getPicurl());
        snapshot.setSupid(product.getSupid());
        snapshot.setSupname(product.getSupname());
        snapshot.setPrice(product.getPrice());
        snapshot.setStockAfter(product.getStock());
        return Result.ok(snapshot);
    }

    @ApiOperation("释放库存（下单失败补偿或取消订单）")
    @PostMapping("release")
    public Result release(@Valid @RequestBody ReserveRequest request) {
        productInfoMapper.restoreStock(request.getProductId(), request.getNum());
        return Result.ok();
    }
}
