package OUA.OUA_V1.advice;

import org.springframework.http.HttpStatus;

public class ConflictException extends OUACustomException {

    private static final HttpStatus STATUS = HttpStatus.CONFLICT;

    public ConflictException(String message) {
        super(message, STATUS);
    }
}
