package xcfp.proinfo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xcfp.model.ProductInfo;
import xcfp.proinfo.mapper.ProductInfoMapper;
import xcfp.proinfo.service.ProductInfoService;

/**
 * @author Kerr
 * @create 2021-10-13 19:22
 */
@Service
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo> implements ProductInfoService {

}
