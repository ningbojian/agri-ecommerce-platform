package xcfp.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import xcfp.common.result.Result;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * /app/** 与 /admin/** 的 JWT 鉴权。
 *
 * <ul>
 *   <li>未登录/令牌无效：HTTP 401 + code 208</li>
 *   <li>角色不匹配：HTTP 403 + code 209</li>
 *   <li>白名单与 OPTIONS 预检直接放行</li>
 * </ul>
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String[] ALWAYS_ALLOW = {
            "/error", "/favicon.ico",
            "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**",
            "/webjars/**", "/v2/api-docs", "/csrf"
    };

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${xcfp.auth.whitelist:}")
    private String whitelistConfig;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private List<String> whitelist = new ArrayList<>();

    @PostConstruct
    public void init() {
        if (whitelistConfig != null && !whitelistConfig.trim().isEmpty()) {
            whitelist = Arrays.asList(whitelistConfig.split(","));
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }

        if (isAlwaysAllowed(uri) || isWhitelisted(uri)) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, 208, "未登录或登录已过期");
            return false;
        }

        try {
            Claims claims = jwtUtil.parse(header.substring(BEARER_PREFIX.length()).trim());
            Long id = Long.valueOf(claims.getSubject());
            String role = claims.get("role", String.class);
            String identity = claims.get("identity", String.class);
            UserContext.set(new AuthUser(id, role, identity));
        } catch (Exception e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, 208, "未登录或登录已过期");
            return false;
        }

        AuthUser user = UserContext.get();
        if (uri.startsWith("/admin/") && !JwtUtil.ROLE_ADMIN.equals(user.getRole())) {
            writeJson(response, HttpServletResponse.SC_FORBIDDEN, 209, "没有权限");
            return false;
        }
        if (uri.startsWith("/app/") && !JwtUtil.ROLE_USER.equals(user.getRole())) {
            writeJson(response, HttpServletResponse.SC_FORBIDDEN, 209, "没有权限");
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private boolean isAlwaysAllowed(String uri) {
        for (String pattern : ALWAYS_ALLOW) {
            if (pathMatcher.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWhitelisted(String uri) {
        for (String pattern : whitelist) {
            if (pathMatcher.match(pattern.trim(), uri)) {
                return true;
            }
        }
        return false;
    }

    private void writeJson(HttpServletResponse response, int status, int code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        new ObjectMapper().writeValue(response.getWriter(), Result.build(code, message));
    }
}
