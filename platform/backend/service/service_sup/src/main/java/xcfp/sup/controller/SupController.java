package xcfp.sup.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.result.Result;
import xcfp.model.Supinfo;
import xcfp.sup.service.SupService;
import xcfp.vo.sup.AuditStatusRequest;
import xcfp.vo.sup.StatusRequest;
import xcfp.vo.sup.SupplierSaveRequest;

import javax.validation.Valid;

/**
 * 供应商管理（仅管理端）。密码、签名密钥、历史码均不返回前端。
 *
 * @author Kerr
 * @create 2021-10-17 0:36
 */
@RestController
@Api(tags = "管理端供应商")
@RequestMapping("/admin/sup")
public class SupController {

    @Autowired
    private SupService supService;

    @ApiOperation("供应商分页")
    @GetMapping("/page")
    public Result<Page<Supinfo>> page(@RequestParam(defaultValue = "1") long current,
                                      @RequestParam(defaultValue = "10") long limit,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Integer auditStatus,
                                      @RequestParam(required = false) Integer status) {
        return Result.ok(supService.pageSuppliers(current, limit, keyword, auditStatus, status));
    }

    @ApiOperation("供应商详情")
    @GetMapping("/{id}")
    public Result<Supinfo> detail(@PathVariable Long id) {
        return Result.ok(supService.getRequired(id));
    }

    @ApiOperation("新增供应商")
    @PostMapping("/save")
    public Result<Supinfo> save(@Valid @RequestBody SupplierSaveRequest request) {
        return Result.ok(supService.createSupplier(request));
    }

    @ApiOperation("编辑供应商")
    @PostMapping("/update")
    public Result<Supinfo> update(@Valid @RequestBody SupplierSaveRequest request) {
        return Result.ok(supService.updateSupplier(request));
    }

    @ApiOperation("删除供应商")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        supService.deleteSupplier(id);
        return Result.ok();
    }

    @ApiOperation("审核供应商")
    @PostMapping("/{id}/audit")
    public Result<Supinfo> audit(@PathVariable Long id,
                                 @Valid @RequestBody AuditStatusRequest request) {
        return Result.ok(supService.audit(id, request));
    }

    @ApiOperation("启用/停用供应商")
    @PostMapping("/{id}/status")
    public Result<Supinfo> status(@PathVariable Long id,
                                  @Valid @RequestBody StatusRequest request) {
        return Result.ok(supService.changeStatus(id, request));
    }
}
