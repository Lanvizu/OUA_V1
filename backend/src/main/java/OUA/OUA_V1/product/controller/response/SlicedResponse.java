package OUA.OUA_V1.product.controller.response;

import java.util.List;

public record SlicedResponse<T>(
        List<T> content,
        boolean hasNext
) {
}
