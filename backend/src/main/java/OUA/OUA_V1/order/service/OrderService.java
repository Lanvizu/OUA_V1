package OUA.OUA_V1.order.service;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.order.controller.response.MyOrdersResponse;
import OUA.OUA_V1.order.controller.response.OrdersResponse;
import OUA.OUA_V1.order.domain.Order;
import OUA.OUA_V1.order.exception.OrderNotFoundException;
import OUA.OUA_V1.order.repository.OrderRepository;
import OUA.OUA_V1.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(Member member, Product product, int orderPrice) {
        Order order = new Order(member, product, orderPrice);
        return orderRepository.save(order);
    }

    @Transactional
    public Order buyNowOrder(Member member, Product product) {
        Order order = new Order(member, product, product.getBuyNowPrice());
        order.confirmOrder();
        return orderRepository.save(order);
    }

    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
    }

    public OrdersResponse getMyOrderForProduct(Long memberId, Long productId) {
        return orderRepository.findByMemberIdAndProductId(memberId, productId)
                .map(this::toOrdersResponse)
                .orElse(null);
    }

    public Page<OrdersResponse> getProductOrders(Long productId, Pageable pageable) {
        return orderRepository.findAllByProductId(productId, pageable)
                .map(this::toOrdersResponse);
    }

    public long getCountByProductId(Long productId) {
        return orderRepository.countByProductId(productId);
    }

    public Page<MyOrdersResponse> getMyOrders(Long memberId, Pageable pageable) {
        return orderRepository.findAllByMemberId(memberId, pageable)
                .map(this::toMyOrdersResponse);
    }

    private OrdersResponse toOrdersResponse(Order order) {
        return new OrdersResponse(
                order.getMember().getId(),
                order.getId(),
                order.getOrderPrice()
        );
    }

    private MyOrdersResponse toMyOrdersResponse(Order order) {
        return new MyOrdersResponse(
                order.getProduct().getId(),
                order.getProduct().getName(),
                order.getProduct().getEndDate(),
                order.getId(),
                order.getOrderPrice(),
                order.getStatus()
        );
    }

    @Transactional
    public void cancelOrder(Order order) {
        order.cancel();
        order.markAsDeleted();
    }

    public Optional<Order> findHighestOrder(Long productId) {
        return orderRepository.findTopActiveByProductId(productId);
    }
}
