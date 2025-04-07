package OUA.OUA_V1.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("security.jwt.token")
public record TokenProperties(
        String secretKey, Long expireLength, String algorithm
) {
}
