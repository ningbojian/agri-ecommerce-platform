package xcfp.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xcfp.common.result.Result;
import xcfp.model.User;
import xcfp.user.service.UserService;

/**
 * @author Kerr
 * @create 2021-10-16 22:59
 */
@Api(value = "用户")
@RestController
@RequestMapping("/admin/user")
public class UserController {
    @Autowired
    private UserService userService;
    @ApiOperation(value = "添加用户tel")
    @PostMapping("saveUser")
    public Result saveUser(@RequestBody User user){
        User byTel = findByTel(user.getTel());
        if(byTel==null){
            userService.save(user);
        }
        return Result.ok();
    }
    @PostMapping("findByTel/{tel}")
    public Result QueByTel(@PathVariable String tel){
        User byTel = findByTel(tel);
        return Result.ok(byTel);
    }
    private User findByTel(String tel){
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("tel",tel);
        User user = userService.getOne(queryWrapper);
        return user;
    }
}
