package OUA.OUA_V1.member.exception;

import OUA.OUA_V1.advice.RateLimitingException;

public class MemberEmailSendLimitException extends RateLimitingException {

    private static final int COOLDOWN_MINUTES = 1;

    public MemberEmailSendLimitException() {
        super(COOLDOWN_MINUTES);
    }
}
