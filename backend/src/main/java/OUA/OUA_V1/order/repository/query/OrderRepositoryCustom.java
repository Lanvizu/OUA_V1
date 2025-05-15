package OUA.OUA_V1.order.repository.query;

import OUA.OUA_V1.order.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepositoryCustom {

    Slice<Orders> findByMemberIdWithKeySet(Long memberId, LocalDateTime localDateTime, int size);
    Page<Orders> findAllByProductId(Long productId, Pageable pageable);
    Optional<Orders> findByMemberIdAndProductId(Long memberId, Long productId);
    long countByProductId(Long productId);
    Optional<Orders> findTopActiveByProductId(Long productId);
}
