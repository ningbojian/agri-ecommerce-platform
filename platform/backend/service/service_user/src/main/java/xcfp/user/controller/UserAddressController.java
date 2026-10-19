package xcfp.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xcfp.common.result.Result;
import xcfp.model.UserAddress;
import xcfp.user.service.UserAddressService;

import java.util.Map;

/**
 * @author Kerr
 * @create 2021-10-26 23:54
 */
@Api(value = "地址")
@RestController
@RequestMapping("/admin/address")
public class UserAddressController {
    @Autowired
    private UserAddressService userAddressService;
    @PostMapping("save")
    public Result save(@RequestBody UserAddress userAddress){
        boolean b = userAddressService.save(userAddress);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @DeleteMapping("removeById/{id}")
    public Result removeById(@PathVariable Long id){
        boolean b = userAddressService.removeById(id);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    @PostMapping("update")
    public Result update(@RequestBody UserAddress userAddress){
        UserAddress byId = userAddressService.getById(userAddress.getId());
        if(byId!=null){
            return Result.ok();
        }
        boolean b = userAddressService.updateById(userAddress);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @PostMapping("findByUserId/{userid}/{current}/{limit}")
    public Result QueByTel(@PathVariable Long userid,@PathVariable Long current, @PathVariable Long limit){
        Page<UserAddress> page=new Page<>(current,limit);
        QueryWrapper<UserAddress> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userid",userid);
        Page<UserAddress> page1 = userAddressService.page(page, queryWrapper);
        return Result.ok(page1);
    }
}
