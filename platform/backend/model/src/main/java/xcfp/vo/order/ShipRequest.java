package xcfp.vo.order;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ShipRequest {
    @NotBlank(message = "快递信息不能为空")
    @Size(max = 128, message = "快递信息过长")
    private String express;
}
