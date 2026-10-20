package xcfp.proinfo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.Result;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.ProductCategory;
import xcfp.model.ProductInfo;
import xcfp.proinfo.service.ProductCategoryService;
import xcfp.proinfo.service.ProductInfoService;
import xcfp.vo.product.CategorySaveRequest;

import javax.validation.Valid;

@Api(tags = "管理端-分类")
@Validated
@RestController
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductInfoService productInfoService;

    @ApiOperation("全部分类")
    @GetMapping("list")
    public Result list() {
        QueryWrapper<ProductCategory> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort_order").orderByAsc("id");
        return Result.ok(productCategoryService.list(wrapper));
    }

    @ApiOperation("新增分类")
    @PostMapping("save")
    public Result save(@Valid @RequestBody CategorySaveRequest request) {
        ensureNameAvailable(request.getName(), null);
        ProductCategory category = new ProductCategory();
        applyRequest(category, request);
        category.setIsDelete(0);
        productCategoryService.save(category);
        return Result.ok(category);
    }

    @ApiOperation("更新分类")
    @PostMapping("update/{id}")
    public Result update(@PathVariable Long id, @Valid @RequestBody CategorySaveRequest request) {
        ProductCategory category = requireCategory(id);
        ensureNameAvailable(request.getName(), id);
        applyRequest(category, request);
        productCategoryService.updateById(category);
        return Result.ok(category);
    }

    @ApiOperation("删除分类")
    @DeleteMapping("{id}")
    public Result remove(@PathVariable Long id) {
        requireCategory(id);
        long count = productInfoService.count(new QueryWrapper<ProductInfo>().eq("category_id", id));
        if (count > 0) {
            throw new XcfpException("该分类下仍有商品，不能删除", ResultCodeEnum.PARAM_ERROR.getCode());
        }
        productCategoryService.removeById(id);
        return Result.ok();
    }

    private ProductCategory requireCategory(Long id) {
        ProductCategory category = productCategoryService.getById(id);
        if (category == null) {
            throw new XcfpException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        return category;
    }

    private void ensureNameAvailable(String name, Long excludeId) {
        QueryWrapper<ProductCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name.trim());
        if (excludeId != null) {
            wrapper.ne("id", excludeId);
        }
        if (productCategoryService.count(wrapper) > 0) {
            throw new XcfpException("分类名称已存在", ResultCodeEnum.PARAM_ERROR.getCode());
        }
    }

    private void applyRequest(ProductCategory category, CategorySaveRequest request) {
        category.setName(request.getName().trim());
        category.setIcon(request.getIcon());
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        category.setStatus(request.getStatus() == null ? 1 : request.getStatus());
    }
}
