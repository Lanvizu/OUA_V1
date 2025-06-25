package OUA.OUA_V1.global;

import OUA.OUA_V1.auth.exception.ConcurrentAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class JvmLockTemplate {

    private final ProductLockManager lockManager;

    public <T> T executeWithLock(Long productId, Supplier<T> action) {
        ReentrantLock lock = lockManager.getLock(productId);

        if (!lock.tryLock()) {
            throw new ConcurrentAccessException();
        }

        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }
}