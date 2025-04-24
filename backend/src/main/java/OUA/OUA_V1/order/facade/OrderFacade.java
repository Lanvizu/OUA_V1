package OUA.OUA_V1.order.facade;

import OUA.OUA_V1.advice.badRequest.OrderPriceException;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.service.MemberService;
import OUA.OUA_V1.order.controller.request.OrderCreateRequest;
import OUA.OUA_V1.order.domain.Order;
import OUA.OUA_V1.order.exception.badRequest.OrderAlreadyExistsException;
import OUA.OUA_V1.order.exception.badRequest.OrderOnOwnProductException;
import OUA.OUA_V1.order.service.OrderService;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.exception.badRequest.ProductClosedException;
import OUA.OUA_V1.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ProductService productService;

    @Transactional
    public Long create(Long memberId, Long productId, OrderCreateRequest request) {
        validateDuplicateOrder(memberId, productId);
        Product product = productService.findById(productId); // 락 조회 추가 필요

        validateOwnProduct(memberId, product);
        validateProductOnSale(product);
        validateOrderPrice(product, request.orderPrice());

        Member member = memberService.findById(memberId);
        Order order = orderService.createOrder(member, product, request.orderPrice());
        product.updateHighestOrder(order);
        return order.getId();
    }

    @Transactional
    public void cancel(Long orderId) {
        Product product = orderService.cancelOrder(orderId);
        Optional<Order> highestOrder = orderService.findHighestOrder(product.getId());
        if(highestOrder.isPresent()) {
            product.updateHighestOrder(highestOrder.get());
        }else{
            product.resetHighestOrder();
        }
    }

    private void validateDuplicateOrder(Long memberId, Long productId) {
        if (orderService.existsByMemberIdAndProductId(memberId, productId)) {
            throw new OrderAlreadyExistsException();
        }
    }

    private void validateOrderPrice(Product product, int orderPrice) {
        int currentHighest = product.getHighestOrderPrice();

        if (orderPrice <= currentHighest) {
            throw new OrderPriceException(currentHighest, product.getBuyNowPrice(), orderPrice);
        }
        if (orderPrice > product.getBuyNowPrice()) {
            throw new OrderPriceException(currentHighest, product.getBuyNowPrice(), orderPrice);
        }
    }

    private static void validateOwnProduct(Long memberId, Product product) {
        if(product.getMember().getId().equals(memberId)){
            throw new OrderOnOwnProductException();
        }
    }

    private void validateProductOnSale(Product product) {
        if (!product.getOnSale()) {
            throw new ProductClosedException();
        }
    }
}
