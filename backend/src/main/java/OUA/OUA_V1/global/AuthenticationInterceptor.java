package OUA.OUA_V1.global;

import OUA.OUA_V1.auth.exception.IllegalCookieException;
import OUA.OUA_V1.auth.service.AuthService;
import OUA.OUA_V1.auth.exception.LoginUnauthorizedException;
import OUA.OUA_V1.global.util.CookieManager;
import com.google.cloud.storage.HttpMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final AuthService authService;
    private final CookieManager cookieManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (isOptionsRequest(request) || isAuthenticated(request)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    private boolean isOptionsRequest(HttpServletRequest request) {
        return HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod());
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        try {
            String token = cookieManager.extractToken(request);
            return authService.isTokenValid(token);
        } catch (IllegalCookieException e) {
            throw new LoginUnauthorizedException();
        }
    }

}
