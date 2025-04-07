package OUA.OUA_V1.product.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.BadRequestException;

public class ProductIllegalNameException extends BadRequestException {

    private static final String MESSAGE = "올바르지 않은 상품명 양식입니다.";

    public ProductIllegalNameException() {
        super(MESSAGE);
    }
}
