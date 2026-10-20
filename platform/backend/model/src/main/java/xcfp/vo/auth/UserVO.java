package xcfp.vo.auth;

import lombok.Data;
import xcfp.model.User;

import java.time.LocalDateTime;

/**
 * 返回给前端的用户信息，不含密码。
 */
@Data
public class UserVO {
    private Long id;
    private String tel;
    private String nickname;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;

    public static UserVO from(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setTel(user.getTel());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
