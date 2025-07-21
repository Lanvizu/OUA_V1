package OUA.OUA_V1.member.facade;

import OUA.OUA_V1.auth.exception.IllegalTokenException;
import OUA.OUA_V1.auth.security.TokenProvider;
import OUA.OUA_V1.auth.service.AuthService;
import OUA.OUA_V1.global.service.RedisService;
import OUA.OUA_V1.member.controller.request.CodeVerificationRequest;
import OUA.OUA_V1.member.controller.request.EmailRequest;
import OUA.OUA_V1.member.controller.request.MemberCreateRequest;
import OUA.OUA_V1.member.controller.request.NewPasswordRequest;
import OUA.OUA_V1.member.domain.Member;
import OUA.OUA_V1.member.exception.MemberEmailDuplicationException;
import OUA.OUA_V1.member.exception.MemberEmailSendLimitException;
import OUA.OUA_V1.member.exception.MemberNotFoundException;
import OUA.OUA_V1.member.exception.badRequest.MemberCodeVerificationException;
import OUA.OUA_V1.member.service.EmailService;
import OUA.OUA_V1.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberService memberService;
    private final EmailService emailService;
    private final RedisService redisService;
    private final TokenProvider tokenProvider;
    private final AuthService authService;

    private static final long VERIFICATION_EXPIRATION_MILLIS = 10 * 60 * 1000; //10분

    public void requestEmailVerification(EmailRequest request) {
        validateEmailAvailability(request.email());
        sendEmailVerification(request);
    }

    public void requestPasswordUpdateEmailVerification(EmailRequest request) {
        validatePasswordUpdateEmailAvailability(request.email());
        sendEmailVerification(request);
    }

    private void validatePasswordUpdateEmailAvailability(String email) {
        if (!memberService.existsByEmail(email)) {
            throw new MemberNotFoundException();
        }
    }

    private void validateEmailAvailability(String email) {
        if (memberService.existsByEmail(email)) {
            throw new MemberEmailDuplicationException();
        }
    }

    private void sendEmailVerification(EmailRequest request) {
        if (redisService.wasRecentlySent(request.email())) {
            throw new MemberEmailSendLimitException();
        }
        String code = generateRandomCode();
        emailService.sendVerificationEmail(request.email(), code);
        redisService.setVerificationCode(request.email(), code, VERIFICATION_EXPIRATION_MILLIS);
        redisService.setLastSentTime(request.email());
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1_000_000));
    }

    public String verifyEmailCode(CodeVerificationRequest request) {
        String storedCode = redisService.getVerificationCode(request.email());
        if (storedCode != null && storedCode.equals(request.code())) {
            Map<String, Object> claims = Map.of("email", request.email());
            String token = tokenProvider.createToken(claims);
            redisService.deleteVerificationCode(request.email());
            return token;
        } else {
            throw new MemberCodeVerificationException();
        }
    }

    @Transactional
    public Long create(MemberCreateRequest request, String token) {
        validateToken(token, request.email());
        Member savedMember = memberService.create(request.email(), request.name(),
                request.nickName(), request.password(), request.phone());
        return savedMember.getId();
    }

    @Transactional
    public void updatePassword(NewPasswordRequest request, String token) {
        String email = authService.extractEmail(token);
        memberService.updatePassword(email, request.password());
    }

    private void validateToken(String token, String email) {
        if (token == null || !tokenProvider.isAlive(token)) {
            throw new IllegalTokenException();
        }
        String emailFromToken = tokenProvider.extractClaim(token, "email");
        if (!emailFromToken.equals(email)) {
            throw new IllegalTokenException();
        }
    }
}
