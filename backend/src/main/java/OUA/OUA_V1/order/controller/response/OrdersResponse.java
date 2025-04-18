package OUA.OUA_V1.order.controller.response;

public record OrdersResponse(
        Long memberId,
        Long orderId,
        int orderPrice
) {
}
