package OUA.OUA_V1.order.controller;

import OUA.OUA_V1.auth.annotation.LoginMember;
import OUA.OUA_V1.auth.annotation.RequireAuthCheck;
import OUA.OUA_V1.global.LoginProfile;
import OUA.OUA_V1.order.controller.request.OrderRequest;
import OUA.OUA_V1.order.controller.response.MyOrdersResponse;
import OUA.OUA_V1.order.controller.response.OrdersResponse;
import OUA.OUA_V1.order.domain.Orders;
import OUA.OUA_V1.order.facade.OrderLockFacade;
import OUA.OUA_V1.order.service.OrdersService;
import OUA.OUA_V1.product.controller.response.SlicedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrdersService ordersService;
    private final OrderLockFacade orderLockService;

    @PostMapping("/product/{productId}/orders")
    public ResponseEntity<Void> createOrder(
            @LoginMember Long memberId,
            @PathVariable Long productId,
            @RequestBody @Valid OrderRequest request
    ) {
        orderLockService.createWithLock(memberId, productId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/product/{productId}/orders/quick")
    public ResponseEntity<Void> buyNow(
            @LoginMember Long memberId,
            @PathVariable Long productId
    ) {
        orderLockService.buyNowWithLock(memberId, productId);
        return ResponseEntity.ok().build();
    }

    // 상품 페이지 로딩
    @GetMapping("/product/{productId}/orders")
    public ResponseEntity<OrdersResponse> getMyOrderAndOrdersCount(
            @PathVariable Long productId,
            @LoginMember Long memberId
    ) {
        OrdersResponse myOrderForProduct = ordersService.findVisibleOrder(memberId, productId);
        if (myOrderForProduct == null) {
            return ResponseEntity.noContent().build(); // HTTP 204
        }
        return ResponseEntity.ok(myOrderForProduct);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<SlicedResponse<MyOrdersResponse>> getMyOrders(
            @LoginMember Long memberId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedDate,
            @RequestParam(defaultValue = "20") int size
    ) {
        Slice<MyOrdersResponse> myOrders = ordersService.findByMemberIdWithKeySet(memberId, lastCreatedDate, size);
        return ResponseEntity.ok(new SlicedResponse<>(myOrders.getContent(), myOrders.hasNext()));
    }

    @RequireAuthCheck(targetId = "orderId", targetDomain = Orders.class)
    @DeleteMapping("/product/{productId}/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable("productId") Long productId,
            @PathVariable("orderId") Long orderId,
            LoginProfile loginProfile
    ) {
        orderLockService.cancelWithLock(loginProfile.memberId(), productId, orderId);
        return ResponseEntity.noContent().build();
    }

    @RequireAuthCheck(targetId = "orderId", targetDomain = Orders.class)
    @PatchMapping("/product/{productId}/orders/{orderId}")
    public ResponseEntity<Void> updateOrder(
            @PathVariable("productId") Long productId,
            @PathVariable("orderId") Long orderId,
            @RequestBody @Valid OrderRequest request,
            LoginProfile loginProfile
    ) {
        orderLockService.updatePriceWithLock(productId, orderId, request);
        return ResponseEntity.ok().build();
    }
}
