package xcfp.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 签发与验签。三个服务共享同一个 xcfp.jwt.secret。
 */
@Component
public class JwtUtil {

    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    @Value("${xcfp.jwt.secret:xcfp-local-dev-secret-please-change-0123456789}")
    private String secret;

    @Value("${xcfp.jwt.user-expire-seconds:604800}")
    private long userExpireSeconds;

    @Value("${xcfp.jwt.admin-expire-seconds:43200}")
    private long adminExpireSeconds;

    public String createUserToken(Long userId, String identity) {
        return createToken(userId, ROLE_USER, identity, userExpireSeconds);
    }

    public String createAdminToken(Long adminId, String identity) {
        return createToken(adminId, ROLE_ADMIN, identity, adminExpireSeconds);
    }

    private String createToken(Long id, String role, String identity, long expireSeconds) {
        Date now = new Date();
        return Jwts.builder()
                .setIssuer("xcfp")
                .setSubject(String.valueOf(id))
                .claim("role", role)
                .claim("identity", identity)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireSeconds * 1000L))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }
}
