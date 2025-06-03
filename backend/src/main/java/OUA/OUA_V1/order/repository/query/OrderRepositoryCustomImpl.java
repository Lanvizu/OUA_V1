package OUA.OUA_V1.order.repository.query;

import OUA.OUA_V1.order.domain.Orders;
import OUA.OUA_V1.order.domain.OrderStatus;
import OUA.OUA_V1.order.domain.QOrders;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static OUA.OUA_V1.product.domain.QProduct.product;

@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Orders> findByMemberIdWithKeySet(Long memberId, LocalDateTime lastCreatedDate, int size) {
        QOrders order = QOrders.orders;

        BooleanBuilder predicate = new BooleanBuilder()
                .and(order.member.id.eq(memberId))
                .and(order.status.ne(OrderStatus.CANCELED));

        if (lastCreatedDate != null) {
            predicate.and(order.createdDate.lt(lastCreatedDate));
        }

        List<Orders> orders = queryFactory
                .selectFrom(order)
                .join(order.product, product).fetchJoin()
                .where(predicate)
                .orderBy(order.createdDate.desc(), order.id.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = orders.size() > size;

        if (hasNext) {
            orders.removeLast();
        }

        return new SliceImpl<>(orders, PageRequest.of(0, size), hasNext);
    }

    @Override
    public Optional<Orders> findVisibleOrder(Long memberId, Long productId) {
        QOrders order = QOrders.orders;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(order)
                        .where(
                                order.member.id.eq(memberId),
                                order.product.id.eq(productId),
                                order.status.ne(OrderStatus.CANCELED)
                        )
                        .fetchOne()
        );
    }

    @Override
    public Optional<Orders> findActiveOrder(Long memberId, Long productId) {
        QOrders order = QOrders.orders;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(order)
                        .where(
                                order.member.id.eq(memberId),
                                order.product.id.eq(productId),
                                order.status.eq(OrderStatus.ACTIVE)
                        )
                        .fetchOne()
        );
    }

    @Override
    public Optional<Orders> findTopActiveByProductId(Long productId) {
        QOrders order = QOrders.orders;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(order)
                        .where(
                                order.product.id.eq(productId),
                                order.status.ne(OrderStatus.CANCELED)
                        )
                        .orderBy(order.orderPrice.desc())
                        .limit(1)
                        .fetchOne()
        );
    }

    @Override
    public List<Orders> findActiveOrdersByProductId(Long productId, @Nullable Long excludeOrderId) {
        QOrders order = QOrders.orders;

        BooleanBuilder where = new BooleanBuilder()
                .and(order.product.id.eq(productId))
                .and(order.status.eq(OrderStatus.ACTIVE));

        if (excludeOrderId != null) {
            where.and(order.id.ne(excludeOrderId));
        }

        return queryFactory
                .selectFrom(order)
                .where(where)
                .fetch();
    }

}
