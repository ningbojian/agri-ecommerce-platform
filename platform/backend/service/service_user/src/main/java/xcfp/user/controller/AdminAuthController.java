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
import xcfp.vo.auth.AdminLoginRequest;
import xcfp.vo.auth.AdminLoginVO;

import javax.validation.Valid;

@Api(tags = "管理端认证")
@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    @Autowired
    private AuthService authService;

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@Valid @RequestBody AdminLoginRequest request) {
        return Result.ok(authService.adminLogin(request));
    }
}
