package OUA.OUA_V1.order.repository.query;

import OUA.OUA_V1.order.domain.Orders;
import OUA.OUA_V1.order.domain.OrderStatus;
import OUA.OUA_V1.order.domain.QOrders;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public long countByProductId(Long productId) {
        QOrders order = QOrders.orders;
        Long count = queryFactory
                .select(order.count())
                .from(order)
                .where(
                        order.product.id.eq(productId),
                        order.status.ne(OrderStatus.CANCELED)
                )
                .fetchOne();
        return count != null ? count: 0L;
    }

    @Override
    public Page<Orders> findAllByMemberId(Long memberId, Pageable pageable) {
        QOrders order = QOrders.orders;

        List<Orders> orders = queryFactory
                .selectFrom(order)
                .where(
                        order.member.id.eq(memberId),
                        order.status.ne(OrderStatus.CANCELED)
                )
                .orderBy(order.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = Optional.ofNullable(
                queryFactory
                        .select(order.count())
                        .from(order)
                        .where(order.member.id.eq(memberId))
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(orders, pageable, totalCount);
    }

    @Override
    public Page<Orders> findAllByProductId(Long productId, Pageable pageable) {
        QOrders order = QOrders.orders;

        List<Orders> orders = queryFactory
                .selectFrom(order)
                .where(
                        order.product.id.eq(productId),
                        order.status.ne(OrderStatus.CANCELED)
                )
                .orderBy(order.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = Optional.ofNullable(
                queryFactory
                        .select(order.count())
                        .from(order)
                        .where(order.product.id.eq(productId))
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(orders, pageable, totalCount);
    }

    @Override
    public Optional<Orders> findByMemberIdAndProductId(Long memberId, Long productId) {
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
}
