package xcfp.proinfo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.Result;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.ProductInfo;
import xcfp.proinfo.service.ProductInfoService;
import xcfp.vo.product.AuditRequest;
import xcfp.vo.product.ProductSaveRequest;
import xcfp.vo.product.PublishRequest;
import xcfp.vo.product.StockRequest;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "管理端-商品")
@Validated
@RestController
@RequestMapping("/admin/product")
public class AdminProductController {

    @Autowired
    private ProductInfoService productInfoService;

    @ApiOperation("商品分页")
    @GetMapping("page")
    public Result page(@RequestParam(defaultValue = "1") long current,
                       @RequestParam(defaultValue = "10") long limit,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) Integer publishStatus,
                       @RequestParam(required = false) Integer auditStatus) {
        Page<ProductInfo> page = new Page<>(Math.max(current, 1), Math.min(Math.max(limit, 1), 50));
        QueryWrapper<ProductInfo> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("proname", keyword.trim()).or().like("supname", keyword.trim()));
        }
        if (categoryId != null && categoryId > 0) {
            wrapper.eq("category_id", categoryId);
        }
        if (publishStatus != null) {
            wrapper.eq("publish_status", publishStatus);
        }
        if (auditStatus != null) {
            wrapper.eq("audit_status", auditStatus);
        }
        wrapper.orderByDesc("create_time").orderByDesc("id");
        return Result.ok(productInfoService.page(page, wrapper));
    }

    @ApiOperation("商品详情")
    @GetMapping("{id}")
    public Result detail(@PathVariable Long id) {
        return Result.ok(requireProduct(id));
    }

    @ApiOperation("新增商品")
    @PostMapping("save")
    public Result save(@Valid @RequestBody ProductSaveRequest request) {
        ProductInfo product = new ProductInfo();
        applyRequest(product, request);
        product.setSales(0);
        product.setIsDelete(0);
        productInfoService.save(product);
        return Result.ok(product);
    }

    @ApiOperation("更新商品")
    @PostMapping("update/{id}")
    public Result update(@PathVariable Long id, @Valid @RequestBody ProductSaveRequest request) {
        ProductInfo product = requireProduct(id);
        applyRequest(product, request);
        productInfoService.updateById(product);
        return Result.ok(product);
    }

    @ApiOperation("删除商品")
    @DeleteMapping("{id}")
    public Result remove(@PathVariable Long id) {
        requireProduct(id);
        productInfoService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("批量删除商品")
    @PostMapping("batchDelete")
    public Result batchRemove(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new XcfpException(ResultCodeEnum.PARAM_ERROR);
        }
        productInfoService.removeByIds(ids);
        return Result.ok();
    }

    @ApiOperation("上架/下架")
    @PostMapping("{id}/publish")
    public Result publish(@PathVariable Long id, @Valid @RequestBody PublishRequest request) {
        ProductInfo product = requireProduct(id);
        product.setPublishStatus(request.getPublishStatus());
        productInfoService.updateById(product);
        return Result.ok(product);
    }

    @ApiOperation("设置库存")
    @PostMapping("{id}/stock")
    public Result stock(@PathVariable Long id, @Valid @RequestBody StockRequest request) {
        ProductInfo product = requireProduct(id);
        product.setStock(request.getStock());
        productInfoService.updateById(product);
        return Result.ok(product);
    }

    @ApiOperation("审核商品")
    @PostMapping("{id}/audit")
    public Result audit(@PathVariable Long id, @Valid @RequestBody AuditRequest request) {
        ProductInfo product = requireProduct(id);
        product.setAuditStatus(request.getAuditStatus());
        productInfoService.updateById(product);
        return Result.ok(product);
    }

    private ProductInfo requireProduct(Long id) {
        ProductInfo product = productInfoService.getById(id);
        if (product == null) {
            throw new XcfpException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        return product;
    }

    private void applyRequest(ProductInfo product, ProductSaveRequest request) {
        product.setProname(request.getProname().trim());
        product.setSupid(request.getSupid());
        product.setSupname(request.getSupname());
        product.setCategoryId(request.getCategoryId());
        product.setDelivery(request.getDelivery());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setUnit(StringUtils.hasText(request.getUnit()) ? request.getUnit().trim() : "件");
        product.setDescript(request.getDescript());
        product.setPicurl(request.getPicurl());
        product.setPublishStatus(request.getPublishStatus() == null ? 0 : request.getPublishStatus());
        product.setAuditStatus(request.getAuditStatus() == null ? 0 : request.getAuditStatus());
    }
}
