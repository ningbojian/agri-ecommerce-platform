package xcfp.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.auth.UserContext;
import xcfp.common.result.Result;
import xcfp.model.Orderinfo;
import xcfp.user.service.OrderService;
import xcfp.vo.order.OrderCreateRequest;

import javax.validation.Valid;

@Api(tags = "小程序订单")
@RestController
@RequestMapping("/app/order")
public class AppOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping("/create")
    public Result<Orderinfo> create(@Valid @RequestBody OrderCreateRequest request) {
        return Result.ok(orderService.createOrder(UserContext.getUserId(), request));
    }

    @ApiOperation("我的订单分页")
    @GetMapping("/page")
    public Result<Page<Orderinfo>> page(@RequestParam(defaultValue = "1") long current,
                                        @RequestParam(defaultValue = "10") long limit,
                                        @RequestParam(required = false) String state) {
        return Result.ok(orderService.pageForUser(
                UserContext.getUserId(), current, limit, state));
    }

    @ApiOperation("订单详情")
    @GetMapping("/{orderid}")
    public Result<Orderinfo> detail(@PathVariable String orderid) {
        return Result.ok(orderService.getUserOrder(UserContext.getUserId(), orderid));
    }

    @ApiOperation("模拟支付")
    @PostMapping("/{orderid}/pay")
    public Result<Orderinfo> pay(@PathVariable String orderid) {
        return Result.ok(orderService.pay(UserContext.getUserId(), orderid));
    }

    @ApiOperation("取消订单")
    @PostMapping("/{orderid}/cancel")
    public Result<Orderinfo> cancel(@PathVariable String orderid) {
        return Result.ok(orderService.cancel(UserContext.getUserId(), orderid));
    }

    @ApiOperation("确认收货")
    @PostMapping("/{orderid}/confirm")
    public Result<Orderinfo> confirm(@PathVariable String orderid) {
        return Result.ok(orderService.confirm(UserContext.getUserId(), orderid));
    }
}
