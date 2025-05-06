package OUA.OUA_V1.order.controller;

import OUA.OUA_V1.auth.annotation.LoginMember;
import OUA.OUA_V1.auth.annotation.RequireAuthCheck;
import OUA.OUA_V1.global.LoginProfile;
import OUA.OUA_V1.order.controller.request.OrderRequest;
import OUA.OUA_V1.order.controller.response.MyOrdersResponse;
import OUA.OUA_V1.order.controller.response.OrdersResponse;
import OUA.OUA_V1.order.controller.response.CountAndMyOrderResponse;
import OUA.OUA_V1.order.domain.Orders;
import OUA.OUA_V1.order.facade.OrderFacade;
import OUA.OUA_V1.order.service.OrdersService;
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

    private final OrdersService ordersService;
    private final OrderFacade orderFacade;

    @PostMapping("/product/{productId}/orders")
    public ResponseEntity<Void> createOrder(
            @LoginMember Long memberId,
            @PathVariable Long productId,
            @RequestBody @Valid OrderRequest request
    ) {
        Long orderId = orderFacade.create(memberId, productId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/product/{productId}/buy-now")
    public ResponseEntity<Void> buyNow(
            @LoginMember Long memberId,
            @PathVariable Long productId
    ) {
        Long orderId = orderFacade.buyNow(memberId, productId);
        return ResponseEntity.ok().build();
    }

    // 상품에 대한 전체 주문 조회
    @GetMapping("/product/{productId}/total-orders")
    public ResponseEntity<PagedModel<OrdersResponse>> getProductOrders(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<OrdersResponse> productOrders = ordersService.getProductOrders(productId, pageable);
        return ResponseEntity.ok(new PagedModel<>(productOrders));
    }

    // 상품 페이지 로딩
    @GetMapping("/product/{productId}/orders")
    public ResponseEntity<CountAndMyOrderResponse> getMyOrderAndOrdersCount(
            @PathVariable Long productId,
            @LoginMember Long memberId
    ) {
        long productOrdersCount =  ordersService.getCountByProductId(productId);
        OrdersResponse myOrderForProduct = ordersService.getMyOrderForProduct(memberId, productId);
        return ResponseEntity.ok(new CountAndMyOrderResponse(myOrderForProduct, productOrdersCount));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<PagedModel<MyOrdersResponse>> getMyOrders(
            @LoginMember Long memberId,
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MyOrdersResponse> myOrders = ordersService.getMyOrders(memberId, pageable);
        return ResponseEntity.ok(new PagedModel<>(myOrders));
    }

    @RequireAuthCheck(targetId = "orderId", targetDomain = Orders.class)
    @PostMapping("/product/{productId}/orders/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable("productId") Long productId,
            @PathVariable("orderId") Long orderId,
            LoginProfile loginProfile
    ) {
        orderFacade.cancel(productId, orderId);
        return ResponseEntity.noContent().build();
    }

    @RequireAuthCheck(targetId = "orderId", targetDomain = Orders.class)
    @PostMapping("/product/{productId}/orders/{orderId}/update")
    public ResponseEntity<Void> updateOrder(
            @PathVariable("productId") Long productId,
            @PathVariable("orderId") Long orderId,
            @RequestBody @Valid OrderRequest request,
            LoginProfile loginProfile
    ) {
        orderFacade.updatePrice(productId, orderId, request);
        return ResponseEntity.ok().build();
    }

    // 상품 주인이 입찰 수락? 이걸 넣는 것이 적합할까...
}
