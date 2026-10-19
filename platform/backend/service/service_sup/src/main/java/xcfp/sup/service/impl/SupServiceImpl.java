package xcfp.sup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xcfp.model.Supinfo;
import xcfp.sup.mapper.SupMapper;
import xcfp.sup.service.SupService;

/**
 * @author Kerr
 * @create 2021-10-17 0:34
 */
@Service
public class SupServiceImpl extends ServiceImpl<SupMapper, Supinfo> implements SupService {
}
