package xcfp.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xcfp.model.Orderinfo;
import xcfp.user.mapper.OrderMapper;
import xcfp.user.service.OrderService;

/**
 * @author Kerr
 * @create 2021-10-27 1:55
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orderinfo> implements OrderService {
}
