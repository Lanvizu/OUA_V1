package OUA.OUA_V1.order.repository.query;

import OUA.OUA_V1.order.domain.Orders;
import org.springframework.data.domain.Slice;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepositoryCustom {

    Slice<Orders> findByMemberIdWithKeySet(Long memberId, LocalDateTime localDateTime, int size);
    Optional<Orders> findVisibleOrder(Long memberId, Long productId);
    Optional<Orders> findActiveOrder(Long memberId, Long productId);
    Optional<Orders> findTopActiveByProductId(Long productId);
    List<Orders> findActiveOrdersByProductId(Long productId, @Nullable Long excludeOrderId);
}
