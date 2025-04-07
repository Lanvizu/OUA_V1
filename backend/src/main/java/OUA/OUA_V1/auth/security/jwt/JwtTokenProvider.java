package OUA.OUA_V1.auth.security.jwt;

import OUA.OUA_V1.auth.exception.IllegalTokenException;
import OUA.OUA_V1.auth.security.TokenProperties;
import OUA.OUA_V1.auth.security.TokenProvider;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {

    private final TokenProperties tokenProperties;

    @Override
    public String createToken(Map<String, Object> claims) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenProperties.expireLength());

        return Jwts.builder()
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.valueOf(tokenProperties.algorithm()),
                        tokenProperties.secretKey().getBytes())
                .compact();
    }

    @Override
    public boolean isAlive(String token) throws IllegalTokenException {
        Claims claims = extractClaims(token);
        Date expiration = claims.getExpiration();
        return expiration.after(new Date());
    }

    @Override
    public String extractClaim(String token, String key) throws IllegalTokenException {
        Claims claims = extractClaims(token);
        return claims.get(key, String.class);
    }

    private Claims extractClaims(String token) throws IllegalTokenException {
        try {
            return Jwts.parser()
                    .setSigningKey(tokenProperties.secretKey().getBytes())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalTokenException();
        }
    }
}
