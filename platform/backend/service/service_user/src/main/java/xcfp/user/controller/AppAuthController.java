package xcfp.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.result.Result;
import xcfp.user.service.AuthService;
import xcfp.vo.auth.LoginRequest;
import xcfp.vo.auth.LoginVO;
import xcfp.vo.auth.RegisterRequest;

import javax.validation.Valid;

@Api(tags = "小程序认证")
@RestController
@RequestMapping("/app/auth")
public class AppAuthController {

    @Autowired
    private AuthService authService;

    @ApiOperation("注册并返回登录态")
    @PostMapping("/register")
    public Result<LoginVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.ok(authService.register(request));
    }

    @ApiOperation("登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }
}
