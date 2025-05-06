package OUA.OUA_V1.member.facade;

import OUA.OUA_V1.auth.security.TokenProvider;
import OUA.OUA_V1.global.service.RedisService;
import OUA.OUA_V1.member.controller.request.CodeVerificationRequest;
import OUA.OUA_V1.member.controller.request.EmailRequest;
import OUA.OUA_V1.util.ServiceTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@DisplayName("사용자 파사드 테스트")
public class MemberFacadeTest extends ServiceTest {
    @Autowired
    private MemberFacade memberFacade;

    @MockBean
    private RedisService redisService;

    @MockBean
    private TokenProvider tokenProvider;

    @BeforeEach
    void setup() {
        MimeMessage mockMessage = mock(MimeMessage.class);
        given(mailSender.createMimeMessage()).willReturn(mockMessage);
    }

    // 회원가입 인증 요청 테스트
    @Test
    @DisplayName("이메일 인증 요청 성공 - 중복 가입 없음")
    void requestEmailVerification_Success() {
        // given
        EmailRequest request = new EmailRequest("new@email.com");
        given(redisService.wasRecentlySent(anyString())).willReturn(false);

        // when
        memberFacade.requestEmailVerification(request);

        // then
        then(redisService).should().setVerificationCode(eq(request.email()), anyString(), anyLong());
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 성공")
    void verifyEmailCode_Success() {
        // given
        CodeVerificationRequest request = new CodeVerificationRequest("test@email.com", "123456");
        given(redisService.getVerificationCode(anyString())).willReturn("123456");
        given(tokenProvider.createToken(anyMap())).willReturn("valid_token");

        // when
        String resultToken = memberFacade.verifyEmailCode(request);

        // then
        assertThat(resultToken).isNotBlank();
        then(redisService).should().deleteVerificationCode(request.email());
    }
}
