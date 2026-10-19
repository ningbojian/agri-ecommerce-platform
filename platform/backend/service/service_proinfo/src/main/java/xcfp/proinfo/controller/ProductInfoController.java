package xcfp.proinfo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xcfp.common.result.Result;
import xcfp.model.ProductInfo;
import xcfp.proinfo.service.ProductInfoService;
import xcfp.vo.ProductionInfoQueryVo;

import com.alibaba.excel.util.StringUtils;
import java.util.List;

/**
 * @author Kerr
 * @create 2021-10-13 19:26
 */
@Api(tags = "商品设置")
@RestController
@RequestMapping("/admin/pro/productinfo")
public class ProductInfoController {
    @Autowired
    private ProductInfoService productInfoService;
    @ApiOperation(value = "查找所有商品")
    @GetMapping("findAll")
    public Result findAll(){
        List<ProductInfo> list = productInfoService.list();
        return Result.ok(list);
    }
    @DeleteMapping("removeById/{id}")
    public Result removeById(@PathVariable Long id){
        boolean flag = productInfoService.removeById(id);
        if(flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @GetMapping("getById/{id}")
    public Result getById(@PathVariable Long id){
        ProductInfo byId = productInfoService.getById(id);
        return Result.ok(byId);
    }
    @ApiOperation(value = "条件查询带分页")
    @PostMapping("findPage/{current}/{limit}")
    public Result findPageProInfo(@PathVariable Long current, @PathVariable Long limit,
                                  @RequestBody(required = false) ProductionInfoQueryVo productionInfoQueryVo){
        Page<ProductInfo> ProInfopage=new Page<>(current,limit);
        QueryWrapper<ProductInfo> wrapper=new QueryWrapper<>();
        if(!StringUtils.isEmpty(productionInfoQueryVo.getProname())){
            wrapper.like("proname",productionInfoQueryVo.getProname());
        }
        if(!StringUtils.isEmpty(productionInfoQueryVo.getSupid())){
            wrapper.eq("supid",productionInfoQueryVo.getSupid());
        }
        if(!StringUtils.isEmpty(productionInfoQueryVo.getSupname())){
            wrapper.like("supname",productionInfoQueryVo.getSupname());
        }
        if(!StringUtils.isEmpty(productionInfoQueryVo.getDelivery())){
            wrapper.like("delivery",productionInfoQueryVo.getDelivery());
        }
        if(!StringUtils.isEmpty(productionInfoQueryVo.getMax())&&!StringUtils.isEmpty(productionInfoQueryVo.getMin())){
            wrapper.between("price",productionInfoQueryVo.getMin(),productionInfoQueryVo.getMax());
        }
        if (!StringUtils.isEmpty(productionInfoQueryVo.getSort())){
            wrapper.eq("sort",productionInfoQueryVo.getSort());
        }
        Page<ProductInfo> page = productInfoService.page(ProInfopage, wrapper);
        return Result.ok(page);
    }
    @ApiOperation(value = "添改")
    @PostMapping("updatePro")
    public Result updatePro(@RequestBody ProductInfo productInfo){
        boolean flag;
        ProductInfo byId = productInfoService.getById(productInfo.getId());
        if(byId==null){
            flag = productInfoService.save(productInfo);
        }else {
            flag = productInfoService.updateById(productInfo);
        }
        if(flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @ApiOperation(value = "批量删除")
    @DeleteMapping("batchRemovePro")
    public Result batchRemovePro(@RequestBody List<Long> idlist){
        boolean flag = productInfoService.removeByIds(idlist);
        if(flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
}