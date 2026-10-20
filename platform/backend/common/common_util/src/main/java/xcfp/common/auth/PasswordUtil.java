package xcfp.common.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCrypt 密码工具。
 *
 * <p>兼容旧数据：如果库中还是明文密码，{@link #matches(String, String)} 会按明文比较，
 * 登录成功后由调用方立即用 {@link #encode(String)} 回写，完成平滑升级。</p>
 */
public final class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(10);

    private PasswordUtil() {
    }

    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        if (isBcrypt(storedPassword)) {
            try {
                return ENCODER.matches(rawPassword, storedPassword);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return storedPassword.equals(rawPassword);
    }

    public static boolean isBcrypt(String value) {
        return value != null
                && (value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$"));
    }
}
