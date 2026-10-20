package xcfp.vo.address;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AddressRequest {
    /** 编辑时由前端传入，新增时忽略 */
    private Long id;

    @NotBlank(message = "收货人不能为空")
    @Size(max = 64, message = "收货人姓名过长")
    private String addname;

    @NotBlank(message = "联系电话不能为空")
    @Size(max = 20, message = "联系电话过长")
    private String addtel;

    @NotBlank(message = "收货地址不能为空")
    @Size(max = 255, message = "收货地址过长")
    private String address;

    private Boolean isDefault;
}
