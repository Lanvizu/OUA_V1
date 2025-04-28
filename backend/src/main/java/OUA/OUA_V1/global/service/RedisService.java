package OUA.OUA_V1.global.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String EMAIL_VERIFICATION_PREFIX = "email:verification:";
    private static final String EMAIL_LAST_SENT_PREFIX = "email:lastSent:";

    /**
     * Store verification code with expiration time
     */
    public void setVerificationCode(String email, String code, long expirationTimeMillis) {
        String key = EMAIL_VERIFICATION_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, Duration.ofMillis(expirationTimeMillis));
        log.info("Verification code saved for email: {}", email);
    }

    /**
     * Get verification code for email
     */
    public String getVerificationCode(String email) {
        String key = EMAIL_VERIFICATION_PREFIX + email;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Record last sent time to implement rate limiting
     */
    public void setLastSentTime(String email) {
        String key = EMAIL_LAST_SENT_PREFIX + email;
        redisTemplate.opsForValue().set(key, LocalDateTime.now().toString(), Duration.ofMinutes(1));
    }

    /**
     * Check if email was recently sent (within rate limit)
     */
    public boolean wasRecentlySent(String email) {
        String key = EMAIL_LAST_SENT_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Delete verification code
     */
    public void deleteVerificationCode(String email) {
        String key = EMAIL_VERIFICATION_PREFIX + email;
        redisTemplate.delete(key);
    }

    // 락 획득 시도
    public String tryLock(String key, long expireMillis) {
        String lockValue = UUID.randomUUID().toString();
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, lockValue, expireMillis, TimeUnit.MILLISECONDS);
        return Boolean.TRUE.equals(success) ? lockValue : null;
    }

    // 락 해제
    public void releaseLock(String key, String lockValue) {
        String currentValue = redisTemplate.opsForValue().get(key);
        if (lockValue.equals(currentValue)) {
            redisTemplate.delete(key);
        }
    }
}
