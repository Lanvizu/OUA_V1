package OUA.OUA_V1.global;

import OUA.OUA_V1.auth.exception.ConcurrentAccessException;
import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class JvmLockTemplate {

    //    private static final Logger log = LoggerFactory.getLogger(JvmLockTemplate.class);
//    private static final long DEFAULT_TIMEOUT_MILLIS = 5000;
    private final ProductLockManager lockManager;

    public <T> T executeWithLock(Long productId, Supplier<T> action) {
        ReentrantLock lock = lockManager.getLock(productId);
        boolean acquired = false;

        try {
//        log.info("[LOCK] Trying to acquire lock for productId: {}", productId);
            acquired = lock.tryLock();
            if (!acquired) {
//            log.warn("[LOCK] Failed to acquire lock for productId: {}", productId);
                throw new ConcurrentAccessException();
            }

//        log.info("[LOCK] Lock acquired for productId: {}", productId);

            // 락 해제를 트랜잭션 커밋 후로 지연
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
//            log.info("[TXN] Transaction is active. Registering synchronization for productId: {}", productId);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    private boolean unlocked = false;

                    @Override
                    public void afterCommit() {
//                    log.info("[TXN] Transaction committed. Unlocking for productId: {}", productId);
                        unlockSafely();
                    }

                    @Override
                    public void afterCompletion(int status) {
                        // 트랜잭션 롤백 시 unlock 처리 (누수 방지)
                        if (status != STATUS_COMMITTED) {
//                        log.info("[TXN] Transaction rolled back. Unlocking for productId: {}", productId);
                            unlockSafely();
                        }
                    }

                    private void unlockSafely() {
                        if (!unlocked) {
                            unlocked = true;
                            lock.unlock();
                        }
                    }
                });
            } else {
                // 트랜잭션 없을 경우 즉시 해제
//            log.info("[TXN] No active transaction. Executing and unlocking immediately.");
                return runAndUnlock(action, lock);
            }

            return action.get();
        } catch (RuntimeException e) {
            if (acquired && !TransactionSynchronizationManager.isSynchronizationActive()) {
//            log.info("[LOCK] Exception occurred. Unlocking for productId: {}", productId);
                lock.unlock();
            }
            throw e;
        }
    }

    private <T> T runAndUnlock(Supplier<T> action, ReentrantLock lock) {
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }
}