package xcfp.vo;

import io.swagger.annotations.ApiModelProperty;
//import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @author Kerr
 * @create 2021-10-13 22:42
 */
@Data
public class ProductionInfoQueryVo {
    @ApiModelProperty(value = "商品名称")
    private String proname;
    @ApiModelProperty(value = "商家名称")
    private String supname;
    @ApiModelProperty(value = "商家Id")
    private String supid;
    @ApiModelProperty(value = "发货地")
    private String delivery;
    @ApiModelProperty(value = "价格")
    private Double price;
    private String min;
    private String max;
    private String sort;

}
