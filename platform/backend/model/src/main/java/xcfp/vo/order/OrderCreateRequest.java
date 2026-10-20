package xcfp.vo.order;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class OrderCreateRequest {
    @NotNull(message = "商品不能为空")
    @Min(value = 1, message = "商品不正确")
    private Long productId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为 1")
    @Max(value = 99, message = "单次最多购买 99 件")
    private Integer num;

    @NotNull(message = "收货地址不能为空")
    @Min(value = 1, message = "收货地址不正确")
    private Long addressId;
}
