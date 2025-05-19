package OUA.OUA_V1.order.facade;

import OUA.OUA_V1.advice.badRequest.OrderPriceException;
import OUA.OUA_V1.global.RedisLockTemplate;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.service.MemberService;
import OUA.OUA_V1.order.controller.request.OrderRequest;
import OUA.OUA_V1.order.domain.Orders;
import OUA.OUA_V1.order.exception.badRequest.OrderOnOwnProductException;
import OUA.OUA_V1.order.service.OrdersService;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.domain.ProductStatus;
import OUA.OUA_V1.product.exception.badRequest.ProductClosedException;
import OUA.OUA_V1.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderFacade {

    private final OrdersService ordersService;
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
                    validateProductIsActive(product); // onSale과 현재 입찰 시간을 판단하는 기능 추가.
                    validateOrderPrice(product, request.orderPrice());

                    Member member = memberService.findById(memberId);
                    Orders orders = ordersService.createOrder(member, product, request.orderPrice());
                    product.updateHighestOrder(orders.getId(), orders.getOrderPrice());
                    return orders.getId();
                }
        );
    }

    @Transactional
    public Long buyNow(Long memberId, Long productId){
        return lockTemplate.executeWithLock(
                productId,
                () ->{
                    Product product = productService.findById(productId);
                    validateOwnProduct(memberId, product);
                    validateProductIsActive(product);

                    Member member = memberService.findById(memberId);
                    Orders confirmedOrders = ordersService.buyNowOrder(member, product);
                    product.updateHighestOrder(confirmedOrders.getId(), confirmedOrders.getOrderPrice());
                    ordersService.failOtherOrders(product.getId(), confirmedOrders.getId());
                    product.soldAuction();
                    return confirmedOrders.getId();
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
                Orders orders = ordersService.findById(orderId);
                ordersService.cancelOrder(orders);
                if (product.isHighestOrder(orderId)) {
                    Optional<Orders> highestOrder = ordersService.findHighestOrder(product.getId());
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
                    Orders orders = ordersService.findById(orderId);

                    validateProductIsActive(product);
                    validateOrderPrice(product, request.orderPrice());

                    orders.updateOrderPrice(request.orderPrice());
                    product.updateHighestOrder(orders.getId(), orders.getOrderPrice());
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

    private void validateProductIsActive(Product product) {
        if (!product.getStatus().equals(ProductStatus.ACTIVE) || product.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ProductClosedException();
        }
    }
}
