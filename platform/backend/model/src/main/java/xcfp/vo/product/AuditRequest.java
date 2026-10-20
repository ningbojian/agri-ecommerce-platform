package xcfp.vo.product;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AuditRequest {
    @NotNull(message = "审核状态不能为空")
    @Min(value = 0, message = "审核状态不正确")
    @Max(value = 2, message = "审核状态不正确")
    private Integer auditStatus;
}
