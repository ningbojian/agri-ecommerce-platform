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
import xcfp.common.result.Result;
import xcfp.model.Orderinfo;
import xcfp.user.service.OrderService;
import xcfp.vo.order.ShipRequest;

import javax.validation.Valid;

@Api(tags = "管理端订单")
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("订单分页")
    @GetMapping("/page")
    public Result<Page<Orderinfo>> page(@RequestParam(defaultValue = "1") long current,
                                        @RequestParam(defaultValue = "10") long limit,
                                        @RequestParam(required = false) String state,
                                        @RequestParam(required = false) String orderid) {
        return Result.ok(orderService.pageForAdmin(current, limit, state, orderid));
    }

    @ApiOperation("订单详情")
    @GetMapping("/{orderid}")
    public Result<Orderinfo> detail(@PathVariable String orderid) {
        return Result.ok(orderService.getAdminOrder(orderid));
    }

    @ApiOperation("发货")
    @PostMapping("/{orderid}/ship")
    public Result<Orderinfo> ship(@PathVariable String orderid,
                                  @Valid @RequestBody ShipRequest request) {
        return Result.ok(orderService.ship(orderid, request));
    }
}
