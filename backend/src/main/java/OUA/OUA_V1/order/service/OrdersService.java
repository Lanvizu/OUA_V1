package OUA.OUA_V1.order.service;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.order.controller.response.MyOrdersResponse;
import OUA.OUA_V1.order.controller.response.OrdersResponse;
import OUA.OUA_V1.order.domain.OrderStatus;
import OUA.OUA_V1.order.domain.Orders;
import OUA.OUA_V1.order.exception.OrderNotFoundException;
import OUA.OUA_V1.order.exception.badRequest.OrderNotActiveException;
import OUA.OUA_V1.order.repository.OrderRepository;
import OUA.OUA_V1.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrdersService {

    private final OrderRepository orderRepository;

    @Transactional
    public Orders createOrder(Member member, Product product, int orderPrice) {
        Orders orders = new Orders(member, product, orderPrice);
        return orderRepository.save(orders);
    }

    @Transactional
    public Orders buyNowOrder(Member member, Product product) {
        Orders orders = new Orders(member, product, product.getBuyNowPrice());
        orders.confirmOrder();
        return orderRepository.save(orders);
    }

    @Transactional
    public Orders updateBuyNowOrder(Orders orders, Product product) {
        orders.updateOrderPrice(product.getBuyNowPrice());
        orders.confirmOrder();
        return orderRepository.save(orders);
    }

    // 즉시 주문 || 경매 종료 시 다른 주문들 탈락 처리
    @Transactional
    public void failOtherOrders(Long productId, Long confirmedOrderId) {
        List<Orders> orders = orderRepository.findActiveOrdersByProductId(productId, confirmedOrderId);
        for (Orders order : orders) {
            order.markAsFailed();
        }
    }

    @Transactional
    public void confirmOrderFailOthers(Long productId, Orders orders) {
        orders.confirmOrder();
        List<Orders> activeOtherOrders = orderRepository.findActiveOrdersByProductId(productId, orders.getId());
        for (Orders order : activeOtherOrders) {
            order.markAsFailed();
        }
    }

    // 상품 삭제 시 모든 주문 탈락 처리
    @Transactional
    public void failAllByProductId(Long productId) {
        List<Orders> orders = orderRepository.findActiveOrdersByProductId(productId, null);
        for (Orders order : orders) {
            order.markAsFailed();
        }
    }

    @Transactional
    public void updateOrderPrice(Orders orders, int orderPrice) {
        orders.updateOrderPrice(orderPrice);
    }

    public Orders findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
    }

    public OrdersResponse findVisibleOrder(Long memberId, Long productId) {
        return orderRepository.findVisibleOrder(memberId, productId)
                .map(this::toOrdersResponse)
                .orElse(null);
    }

    public Optional<Orders> findActiveOrder(Long memberId, Long productId) {
        return orderRepository.findActiveOrder(memberId, productId);
    }

    public Slice<MyOrdersResponse> findByMemberIdWithKeySet(Long memberId, LocalDateTime lastCreatedDate,
                                                            int size) {
        return orderRepository.findByMemberIdWithKeySet(memberId, lastCreatedDate, size)
                .map(this::toMyOrdersResponse);
    }

    private OrdersResponse toOrdersResponse(Orders orders) {
        return new OrdersResponse(
                orders.getMember().getId(),
                orders.getId(),
                orders.getOrderPrice()
        );
    }

    private MyOrdersResponse toMyOrdersResponse(Orders orders) {
        return new MyOrdersResponse(
                orders.getProduct().getId(),
                orders.getProduct().getName(),
                orders.getProduct().getEndDate(),
                orders.getCreatedDate(),
                orders.getId(),
                orders.getOrderPrice(),
                orders.getStatus()
        );
    }

    @Transactional
    public void cancelOrder(Orders orders) {
        if (orders.getStatus() != OrderStatus.ACTIVE) {
            throw new OrderNotActiveException();
        }
        orders.cancel();
        orders.markAsDeleted();
    }

    public Optional<Orders> findHighestOrder(Long productId) {
        return orderRepository.findTopActiveByProductId(productId);
    }
}
