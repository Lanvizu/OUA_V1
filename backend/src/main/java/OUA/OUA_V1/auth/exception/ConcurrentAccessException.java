package OUA.OUA_V1.auth.exception;

import OUA.OUA_V1.advice.InternalServerException;

public class ConcurrentAccessException extends InternalServerException {

    private static final String MESSAGE = "다른 사용자가 동시에 접근 중입니다. 잠시 후 다시 시도해주세요.";

    public ConcurrentAccessException(){
        super(MESSAGE);
    }
}
