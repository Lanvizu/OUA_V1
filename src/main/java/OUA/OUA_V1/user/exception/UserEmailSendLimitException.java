package OUA.OUA_V1.user.exception;

import OUA.OUA_V1.advice.RateLimitingException;

public class UserEmailSendLimitException extends RateLimitingException {

    private static final int COOLDOWN_MINUTES = 1;

    public UserEmailSendLimitException() {
        super(COOLDOWN_MINUTES);
    }
}
