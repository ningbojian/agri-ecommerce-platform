package xcfp.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xcfp.common.result.Result;
import xcfp.model.Orderinfo;
import xcfp.user.service.OrderService;
import xcfp.vo.OrderInfoVo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author Kerr
 * @create 2021-10-27 1:55
 */
@Api(value = "订单")
@RestController
@RequestMapping("/admin/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("save")
    public Result save(@RequestBody Orderinfo order){
        String str= new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Random random = new Random();
        int ran=(int) (random.nextDouble()*(9999-1000+1))+1000;
        order.setOrderid(str+ran);
        boolean b = orderService.save(order);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @PostMapping("findByUserId/{userid}")
    public Result findById(@PathVariable Long userid, @RequestBody (required = false)OrderInfoVo orderInfoVo){
        Page<Orderinfo> orderPage=new Page<>(1,100);
        QueryWrapper<Orderinfo> wrapper=new QueryWrapper<>();
        wrapper.eq("userid",userid);
        if(!StringUtils.isEmpty(orderInfoVo.getState())){
            wrapper.eq("state",orderInfoVo.getState());
        }
        Page<Orderinfo> page = orderService.page(orderPage, wrapper);
        return Result.ok(page);
    }
}
