package OUA.OUA_V1.auth.service;

import OUA.OUA_V1.auth.exception.IllegalTokenException;
import OUA.OUA_V1.auth.security.PasswordValidator;
import OUA.OUA_V1.auth.security.TokenProvider;
import OUA.OUA_V1.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private static final String EMAIL_CLAIM = "email";
    private static final String ROLE_CLAIM = "role";

    private final TokenProvider tokenProvider;
    private final PasswordValidator passwordValidator;

    public String createToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(EMAIL_CLAIM, user.getEmail());
        claims.put(ROLE_CLAIM, user.getRole().name());

        return tokenProvider.createToken(claims);
    }

    public boolean isTokenValid(String token) {
        try {
            return tokenProvider.isAlive(token);
        } catch (IllegalTokenException e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return extractClaim(token, EMAIL_CLAIM);
    }

    private String extractClaim(String token, String key) {
        String claim = tokenProvider.extractClaim(token, key);
        if (claim == null) {
            throw new IllegalTokenException();
        }
        return claim;
    }

    public String extractMemberRole(String token) {
        return extractClaim(token, ROLE_CLAIM);
    }

    public boolean isNotVerifiedPassword(String rawPassword, String encodedPassword) {
        return !passwordValidator.matches(rawPassword, encodedPassword);
    }
}