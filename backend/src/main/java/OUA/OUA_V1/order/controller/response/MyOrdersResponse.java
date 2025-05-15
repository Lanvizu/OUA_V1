package OUA.OUA_V1.order.controller.response;

import OUA.OUA_V1.order.domain.OrderStatus;

import java.time.LocalDateTime;

public record MyOrdersResponse(
        Long productId,
        String productName,
        LocalDateTime productEndDate,
        LocalDateTime createdDate,
        Long orderId,
        int orderPrice,
        OrderStatus status
) {
}
