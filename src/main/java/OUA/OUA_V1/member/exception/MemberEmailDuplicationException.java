package OUA.OUA_V1.member.exception;

import OUA.OUA_V1.advice.ConflictException;

public class MemberEmailDuplicationException extends ConflictException {

    private static final String MESSAGE = "이미 가입되어있는 Email 입니다.";

    public MemberEmailDuplicationException() {
        super(MESSAGE);
    }
}
