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
public class OrderLockFacade {

    private final OrdersService ordersService;
    private final MemberService memberService;
    private final ProductService productService;
    private final JvmLockTemplate lockTemplate;
    private final OrderFacade orderFacade;

    public void createWithLock(Long memberId, Long productId, OrderRequest request) {
        lockTemplate.executeWithLock(productId, () -> {
            // 페치조인으로 Lazy 조회 문제 해결
            Product product = productService.findByIdWithMemberId(productId);
            validateOwnProduct(memberId, product);
            validateProductIsActive(product);
            validateNotExistingOrder(memberId, productId);
            validateOrderPrice(product, request.orderPrice());

            Member member = memberService.findById(memberId);
            orderFacade.create(member, product, request.orderPrice());
            return null;
        });
    }

    public void updatePriceWithLock(Long productId, Long orderId, OrderRequest request) {
        lockTemplate.executeWithLock(
                productId,
                () -> {
                    Product product = productService.findById(productId);
                    Orders orders = ordersService.findById(orderId);

                    validateProductIsActive(product);
                    validateOrderPrice(product, request.orderPrice());

                    orderFacade.updatePrice(product, orders, request.orderPrice());
                    return null;
                }
        );
    }

    public void buyNowWithLock(Long memberId, Long productId) {
        lockTemplate.executeWithLock(
                productId,
                () -> {
                    // 상품 주인 체크 & 상품 상태 체크 (페치조인으로 member 같이 조회)
                    Product product = productService.findByIdWithMemberId(productId);
                    validateOwnProduct(memberId, product);
                    validateProductIsActive(product);
                    // 입찰자 이전 주문 체크 -> 존재하는 경우 해당 주문을 수정
                    Optional<Orders> existingOrderOpt = ordersService.findActiveOrder(memberId, productId);
                    orderFacade.buyNow(existingOrderOpt, product, memberId);
                    return null;
                }
        );
    }

    public void cancelWithLock(Long memberId, Long productId, Long orderId) {
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
                    orderFacade.cancel(activeOrder.get(), product, orderId);
                    return null;
                }
        );
    }

    // 상품 주인 예외처리
    private static void validateOwnProduct(Long memberId, Product product) {
        if (product.getMember().getId().equals(memberId)) {
            throw new OrderOnOwnProductException();
        }
    }

    // 상품 상태 예외처리
    private void validateProductIsActive(Product product) {
        if (!product.getStatus().equals(ProductStatus.ACTIVE) || product.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ProductClosedException();
        }
    }

    // 상품에 대한 주문 여부 예외처리
    private void validateNotExistingOrder(Long memberId, Long productId) {
        OrdersResponse myOrderForProduct = ordersService.findVisibleOrder(memberId, productId);
        if (myOrderForProduct != null) {
            throw new OrderAlreadyExistsException();
        }
    }

    // 상품 가격 예외처리
    private void validateOrderPrice(Product product, int orderPrice) {
        int currentHighest = product.getHighestOrderPrice();
        if (orderPrice <= currentHighest) {
            throw new OrderPriceException(currentHighest, product.getBuyNowPrice(), orderPrice);
        }
        if (orderPrice > product.getBuyNowPrice()) {
            throw new OrderPriceException(currentHighest, product.getBuyNowPrice(), orderPrice);
        }
    }
}
