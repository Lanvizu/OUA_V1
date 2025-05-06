package OUA.OUA_V1.order.repository.query;

import OUA.OUA_V1.order.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepositoryCustom {

    Page<Orders> findAllByMemberId(Long memberId, Pageable pageable);
    Page<Orders> findAllByProductId(Long productId, Pageable pageable);
    Optional<Orders> findByMemberIdAndProductId(Long memberId, Long productId);
    long countByProductId(Long productId);
    Optional<Orders> findTopActiveByProductId(Long productId);
}
