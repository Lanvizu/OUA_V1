package OUA.OUA_V1.order.facade;

import OUA.OUA_V1.advice.badRequest.OrderPriceException;
import OUA.OUA_V1.global.JvmLockTemplate;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.service.MemberService;
import OUA.OUA_V1.order.controller.request.OrderRequest;
import OUA.OUA_V1.order.controller.response.OrdersResponse;
import OUA.OUA_V1.order.domain.Orders;
import OUA.OUA_V1.order.exception.OrderNotFoundException;
import OUA.OUA_V1.order.exception.badRequest.OrderAlreadyExistsException;
import OUA.OUA_V1.order.exception.badRequest.OrderOnOwnProductException;
import OUA.OUA_V1.order.service.OrdersService;
import OUA.OUA_V1.product.domain.Product;
import OUA.OUA_V1.product.domain.ProductStatus;
import OUA.OUA_V1.product.exception.badRequest.ProductClosedException;
import OUA.OUA_V1.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrdersService ordersService;
    private final MemberService memberService;
    private final ProductService productService;
    private final JvmLockTemplate lockTemplate;

    // 락 해제 시점을 트랜잭션 커밋 이후로 고정하기 위해 트랜잭션 어노테이션 제거.
    public Long create(Long memberId, Long productId, OrderRequest request) {
        return lockTemplate.executeWithLock(productId, () -> {
            Product product = productService.findById(productId);
            validateOwnProduct(memberId, product);
            validateProductIsActive(product);
            validateNotExistingOrder(memberId, productId);
            validateOrderPrice(product, request.orderPrice());

            Member member = memberService.findById(memberId);
            Orders orders = ordersService.createOrder(member, product, request.orderPrice());
            productService.updateHighestOrder(product, orders.getId(), orders.getOrderPrice());
            return orders.getId();
        });
    }

    public Long buyNow(Long memberId, Long productId) {
        return lockTemplate.executeWithLock(
                productId,
                () -> {
                    // 상품 주인 체크 & 상품 상태 체크
                    Product product = productService.findById(productId);
                    validateOwnProduct(memberId, product);
                    validateProductIsActive(product);
                    // 입찰자 이전 주문 체크 -> 존재하는 경우 해당 주문을 수정
                    Optional<Orders> existingOrderOpt = ordersService.findActiveOrder(memberId, productId);
                    Orders confirmedOrders;
                    if (existingOrderOpt.isPresent()) {
                        // 상품 상태도 확인 필요 -> ㄴㄴ ACTIVE만 가져오도록 설정
                        confirmedOrders = ordersService.updateBuyNowOrder(existingOrderOpt.get(), product);
                    } else {
                        Member member = memberService.findById(memberId);
                        confirmedOrders = ordersService.buyNowOrder(member, product);
                    }
                    // 이전 주문이 없는 경우 새로운 즉시 구매 주문을 생성
                    // 경매 종료 + 최고 입찰가 업데이트
                    productService.soldOutAndUpdateOrder(product, confirmedOrders.getId(), confirmedOrders.getOrderPrice());
                    ordersService.failOtherOrders(product.getId(), confirmedOrders.getId());
                    return confirmedOrders.getId();
                }
        );
    }

    public void cancel(Long memberId, Long productId, Long orderId) {
        lockTemplate.executeWithLock(
                productId,
                () -> {
                    Product product = productService.findById(productId);
                    // 상품 상태 체크
                    validateProductIsActive(product);

                    // 유효한 주문인지 체크 -> 주문이 ACTIVE인 경우에만 가능하도록
                    Optional<Orders> activeOrder = ordersService.findActiveOrder(memberId, productId);
                    if (activeOrder.isEmpty()) {
                        throw new OrderNotFoundException();
                    }
                    // 해당 메서드 내의 트랜잭션 커밋이 끝나고 최신 상태에서 조회 가능
                    ordersService.cancelOrder(activeOrder.get());

                    // 최고 입찰가인 경우 다음 최고 입찰가로 업데이트.
                    if (product.isHighestOrder(orderId)) {
                        Optional<Orders> highestOrder = ordersService.findHighestOrder(product.getId());
                        highestOrder.ifPresentOrElse(
                                h -> productService.updateHighestOrder(product, h.getId(), h.getOrderPrice()),
                                () -> productService.resetHighestOrder(product)
                        );
                    }
                    return null;
                }
        );
    }

    public void updatePrice(Long productId, Long orderId, OrderRequest request) {
        lockTemplate.executeWithLock(
                productId,
                () -> {
                    Product product = productService.findById(productId);
                    Orders orders = ordersService.findById(orderId);

                    validateProductIsActive(product);
                    validateOrderPrice(product, request.orderPrice());

                    ordersService.updateOrderPrice(orders, request.orderPrice());
                    productService.updateHighestOrder(product, orders.getId(), orders.getOrderPrice());
                    return null;
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
        if (product.getMember().getId().equals(memberId)) {
            throw new OrderOnOwnProductException();
        }
    }

    private void validateProductIsActive(Product product) {
        if (!product.getStatus().equals(ProductStatus.ACTIVE) || product.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ProductClosedException();
        }
    }

    private void validateNotExistingOrder(Long memberId, Long productId) {
        OrdersResponse myOrderForProduct = ordersService.findVisibleOrder(memberId, productId);
        if (myOrderForProduct != null) {
            throw new OrderAlreadyExistsException();
        }
    }
}
