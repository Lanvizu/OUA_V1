package OUA.OUA_V1.product.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.TextBlankException;

public class ProductNameBlankException extends TextBlankException {

    private static final String TEXT = "상품명";

    public ProductNameBlankException() {
        super(TEXT);
    }
}
