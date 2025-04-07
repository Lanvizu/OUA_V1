package OUA.OUA_V1.global.util;

import OUA.OUA_V1.auth.exception.IllegalCookieException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CookieManager {

    private final CookieProperties cookieProperties;

    public String extractToken(HttpServletRequest request) {
        Cookie[] cookies = extractCookie(request);
        return Arrays.stream(cookies)
                .filter(this::isAccessTokenCookie)
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(IllegalCookieException::new);
    }

    private Cookie[] extractCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new IllegalCookieException();
        }
        return cookies;
    }

    private boolean isAccessTokenCookie(Cookie cookie) {
        return cookieProperties.accessTokenKey().equals(cookie.getName());
    }

    public ResponseCookie createTokenCookie(String token) {
        return ResponseCookie.from(cookieProperties.accessTokenKey(), token)
                .httpOnly(cookieProperties.httpOnly())
                .secure(cookieProperties.secure())
                .domain(cookieProperties.domain())
                .path(cookieProperties.path())
                .sameSite(cookieProperties.sameSite())
                .maxAge(cookieProperties.maxAge())
                .build();
    }

    public ResponseCookie clearTokenCookie() {
        return ResponseCookie.from(cookieProperties.accessTokenKey())
                .httpOnly(cookieProperties.httpOnly())
                .secure(cookieProperties.secure())
                .domain(cookieProperties.domain())
                .path(cookieProperties.path())
                .sameSite(cookieProperties.sameSite())
                .maxAge(0)
                .build();
    }
}
