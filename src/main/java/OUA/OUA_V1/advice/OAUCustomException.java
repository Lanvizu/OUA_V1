package OUA.OUA_V1.advice;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OAUCustomException extends RuntimeException {

    private final HttpStatus status;

    public OAUCustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public String statusCode() {
        return status.toString();
    }
}
