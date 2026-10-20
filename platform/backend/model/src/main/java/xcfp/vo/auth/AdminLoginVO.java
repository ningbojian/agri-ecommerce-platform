package xcfp.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xcfp.model.AdminUser;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginVO {
    private String token;
    private AdminUser admin;
}
