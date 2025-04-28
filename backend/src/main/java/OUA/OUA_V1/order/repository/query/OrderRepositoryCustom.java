package OUA.OUA_V1.order.repository.query;

import OUA.OUA_V1.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepositoryCustom {

    Page<Order> findAllByMemberId(Long memberId, Pageable pageable);
    Page<Order> findAllByProductId(Long productId, Pageable pageable);
    Optional<Order> findByMemberIdAndProductId(Long memberId, Long productId);
    long countByProductId(Long productId);
    Optional<Order> findTopActiveByProductId(Long productId);
}
