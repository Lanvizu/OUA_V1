package OUA.OUA_V1.global;

import OUA.OUA_V1.advice.UnauthorizedException;
import OUA.OUA_V1.auth.annotation.RequireAuthCheck;
import OUA.OUA_V1.auth.service.AuthService;
import OUA.OUA_V1.global.util.CookieManager;
import OUA.OUA_V1.member.domain.MemberRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

@RequiredArgsConstructor
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;
    private final CookieManager cookieManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Objects.requireNonNull(parameter.getMethod()).isAnnotationPresent(RequireAuthCheck.class);
    }

    @Override
    public LoginProfile resolveArgument(MethodParameter parameter,
                                        ModelAndViewContainer mavContainer,
                                        NativeWebRequest webRequest,
                                        WebDataBinderFactory binderFactory
    ) throws UnauthorizedException {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String token = cookieManager.extractToken(request);
        Long memberIdPayload = authService.extractMemberId(token);
        MemberRole memberRolePayload = MemberRole.valueOf(authService.extractMemberRole(token));

        return new LoginProfile(memberIdPayload, memberRolePayload);
    }
}
