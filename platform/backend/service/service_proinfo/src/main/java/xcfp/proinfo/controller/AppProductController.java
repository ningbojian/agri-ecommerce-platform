package xcfp.proinfo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.Result;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.ProductCategory;
import xcfp.model.ProductInfo;
import xcfp.proinfo.service.ProductCategoryService;
import xcfp.proinfo.service.ProductInfoService;

import java.math.BigDecimal;

/**
 * 小程序商品浏览接口（公开）。
 */
@Api(tags = "小程序-商品")
@RestController
@RequestMapping("/app/product")
public class AppProductController {

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private ProductCategoryService productCategoryService;

    @ApiOperation("启用中的分类列表")
    @GetMapping("categories")
    public Result categories() {
        QueryWrapper<ProductCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByAsc("sort_order").orderByAsc("id");
        return Result.ok(productCategoryService.list(wrapper));
    }

    @ApiOperation("商品分页（仅上架且审核通过）")
    @GetMapping("page")
    public Result page(@RequestParam(defaultValue = "1") long current,
                       @RequestParam(defaultValue = "10") long limit,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) BigDecimal minPrice,
                       @RequestParam(required = false) BigDecimal maxPrice) {
        Page<ProductInfo> page = new Page<>(Math.max(current, 1), Math.min(Math.max(limit, 1), 50));
        QueryWrapper<ProductInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("publish_status", 1).eq("audit_status", 1);
        if (categoryId != null && categoryId > 0) {
            wrapper.eq("category_id", categoryId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("proname", keyword.trim()).or().like("supname", keyword.trim()));
        }
        if (minPrice != null) {
            wrapper.ge("price", minPrice);
        }
        if (maxPrice != null) {
            wrapper.le("price", maxPrice);
        }
        wrapper.orderByDesc("create_time").orderByDesc("id");
        return Result.ok(productInfoService.page(page, wrapper));
    }

    @ApiOperation("商品详情")
    @GetMapping("{id}")
    public Result detail(@PathVariable Long id) {
        ProductInfo product = productInfoService.getById(id);
        if (product == null
                || !Integer.valueOf(1).equals(product.getPublishStatus())
                || !Integer.valueOf(1).equals(product.getAuditStatus())) {
            throw new XcfpException(ResultCodeEnum.PRODUCT_UNAVAILABLE);
        }
        return Result.ok(product);
    }
}
