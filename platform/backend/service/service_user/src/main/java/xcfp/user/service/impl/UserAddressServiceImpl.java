package xcfp.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xcfp.model.UserAddress;
import xcfp.user.mapper.UserAddressMapper;
import xcfp.user.service.UserAddressService;

/**
 * @author Kerr
 * @create 2021-10-26 23:53
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {
}
