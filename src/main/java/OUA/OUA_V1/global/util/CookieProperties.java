package OUA.OUA_V1.global.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cookie")
public record CookieProperties(
        String accessTokenKey,
        boolean httpOnly,
        boolean secure,
        String domain,
        String path,
        String sameSite,
        Long maxAge
) {

}
