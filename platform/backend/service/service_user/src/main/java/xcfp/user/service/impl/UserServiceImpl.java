package xcfp.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xcfp.model.User;
import xcfp.user.mapper.UserMapper;
import xcfp.user.service.UserService;

/**
 * @author Kerr
 * @create 2021-10-16 22:57
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
