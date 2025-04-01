package OUA.OUA_V1.product.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.BadRequestException;

public class ProductEndDateExceededException extends BadRequestException {
    
    private static final String MESSAGE = "종료 시간은 현재 시간 이후여야 합니다.";

    public ProductEndDateExceededException() {
        super(MESSAGE);
    }
}
