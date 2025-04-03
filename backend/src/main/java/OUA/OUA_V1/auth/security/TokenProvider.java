package OUA.OUA_V1.auth.security;

import OUA.OUA_V1.auth.exception.IllegalTokenException;

import java.util.Map;

public interface TokenProvider {
    // 클레임 정보를 받아 토큰을 생성
    String createToken(Map<String, Object> claims);

    // 토큰의 유효기간이 살아있는지 확인
    boolean isAlive(String token) throws IllegalTokenException;

    // 토큰에서 지정한 키의 클레임 값을 추출
    String extractClaim(String token, String key) throws IllegalTokenException;
}
