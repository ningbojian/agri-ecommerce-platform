package xcfp.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.auth.UserContext;
import xcfp.common.result.Result;
import xcfp.user.service.UserService;
import xcfp.vo.auth.PasswordUpdateRequest;
import xcfp.vo.auth.UserProfileUpdateRequest;
import xcfp.vo.auth.UserVO;

import javax.validation.Valid;

@Api(tags = "小程序用户")
@RestController
@RequestMapping("/app/user")
public class AppUserController {

    @Autowired
    private UserService userService;

    @ApiOperation("当前用户信息")
    @GetMapping("/profile")
    public Result<UserVO> profile() {
        return Result.ok(userService.getProfile(UserContext.getUserId()));
    }

    @ApiOperation("修改昵称/头像")
    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        return Result.ok(userService.updateProfile(UserContext.getUserId(), request));
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<?> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(UserContext.getUserId(), request);
        return Result.ok();
    }
}
