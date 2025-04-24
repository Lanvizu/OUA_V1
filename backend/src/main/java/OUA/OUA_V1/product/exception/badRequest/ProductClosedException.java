package OUA.OUA_V1.product.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.BadRequestException;

public class ProductClosedException extends BadRequestException {
    private static final String MESSAGE = "경매가 종료된 상품입니다.";

    public ProductClosedException() {
        super(MESSAGE);
    }
}
