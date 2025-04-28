package OUA.OUA_V1.order.facade;

import OUA.OUA_V1.advice.badRequest.OrderPriceException;
import OUA.OUA_V1.global.RedisLockTemplate;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.service.MemberService;
import OUA.OUA_V1.order.controller.request.OrderRequest;
import OUA.OUA_V1.order.domain.Order;
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
    private final RedisLockTemplate lockTemplate;

    @Transactional
    public Long create(Long memberId, Long productId, OrderRequest request) {
        return lockTemplate.executeWithLock(
                productId,
                () ->{
                    Product product = productService.findById(productId);
                    validateOwnProduct(memberId, product);
                    validateProductOnSale(product);
                    validateOrderPrice(product, request.orderPrice());

                    Member member = memberService.findById(memberId);
                    Order order = orderService.createOrder(member, product, request.orderPrice());
                    product.updateHighestOrder(order.getId(), order.getOrderPrice());
                    return order.getId();
                }
        );
    }

    @Transactional
    public void cancel(Long productId, Long orderId) {
        // 추후 최고 입찰가인 경우에만 분산락 적용되도록 변경 필요
        lockTemplate.executeWithLock(
            productId,
            ()->{
                Product product = productService.findById(productId);
                Order order = orderService.findById(orderId);
                orderService.cancelOrder(order);
                if (product.isHighestOrder(orderId)) {
                    Optional<Order> highestOrder = orderService.findHighestOrder(product.getId());
                    highestOrder.ifPresentOrElse(
                            h -> product.updateHighestOrder(h.getId(), h.getOrderPrice()),
                            product::resetHighestOrder
                    );
                }
            }
        );
    }

    @Transactional
    public void updatePrice(Long productId, Long orderId, OrderRequest request) {
        lockTemplate.executeWithLock(
                productId,
                () -> {
                    Product product = productService.findById(productId);
                    Order order = orderService.findById(orderId);

                    validateProductOnSale(product);
                    validateOrderPrice(product, request.orderPrice());

                    order.updateOrderPrice(request.orderPrice());
                    product.updateHighestOrder(order.getId(), order.getOrderPrice());
                }
        );
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
