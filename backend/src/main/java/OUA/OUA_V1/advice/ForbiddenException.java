package OUA.OUA_V1.advice;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends OUACustomException {
    private static final String MESSAGE = "접근 권한이 없습니다";
    private static final HttpStatus STATUS = HttpStatus.FORBIDDEN;

    public ForbiddenException() {
        super(MESSAGE, STATUS);
    }
}
