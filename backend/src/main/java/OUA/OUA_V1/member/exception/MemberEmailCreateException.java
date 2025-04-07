package OUA.OUA_V1.member.exception;

import OUA.OUA_V1.advice.InternalServerException;

public class MemberEmailCreateException extends InternalServerException {

    private static final String MESSAGE = "이메일 생성 중 오류가 발생했습니다.";

    public MemberEmailCreateException(){
        super(MESSAGE);
    }
}
