package xcfp.vo.sup;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SupplierSaveRequest {
    /** 编辑时由前端传入，新增时忽略 */
    private Long id;

    @NotBlank(message = "供应商电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "供应商电话格式不正确")
    private String suptel;

    @NotBlank(message = "供应商名称不能为空")
    @Size(max = 64, message = "供应商名称过长")
    private String supname;

    @Size(max = 64, message = "证件号过长")
    private String cardnum;

    @Size(max = 512, message = "Logo 地址过长")
    private String logo;

    @Size(max = 500, message = "简介过长")
    private String descript;

    /** 可选；提供时长度至少 6 位，由服务端 BCrypt 加密 */
    private String suppass;
}
