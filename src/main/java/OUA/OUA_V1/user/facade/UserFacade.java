package OUA.OUA_V1.user.facade;

import OUA.OUA_V1.auth.security.TokenProvider;
import OUA.OUA_V1.config.RedisService;
import OUA.OUA_V1.user.controller.request.CodeVerificationRequest;
import OUA.OUA_V1.user.controller.request.EmailRequest;
import OUA.OUA_V1.user.controller.request.UserCreateRequest;
import OUA.OUA_V1.user.exception.UserEmailDuplicationException;
import OUA.OUA_V1.user.exception.UserEmailSendLimitException;
import OUA.OUA_V1.user.exception.badRequest.UserCodeVerificationException;
import OUA.OUA_V1.user.service.EmailService;
import OUA.OUA_V1.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final EmailService emailService;
    private final RedisService redisService;
    private final TokenProvider tokenProvider;

    private static final long VERIFICATION_EXPIRATION_MILLIS = 10 * 60 * 1000;

    public void requestEmailVerification(EmailRequest request) {
        validateEmailAvailability(request.email());
        checkResendCooldown(request.email());

        String code = generateRandomCode();
        emailService.sendVerificationEmail(request.email(), code);
        redisService.setVerificationCode(request.email(), code, VERIFICATION_EXPIRATION_MILLIS);
        redisService.setLastSentTime(request.email());
    }

    private void validateEmailAvailability(String email) {
        if (userService.existsByEmail(email)) {
            throw new UserEmailDuplicationException();
        }
    }

    private void checkResendCooldown(String email) {
        if (redisService.wasRecentlySent(email)) {
            throw new UserEmailSendLimitException();
        }
    }

    private String generateRandomCode() {
        return String.format("%06d", (int)(Math.random() * 999_999));
    }

    public String verifyEmailCode(CodeVerificationRequest request) {
        String storedCode = redisService.getVerificationCode(request.email());
        if (storedCode != null && storedCode.equals(request.code())) {
            Map<String, Object> claims = Map.of("email", request.email());
            String token = tokenProvider.createToken(claims);
            redisService.deleteVerificationCode(request.email());
            return token;
        } else {
            throw new UserCodeVerificationException();
        }
    }

    @Transactional
    public Long create(UserCreateRequest request) {
        String token = request.token();
        if (token == null || !tokenProvider.isAlive(token)) {
            throw new RuntimeException("유효하지 않은 인증 토큰입니다.");
        }
        String emailFromToken = tokenProvider.extractClaim(token, "email");
        if (!emailFromToken.equals(request.email())) {
            throw new RuntimeException("토큰에 담긴 이메일과 회원가입 요청 이메일이 일치하지 않습니다.");
        }
        return userService.create(request);
    }
}
