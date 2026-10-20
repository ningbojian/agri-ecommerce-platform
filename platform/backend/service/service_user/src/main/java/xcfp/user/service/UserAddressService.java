package xcfp.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xcfp.model.UserAddress;
import xcfp.vo.address.AddressRequest;

import java.util.List;

/**
 * @author Kerr
 * @create 2021-10-26 23:53
 */
public interface UserAddressService extends IService<UserAddress> {

    List<UserAddress> listByUser(Long userId);

    UserAddress getOwned(Long id, Long userId);

    UserAddress create(Long userId, AddressRequest request);

    UserAddress updateAddress(Long userId, Long id, AddressRequest request);

    void deleteAddress(Long userId, Long id);

    void setDefault(Long userId, Long id);

    UserAddress getDefault(Long userId);
}
