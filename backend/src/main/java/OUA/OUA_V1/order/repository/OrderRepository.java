package OUA.OUA_V1.order.repository;

import OUA.OUA_V1.order.domain.Order;
import OUA.OUA_V1.order.repository.query.OrderRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>,
        OrderRepositoryCustom {
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}
