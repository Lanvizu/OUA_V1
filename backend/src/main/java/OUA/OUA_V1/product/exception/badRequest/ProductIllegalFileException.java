package OUA.OUA_V1.product.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.BadRequestException;

public class ProductIllegalFileException extends BadRequestException {

    private static final String MESSAGE = "유효하지 않은 파일입니다.";

    public ProductIllegalFileException() {
        super(MESSAGE);
    }
}
