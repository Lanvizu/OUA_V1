package OUA.OUA_V1.auth.exception;

import OUA.OUA_V1.advice.UnauthorizedException;

public class DeletedAccountException extends UnauthorizedException {
    private static final String MESSAGE = "탈퇴 처리된 계정입니다.";

    public DeletedAccountException() {
        super(MESSAGE);
    }
}
