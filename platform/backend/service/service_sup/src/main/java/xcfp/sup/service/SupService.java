package xcfp.sup.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import xcfp.model.Supinfo;
import xcfp.vo.sup.AuditStatusRequest;
import xcfp.vo.sup.StatusRequest;
import xcfp.vo.sup.SupplierSaveRequest;

/**
 * @author Kerr
 * @create 2021-10-17 0:33
 */
public interface SupService extends IService<Supinfo> {

    Page<Supinfo> pageSuppliers(long current, long limit, String keyword,
                                Integer auditStatus, Integer status);

    Supinfo getRequired(Long id);

    Supinfo createSupplier(SupplierSaveRequest request);

    Supinfo updateSupplier(SupplierSaveRequest request);

    void deleteSupplier(Long id);

    Supinfo audit(Long id, AuditStatusRequest request);

    Supinfo changeStatus(Long id, StatusRequest request);
}
