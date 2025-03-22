package OUA.OUA_V1.user.facade;

import OUA.OUA_V1.config.RedisService;
import OUA.OUA_V1.user.controller.request.CodeVerificationRequest;
import OUA.OUA_V1.user.controller.request.EmailRequest;
import OUA.OUA_V1.user.controller.request.UserCreateRequest;
import OUA.OUA_V1.user.exception.UserEmailDuplicationException;
import OUA.OUA_V1.user.service.EmailService;
import OUA.OUA_V1.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserFacade {

    private final UserService userService;
    private final EmailService emailService;
    private final RedisService redisService;
    private static final long VERIFICATION_EXPIRATION_MILLIS = 10 * 60 * 1000;
    private static final int RESEND_COOLDOWN_MINUTES = 1;

//    public void requestEmailVerification(EmailRequest request) {
//        // 이메일 중복 체크
//        if (userService.existsByEmail(request.email())) {
//            throw new UserEmailDuplicationException();
//        }
//        // 인증 이메일 발송
//        String code = emailService.sendMail(request.email());
//        redisService.setVerificationCode(request.email(), code, VERIFICATION_EXPIRATION_MILLIS);
//        // Record the time email was sent for rate limiting
//        redisService.setLastSentTime(request.email());
//        log.info("Verification email sent to: {}", request.email());
//    }

    public void requestEmailVerification(EmailRequest request) {
        validateEmailAvailability(request.email());
        checkResendCooldown(request.email());

        String code = generateRandomCode();
        emailService.sendVerificationEmail(request.email(), code);
        redisService.setVerificationCode(request.email(), code, VERIFICATION_EXPIRATION_MILLIS);

        // Record the time the email was sent for future cooldown checks
        redisService.setLastSentTime(request.email());
        log.info("Verification email sent to: {}", request.email());
    }

    private void validateEmailAvailability(String email) {
        if (userService.existsByEmail(email)) {
            throw new UserEmailDuplicationException();
        }
    }

    private void checkResendCooldown(String email) {
        if (redisService.wasRecentlySent(email)) {
            throw new RuntimeException("%d분 후 재전송 가능합니다");
        }
    }

    private String generateRandomCode() {
        return String.format("%06d", (int)(Math.random() * 999_999));
    }

    public boolean verifyEmailCode(String email, String inputCode) {
        String storedCode = redisService.getVerificationCode(email);
        return storedCode != null && storedCode.equals(inputCode);
    }





//    public void resendEmail(EmailRequest request) {
//        // 이메일 중복 체크
//        if (userService.existsByEmail(request.email())) {
//            throw new UserEmailDuplicationException();
//        }
//        // 인증 이메일 발송
//        emailService.resendMail(request.email());
//    }
//
//    @Transactional
//    public Long create(UserCreateRequest request) {
//        // 이메일 인증 코드 확인
//        boolean isValid = emailService.verifyCode(request.email(), request.code());
//        if (!isValid) {
//            throw new RuntimeException("인증 코드 다름"); // 추후 예외 처리 필요
//        }
//        // 회원가입 진행
//        return userService.create(request);
//    }
}
