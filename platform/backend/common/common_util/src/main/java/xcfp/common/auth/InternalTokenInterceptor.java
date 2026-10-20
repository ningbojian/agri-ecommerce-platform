package xcfp.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import xcfp.common.result.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 服务间内部接口鉴权，校验 X-Internal-Token，不走 JWT。
 */
@Component
public class InternalTokenInterceptor implements HandlerInterceptor {

    private static final String HEADER = "X-Internal-Token";

    @Value("${xcfp.internal-token:xcfp-local-internal-token}")
    private String internalToken;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader(HEADER);
        if (internalToken == null || internalToken.isEmpty() || !internalToken.equals(token)) {
            writeJson(response);
            return false;
        }
        return true;
    }

    private void writeJson(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        new ObjectMapper().writeValue(response.getWriter(), Result.build(208, "无效的内部调用令牌"));
    }
}
