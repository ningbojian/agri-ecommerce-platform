package xcfp.common.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前请求的登录身份，由 AuthInterceptor 从 JWT 解析后放入 {@link UserContext}。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
    private Long id;
    private String role;
    private String identity;
}
