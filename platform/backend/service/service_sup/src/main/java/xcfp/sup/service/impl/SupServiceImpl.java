package xcfp.sup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xcfp.common.auth.PasswordUtil;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.Supinfo;
import xcfp.sup.mapper.SupMapper;
import xcfp.sup.service.SupService;
import xcfp.vo.sup.AuditStatusRequest;
import xcfp.vo.sup.StatusRequest;
import xcfp.vo.sup.SupplierSaveRequest;

import java.security.SecureRandom;

/**
 * @author Kerr
 * @create 2021-10-17 0:34
 */
@Service
public class SupServiceImpl extends ServiceImpl<SupMapper, Supinfo> implements SupService {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public Page<Supinfo> pageSuppliers(long current, long limit, String keyword,
                                       Integer auditStatus, Integer status) {
        Page<Supinfo> page = new Page<>(Math.max(current, 1), Math.min(Math.max(limit, 1), 50));
        QueryWrapper<Supinfo> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("supname", keyword.trim()).or().like("suptel", keyword.trim()));
        }
        if (auditStatus != null) {
            wrapper.eq("audit_status", auditStatus);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time").orderByDesc("id");
        return page(page, wrapper);
    }

    @Override
    public Supinfo getRequired(Long id) {
        Supinfo supinfo = getById(id);
        if (supinfo == null) {
            throw new XcfpException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        return supinfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Supinfo createSupplier(SupplierSaveRequest request) {
        ensureTelAvailable(request.getSuptel(), null);
        if (!StringUtils.hasText(request.getSuppass())) {
            throw new XcfpException("请设置供应商初始密码", ResultCodeEnum.PARAM_ERROR.getCode());
        }

        Supinfo supinfo = new Supinfo();
        applyRequest(supinfo, request);
        supinfo.setSuppass(PasswordUtil.encode(request.getSuppass()));
        supinfo.setSignkey(randomKey());
        supinfo.setAuditStatus(0);
        supinfo.setStatus(1);
        supinfo.setIsDelete(0);
        save(supinfo);
        return supinfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Supinfo updateSupplier(SupplierSaveRequest request) {
        if (request.getId() == null) {
            throw new XcfpException(ResultCodeEnum.PARAM_ERROR);
        }
        Supinfo supinfo = getRequired(request.getId());
        ensureTelAvailable(request.getSuptel(), request.getId());
        applyRequest(supinfo, request);
        if (StringUtils.hasText(request.getSuppass())) {
            supinfo.setSuppass(PasswordUtil.encode(request.getSuppass()));
        }
        updateById(supinfo);
        return supinfo;
    }

    @Override
    public void deleteSupplier(Long id) {
        getRequired(id);
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Supinfo audit(Long id, AuditStatusRequest request) {
        Supinfo supinfo = getRequired(id);
        supinfo.setAuditStatus(request.getAuditStatus());
        updateById(supinfo);
        return supinfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Supinfo changeStatus(Long id, StatusRequest request) {
        Supinfo supinfo = getRequired(id);
        supinfo.setStatus(request.getStatus());
        updateById(supinfo);
        return supinfo;
    }

    private void applyRequest(Supinfo supinfo, SupplierSaveRequest request) {
        supinfo.setSuptel(request.getSuptel().trim());
        supinfo.setSupname(request.getSupname().trim());
        supinfo.setCardnum(request.getCardnum());
        supinfo.setLogo(request.getLogo());
        supinfo.setDescript(request.getDescript());
    }

    private void ensureTelAvailable(String tel, Long excludeId) {
        QueryWrapper<Supinfo> wrapper = new QueryWrapper<>();
        wrapper.eq("suptel", tel.trim());
        if (excludeId != null) {
            wrapper.ne("id", excludeId);
        }
        if (count(wrapper) > 0) {
            throw new XcfpException("供应商电话已被使用", ResultCodeEnum.REGISTER_MOBLE_ERROR.getCode());
        }
    }

    private String randomKey() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
