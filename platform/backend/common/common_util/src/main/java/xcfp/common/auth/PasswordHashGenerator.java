package xcfp.common.auth;

/**
 * 生成 seed.sql 使用的 BCrypt 哈希。
 *
 * <pre>
 * java -cp common_util/target/classes:&lt;spring-security-crypto jar&gt; \
 *      xcfp.common.auth.PasswordHashGenerator demo123
 * </pre>
 */
public final class PasswordHashGenerator {

    private PasswordHashGenerator() {
    }

    public static void main(String[] args) {
        String raw = args.length > 0 ? args[0] : "demo123";
        System.out.println(raw + " -> " + PasswordUtil.encode(raw));
    }
}
