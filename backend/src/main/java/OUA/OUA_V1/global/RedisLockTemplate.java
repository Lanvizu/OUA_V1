package OUA.OUA_V1.global;

import OUA.OUA_V1.auth.exception.ConcurrentAccessException;
import OUA.OUA_V1.global.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.function.Supplier;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisLockTemplate {
    private static final long DEFAULT_EXPIRE_MILLIS = 5000;
    private final RedisService redisService;
    private static final String LOCK_KEY_PREFIX = "product:lock:";

    public <T> T executeWithLock(Long productId, Supplier<T> action) {
        String lockKey = LOCK_KEY_PREFIX + productId;
        String lockValue = redisService.tryLock(lockKey, DEFAULT_EXPIRE_MILLIS);

        if (lockValue == null) {
//            log.warn("[LOCK FAIL] productId={}, thread={}, timestamp={}", productId, Thread.currentThread().getName(), System.currentTimeMillis());
            throw new ConcurrentAccessException();
        }

//        log.info("[LOCK ACQUIRED] productId={}, lockValue={}, thread={}, timestamp={}", productId, lockValue, Thread.currentThread().getName(), System.currentTimeMillis());

        try {
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        redisService.releaseLock(lockKey, lockValue);
//                        log.info("[LOCK RELEASE afterCommit] productId={}, lockValue={}, thread={}, timestamp={}", productId, lockValue, Thread.currentThread().getName(), System.currentTimeMillis());
                    }
                });
            } else {
                redisService.releaseLock(lockKey, lockValue);
//                log.info("[LOCK RELEASE immediately] productId={}, lockValue={}, thread={}, timestamp={}", productId, lockValue, Thread.currentThread().getName(), System.currentTimeMillis());
            }

            return action.get();

        } catch (RuntimeException e) {
            redisService.releaseLock(lockKey, lockValue);
            log.info("[LOCK RELEASE onError] productId={}, lockValue={}, thread={}, timestamp={}", productId, lockValue, Thread.currentThread().getName(), System.currentTimeMillis());
            throw e;
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
