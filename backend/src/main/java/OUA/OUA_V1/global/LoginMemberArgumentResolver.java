package OUA.OUA_V1.global;

import OUA.OUA_V1.advice.UnauthorizedException;
import OUA.OUA_V1.auth.annotation.LoginMember;
import OUA.OUA_V1.auth.service.AuthService;
import OUA.OUA_V1.global.util.CookieManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;
    private final CookieManager cookieManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory
    )throws UnauthorizedException {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String token = cookieManager.extractToken(request);
        return authService.extractMemberId(token);
    }
}
