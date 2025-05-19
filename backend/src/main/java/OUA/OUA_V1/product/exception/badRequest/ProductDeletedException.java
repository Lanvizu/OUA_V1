package OUA.OUA_V1.product.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.BadRequestException;

public class ProductDeletedException extends BadRequestException {
    private static final String MESSAGE = "삭제된 상품입니다.";

    public ProductDeletedException() {
        super(MESSAGE);
    }
}
