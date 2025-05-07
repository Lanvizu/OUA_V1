package OUA.OUA_V1.config;

import OUA.OUA_V1.auth.service.AuthService;
import OUA.OUA_V1.global.AuthenticationInterceptor;
import OUA.OUA_V1.global.LoginArgumentResolver;
import OUA.OUA_V1.global.LoginMemberArgumentResolver;
import OUA.OUA_V1.global.util.CookieManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthService authService;
    private final CookieManager cookieManager;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "https://localhost:3000",
                        "http://frontend:3000"
                )
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(authService, cookieManager))
                .addPathPatterns("/**")
                .excludePathPatterns("/**/members/**")
                .excludePathPatterns("/**/login")
                .excludePathPatterns("/**/products")
                .excludePathPatterns("/**/product/**") // 상품에 대한 접근은 로그인 시 접근할 수 있돌고 변경
                .addPathPatterns("/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginArgumentResolver(authService, cookieManager));
        resolvers.add(new LoginMemberArgumentResolver(authService, cookieManager));
    }
}
