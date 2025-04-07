package OUA.OUA_V1.advice.badRequest;

import OUA.OUA_V1.advice.OUACustomException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends OUACustomException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public BadRequestException(String message) {
        super(message, STATUS);
    }
}
