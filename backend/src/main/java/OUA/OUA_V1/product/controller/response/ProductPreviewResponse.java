package OUA.OUA_V1.product.controller.response;

import OUA.OUA_V1.product.domain.ProductStatus;

import java.time.LocalDateTime;

public record ProductPreviewResponse(
        Long productId,
        String name,
        int highestOrderPrice,
        int buyNowPrice,
        LocalDateTime createdDate,
        LocalDateTime endDate,
        String mainImageUrl,
        ProductStatus status
) {
}
