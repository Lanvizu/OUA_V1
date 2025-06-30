package OUA.OUA_V1.order.facade;

import OUA.OUA_V1.advice.badRequest.OrderPriceException;
import OUA.OUA_V1.global.JvmLockTemplate;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.service.MemberService;
import OUA.OUA_V1.order.controller.request.OrderRequest;
import OUA.OUA_V1.order.controller.response.OrdersResponse;
import OUA.OUA_V1.order.domain.Orders;
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

@Service
@RequiredArgsConstructor
public class OrderLockService {

    private final OrdersService ordersService;
    private final MemberService memberService;
    private final ProductService productService;
    private final JvmLockTemplate lockTemplate;
    private final OrderFacade orderFacade;

    public Long create(Long memberId, Long productId, OrderRequest request) {
        return lockTemplate.executeWithLock(productId, () -> {
            Product product = productService.findById(productId);
            validateOwnProduct(memberId, product);
            validateProductIsActive(product);
            validateNotExistingOrder(memberId, productId);
            validateOrderPrice(product, request.orderPrice());

            Member member = memberService.findById(memberId);
            return orderFacade.create(member, product, request.orderPrice());
        });
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
