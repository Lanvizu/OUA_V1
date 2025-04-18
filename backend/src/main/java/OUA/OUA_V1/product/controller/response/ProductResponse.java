package OUA.OUA_V1.product.controller.response;

import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long productId,
        String name,
        String description,
        int initialPrice,
        int buyNowPrice,
        LocalDateTime endDate,
        Integer categoryId,
        List<String> imageUrls,
        boolean isOwner
) {
}
