package OUA.OUA_V1.product.exception;

import OUA.OUA_V1.advice.NotFoundException;

public class ProductNotFoundException extends NotFoundException {
    
    private static final String TARGET = "상품";

    public ProductNotFoundException() {
        super(TARGET);
    }
}
