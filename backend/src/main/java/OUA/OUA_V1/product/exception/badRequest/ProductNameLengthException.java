package OUA.OUA_V1.product.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.TextLengthException;

public class ProductNameLengthException extends TextLengthException {

    private static final String TEXT = "상품명";

    public ProductNameLengthException(int minLength, int maxLength, int currentLength) {
        super(TEXT, minLength, maxLength, currentLength);
    }
}
