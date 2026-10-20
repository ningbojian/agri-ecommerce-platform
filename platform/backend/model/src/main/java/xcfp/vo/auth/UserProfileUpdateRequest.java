package xcfp.vo.auth;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UserProfileUpdateRequest {
    @Size(max = 64, message = "昵称不能超过 64 个字符")
    private String nickname;

    @Size(max = 512, message = "头像地址过长")
    private String avatar;
}
