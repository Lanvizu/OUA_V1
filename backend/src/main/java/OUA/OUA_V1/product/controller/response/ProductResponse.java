package OUA.OUA_V1.product.controller.response;

import OUA.OUA_V1.product.domain.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long productId,
        String name,
        String description,
        int highestOrderPrice,
        int buyNowPrice,
        LocalDateTime endDate,
        Integer categoryId,
        List<String> imageUrls,
        ProductStatus status,
        boolean isOwner
) {
}
