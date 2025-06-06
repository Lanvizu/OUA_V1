package OUA.OUA_V1.global;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ProductLockManager {

    private final ConcurrentHashMap<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

    // 락 가져오기 (없으면 생성)
    public ReentrantLock getLock(Long productId) {
        return locks.computeIfAbsent(productId, id -> new ReentrantLock());
    }
}