package OUA.OUA_V1.order.controller.response;

public record CountAndMyOrderResponse(
        OrdersResponse myOrder,
        long productOrdersCount
) {
}
