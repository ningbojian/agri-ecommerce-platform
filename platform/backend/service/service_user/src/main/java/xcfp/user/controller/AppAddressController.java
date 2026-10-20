package xcfp.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.auth.UserContext;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.Result;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.UserAddress;
import xcfp.user.service.UserAddressService;
import xcfp.vo.address.AddressRequest;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "小程序收货地址")
@RestController
@RequestMapping("/app/address")
public class AppAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @ApiOperation("地址列表")
    @GetMapping("/list")
    public Result<List<UserAddress>> list() {
        return Result.ok(userAddressService.listByUser(UserContext.getUserId()));
    }

    @ApiOperation("默认地址")
    @GetMapping("/default")
    public Result<UserAddress> defaultAddress() {
        return Result.ok(userAddressService.getDefault(UserContext.getUserId()));
    }

    @ApiOperation("地址详情")
    @GetMapping("/{id}")
    public Result<UserAddress> detail(@PathVariable Long id) {
        return Result.ok(userAddressService.getOwned(id, UserContext.getUserId()));
    }

    @ApiOperation("新增地址")
    @PostMapping("/save")
    public Result<UserAddress> save(@Valid @RequestBody AddressRequest request) {
        return Result.ok(userAddressService.create(UserContext.getUserId(), request));
    }

    @ApiOperation("编辑地址")
    @PostMapping("/update")
    public Result<UserAddress> update(@Valid @RequestBody AddressRequest request) {
        if (request.getId() == null) {
            throw new XcfpException(ResultCodeEnum.PARAM_ERROR);
        }
        return Result.ok(userAddressService.updateAddress(
                UserContext.getUserId(), request.getId(), request));
    }

    @ApiOperation("删除地址")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userAddressService.deleteAddress(UserContext.getUserId(), id);
        return Result.ok();
    }

    @ApiOperation("设为默认地址")
    @PostMapping("/{id}/default")
    public Result<?> setDefault(@PathVariable Long id) {
        userAddressService.setDefault(UserContext.getUserId(), id);
        return Result.ok();
    }
}
