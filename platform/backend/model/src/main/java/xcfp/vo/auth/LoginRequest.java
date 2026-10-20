package xcfp.vo.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "手机号不能为空")
    private String tel;

    @NotBlank(message = "密码不能为空")
    private String password;
}
