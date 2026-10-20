package xcfp.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import xcfp.model.User;
import xcfp.vo.auth.PasswordUpdateRequest;
import xcfp.vo.auth.UserProfileUpdateRequest;
import xcfp.vo.auth.UserVO;

/**
 * @author Kerr
 * @create 2021-10-16 22:56
 */
public interface UserService extends IService<User> {

    UserVO getProfile(Long userId);

    UserVO updateProfile(Long userId, UserProfileUpdateRequest request);

    void updatePassword(Long userId, PasswordUpdateRequest request);

    Page<User> pageUsers(long current, long limit, String keyword, Integer status);
}
