package xcfp.common.auth;

/**
 * 请求级用户上下文。业务代码只从这里取用户 id，忽略客户端传入的 userid。
 */
public final class UserContext {

    private static final ThreadLocal<AuthUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(AuthUser user) {
        HOLDER.set(user);
    }

    public static AuthUser get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        AuthUser user = HOLDER.get();
        return user == null ? null : user.getId();
    }

    public static String getRole() {
        AuthUser user = HOLDER.get();
        return user == null ? null : user.getRole();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
