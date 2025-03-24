package OUA.OUA_V1.advice;

import org.springframework.http.HttpStatus;

public class RateLimitingException extends OUACustomException {

    private static final String MESSAGE = "%d분 마다 요청이 가능합니다.";
    private static final HttpStatus STATUS = HttpStatus.TOO_MANY_REQUESTS;

    public RateLimitingException(int collDownMinutes) {
        super(String.format(MESSAGE, collDownMinutes), STATUS);
    }
}
