package OUA.OUA_V1.product.controller.response;

import java.time.LocalDateTime;
import java.util.List;

public record ProductPreviewResponse(
        Long productId,
        String name,
        int highestOrderPrice,
        int buyNowPrice,
        LocalDateTime endDate,
        List<String> imageUrls
) {
}
