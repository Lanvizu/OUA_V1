package OUA.OUA_V1.order.controller;

import OUA.OUA_V1.auth.annotation.LoginMember;
import OUA.OUA_V1.auth.annotation.RequireAuthCheck;
import OUA.OUA_V1.global.LoginProfile;
import OUA.OUA_V1.order.controller.request.OrderCreateRequest;
import OUA.OUA_V1.order.controller.response.MyOrdersResponse;
import OUA.OUA_V1.order.controller.response.OrdersResponse;
import OUA.OUA_V1.order.controller.response.CountAndMyOrderResponse;
import OUA.OUA_V1.order.facade.OrderFacade;
import OUA.OUA_V1.order.service.OrderService;
import OUA.OUA_V1.product.domain.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;
    private final OrderService orderService;

    @PostMapping("/product/{productId}/orders")
    public ResponseEntity<Void> createOrder(
            @LoginMember Long memberId,
            @PathVariable Long productId,
            @RequestBody @Valid OrderCreateRequest request
    ) {
        Long orderId = orderFacade.create(memberId, productId, request);
        return ResponseEntity.ok().build();
    }

    // 상품에 대한 전체 주문 조회
    @GetMapping("/product/{productId}/total-orders")
    public ResponseEntity<PagedModel<OrdersResponse>> getProductOrders(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<OrdersResponse> productOrders = orderService.getProductOrders(productId, pageable);
        return ResponseEntity.ok(new PagedModel<>(productOrders));
    }

    // 삼품 페이지 로딩
    @GetMapping("/product/{productId}/orders")
    public ResponseEntity<CountAndMyOrderResponse> getMyOrderAndOrdersCount(
            @PathVariable Long productId,
            @LoginMember Long memberId
    ) {
        long productOrdersCount =  orderService.getCountByProductId(productId);
        OrdersResponse myOrderForProduct = orderService.getMyOrderForProduct(memberId, productId);
        return ResponseEntity.ok(new CountAndMyOrderResponse(myOrderForProduct, productOrdersCount));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<PagedModel<MyOrdersResponse>> getMyOrders(
            @LoginMember Long memberId,
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MyOrdersResponse> myOrders = orderService.getMyOrders(memberId, pageable);
        return ResponseEntity.ok(new PagedModel<>(myOrders));
    }

    @RequireAuthCheck(targetId = "orderId", targetDomain = Product.class)
    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable("orderId") Long orderId,
            LoginProfile loginProfile
    ) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
