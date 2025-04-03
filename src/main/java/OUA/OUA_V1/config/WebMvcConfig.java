package OUA.OUA_V1.config;

import OUA.OUA_V1.auth.service.AuthService;
import OUA.OUA_V1.global.AuthenticationInterceptor;
import OUA.OUA_V1.global.LoginArgumentResolver;
import OUA.OUA_V1.global.util.CookieManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthService authService;
    private final CookieManager cookieManager;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(authService, cookieManager))
                .addPathPatterns("/**")
                .excludePathPatterns("/**/members/**")
                .excludePathPatterns("/**/login")
                .excludePathPatterns("/**/products")
                .excludePathPatterns("/**/product/**")
                .addPathPatterns("/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginArgumentResolver(authService, cookieManager));
    }
}
