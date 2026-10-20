package xcfp.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xcfp.common.auth.JwtUtil;
import xcfp.common.auth.PasswordUtil;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.AdminUser;
import xcfp.model.User;
import xcfp.user.mapper.AdminUserMapper;
import xcfp.user.mapper.UserMapper;
import xcfp.vo.auth.AdminLoginRequest;
import xcfp.vo.auth.AdminLoginVO;
import xcfp.vo.auth.LoginRequest;
import xcfp.vo.auth.LoginVO;
import xcfp.vo.auth.RegisterRequest;
import xcfp.vo.auth.UserVO;

import java.time.LocalDateTime;

/**
 * 注册、用户登录、管理员登录。JWT 由用户服务统一签发。
 */
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional(rollbackFor = Exception.class)
    public LoginVO register(RegisterRequest request) {
        Integer exists = userMapper.selectCount(new QueryWrapper<User>().eq("tel", request.getTel()));
        if (exists != null && exists > 0) {
            throw new XcfpException(ResultCodeEnum.REGISTER_MOBLE_ERROR);
        }

        User user = new User();
        user.setTel(request.getTel());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setNickname(StringUtils.hasText(request.getNickname())
                ? request.getNickname().trim()
                : "用户" + request.getTel().substring(request.getTel().length() - 4));
        user.setStatus(1);
        user.setIsDelete(0);
        userMapper.insert(user);

        return new LoginVO(jwtUtil.createUserToken(user.getId(), user.getTel()), UserVO.from(user));
    }

    public LoginVO login(LoginRequest request) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("tel", request.getTel()));
        if (user == null || !PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new XcfpException(ResultCodeEnum.ACCOUNT_OR_PASSWORD_ERROR);
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new XcfpException(ResultCodeEnum.ACCOUNT_DISABLED);
        }

        // 旧数据是明文密码时，登录成功后立即升级为 BCrypt
        if (!PasswordUtil.isBcrypt(user.getPassword())) {
            user.setPassword(PasswordUtil.encode(request.getPassword()));
            userMapper.updateById(user);
        }

        return new LoginVO(jwtUtil.createUserToken(user.getId(), user.getTel()), UserVO.from(user));
    }

    public AdminLoginVO adminLogin(AdminLoginRequest request) {
        AdminUser admin = adminUserMapper.selectOne(
                new QueryWrapper<AdminUser>().eq("username", request.getUsername()));
        if (admin == null || !PasswordUtil.matches(request.getPassword(), admin.getPassword())) {
            throw new XcfpException("用户名或密码错误", ResultCodeEnum.ACCOUNT_OR_PASSWORD_ERROR.getCode());
        }
        if (admin.getStatus() == null || admin.getStatus() != 1) {
            throw new XcfpException(ResultCodeEnum.ACCOUNT_DISABLED);
        }

        if (!PasswordUtil.isBcrypt(admin.getPassword())) {
            admin.setPassword(PasswordUtil.encode(request.getPassword()));
        }
        admin.setLastLoginTime(LocalDateTime.now());
        adminUserMapper.updateById(admin);

        return new AdminLoginVO(jwtUtil.createAdminToken(admin.getId(), admin.getUsername()), admin);
    }
}
