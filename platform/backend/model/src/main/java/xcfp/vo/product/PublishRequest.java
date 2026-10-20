package xcfp.vo.product;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class PublishRequest {
    @NotNull(message = "上架状态不能为空")
    @Min(value = 0, message = "上架状态不正确")
    @Max(value = 1, message = "上架状态不正确")
    private Integer publishStatus;
}
