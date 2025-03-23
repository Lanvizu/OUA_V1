package OUA.OUA_V1.advice;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends OUACustomException {

    private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;

    public UnauthorizedException(String message) {
        super(message, STATUS);
    }
}
