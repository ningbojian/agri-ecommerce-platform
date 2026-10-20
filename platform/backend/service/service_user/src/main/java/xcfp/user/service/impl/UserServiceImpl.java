package xcfp.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xcfp.common.auth.PasswordUtil;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.User;
import xcfp.user.mapper.UserMapper;
import xcfp.user.service.UserService;
import xcfp.vo.auth.PasswordUpdateRequest;
import xcfp.vo.auth.UserProfileUpdateRequest;
import xcfp.vo.auth.UserVO;

/**
 * @author Kerr
 * @create 2021-10-16 22:57
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public UserVO getProfile(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new XcfpException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        return UserVO.from(user);
    }

    @Override
    public UserVO updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = getById(userId);
        if (user == null) {
            throw new XcfpException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname().trim());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        updateById(user);
        return UserVO.from(user);
    }

    @Override
    public void updatePassword(Long userId, PasswordUpdateRequest request) {
        User user = getById(userId);
        if (user == null) {
            throw new XcfpException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        if (!PasswordUtil.matches(request.getOldPassword(), user.getPassword())) {
            throw new XcfpException("原密码不正确", ResultCodeEnum.PARAM_ERROR.getCode());
        }
        user.setPassword(PasswordUtil.encode(request.getNewPassword()));
        updateById(user);
    }

    @Override
    public Page<User> pageUsers(long current, long limit, String keyword, Integer status) {
        Page<User> page = new Page<>(Math.max(current, 1), Math.min(Math.max(limit, 1), 50));
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("tel", keyword.trim()).or().like("nickname", keyword.trim()));
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time").orderByDesc("id");
        return page(page, wrapper);
    }
}
