package OUA.OUA_V1.global;

import OUA.OUA_V1.auth.exception.ConcurrentAccessException;
import OUA.OUA_V1.global.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisLockTemplate {
    private static final long DEFAULT_EXPIRE_MILLIS = 3000;
    private final RedisService redisService;
    private static final String LOCK_KEY_PREFIX = "product:lock:";

    public <T> T executeWithLock(
            Long productId,
            Supplier<T> action
    ) {
        String lockKey = LOCK_KEY_PREFIX + productId;
        String lockValue = redisService.tryLock(lockKey, DEFAULT_EXPIRE_MILLIS);

        if (lockValue == null) {
            throw new ConcurrentAccessException();
        }

        try {
            return action.get();
        } finally {
            redisService.releaseLock(lockKey, lockValue);
            log.debug("Released lock for key: {}", lockKey);
        }
    }

    public void executeWithLock(
            Long productId,
            Runnable action
    ) {
        executeWithLock(productId, () -> {
            action.run();
            return null;
        });
    }
}
