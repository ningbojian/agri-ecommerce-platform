package xcfp.proinfo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xcfp.model.ProductCategory;
import xcfp.proinfo.mapper.ProductCategoryMapper;
import xcfp.proinfo.service.ProductCategoryService;

@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory>
        implements ProductCategoryService {
}
