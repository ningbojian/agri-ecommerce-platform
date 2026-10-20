package xcfp.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.UserAddress;
import xcfp.user.mapper.UserAddressMapper;
import xcfp.user.service.UserAddressService;
import xcfp.vo.address.AddressRequest;

import java.util.List;

/**
 * @author Kerr
 * @create 2021-10-26 23:53
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress>
        implements UserAddressService {

    @Override
    public List<UserAddress> listByUser(Long userId) {
        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("userid", userId).orderByDesc("is_default").orderByDesc("id");
        return list(wrapper);
    }

    @Override
    public UserAddress getOwned(Long id, Long userId) {
        UserAddress address = getById(id);
        if (address == null) {
            throw new XcfpException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        if (userId == null || !userId.equals(address.getUserid())) {
            throw new XcfpException(ResultCodeEnum.PERMISSION);
        }
        return address;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddress create(Long userId, AddressRequest request) {
        boolean firstAddress = count(new QueryWrapper<UserAddress>().eq("userid", userId)) == 0;
        boolean makeDefault = firstAddress || Boolean.TRUE.equals(request.getIsDefault());
        if (makeDefault) {
            clearDefault(userId);
        }

        UserAddress address = new UserAddress();
        address.setUserid(userId);
        applyRequest(address, request);
        address.setIsDefault(makeDefault ? 1 : 0);
        address.setIsDelete(0);
        save(address);
        return address;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddress updateAddress(Long userId, Long id, AddressRequest request) {
        UserAddress address = getOwned(id, userId);
        boolean makeDefault = Boolean.TRUE.equals(request.getIsDefault());
        if (makeDefault) {
            clearDefault(userId);
            address.setIsDefault(1);
        }
        applyRequest(address, request);
        updateById(address);
        return address;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long userId, Long id) {
        UserAddress address = getOwned(id, userId);
        boolean wasDefault = address.getIsDefault() != null && address.getIsDefault() == 1;
        removeById(id);

        if (wasDefault) {
            List<UserAddress> remaining = listByUser(userId);
            if (!remaining.isEmpty()) {
                UserAddress next = remaining.get(0);
                next.setIsDefault(1);
                updateById(next);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long userId, Long id) {
        UserAddress address = getOwned(id, userId);
        clearDefault(userId);
        address.setIsDefault(1);
        updateById(address);
    }

    @Override
    public UserAddress getDefault(Long userId) {
        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("userid", userId).eq("is_default", 1).orderByAsc("id").last("LIMIT 1");
        return getOne(wrapper);
    }

    private void clearDefault(Long userId) {
        UpdateWrapper<UserAddress> wrapper = new UpdateWrapper<>();
        wrapper.eq("userid", userId).eq("is_default", 1).set("is_default", 0);
        update(new UserAddress(), wrapper);
    }

    private void applyRequest(UserAddress address, AddressRequest request) {
        address.setAddname(request.getAddname().trim());
        address.setAddtel(request.getAddtel().trim());
        address.setAddress(request.getAddress().trim());
    }
}
