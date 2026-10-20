package xcfp.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.result.Result;
import xcfp.model.User;
import xcfp.user.service.UserService;

@Api(tags = "管理端用户")
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @ApiOperation("用户分页")
    @GetMapping("/page")
    public Result<Page<User>> page(@RequestParam(defaultValue = "1") long current,
                                   @RequestParam(defaultValue = "10") long limit,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) Integer status) {
        return Result.ok(userService.pageUsers(current, limit, keyword, status));
    }
}
