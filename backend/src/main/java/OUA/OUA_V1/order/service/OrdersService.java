package OUA.OUA_V1.order.service;

import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.order.controller.response.MyOrdersResponse;
import OUA.OUA_V1.order.controller.response.OrdersResponse;
import OUA.OUA_V1.order.domain.Orders;
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

    public Orders findById(Long orderId) {
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
                orders.getId(),
                orders.getOrderPrice(),
                orders.getStatus()
        );
    }

    @Transactional
    public void cancelOrder(Orders orders) {
        orders.cancel();
        orders.markAsDeleted();
    }

    public Optional<Orders> findHighestOrder(Long productId) {
        return orderRepository.findTopActiveByProductId(productId);
    }
}
