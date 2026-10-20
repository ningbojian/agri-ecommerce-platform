package xcfp.vo.product;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class ProductSaveRequest {
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 128, message = "商品名称过长")
    private String proname;

    @NotNull(message = "供应商不能为空")
    private Long supid;

    @Size(max = 64, message = "供应商名称过长")
    private String supname;

    @NotNull(message = "商品分类不能为空")
    private Long categoryId;

    @Size(max = 64, message = "发货地信息过长")
    private String delivery;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.00", message = "价格不能为负数")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    @Size(max = 16, message = "单位过长")
    private String unit;

    private String descript;

    @Size(max = 512, message = "图片地址过长")
    private String picurl;

    private Integer publishStatus;

    private Integer auditStatus;
}
