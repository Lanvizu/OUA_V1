package OUA.OUA_V1.member.exception.badRequest;

import OUA.OUA_V1.advice.badRequest.BadRequestException;

public class MemberIllegalPasswordException extends BadRequestException {

    private static final String MESSAGE = "입력하신 비밀번호의 형식이 맞지 않습니다.";

    public MemberIllegalPasswordException() {
        super(MESSAGE);
    }
}
