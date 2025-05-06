package OUA.OUA_V1.util;

import OUA.OUA_V1.auth.security.TokenProvider;
import OUA.OUA_V1.global.service.RedisService;
import OUA.OUA_V1.member.repository.MemberRepository;
import io.restassured.RestAssured;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ControllerTest {

    protected static final String TEST_EMAIL = "test@example.com";
    protected static final String TEST_CODE = "123456";
    protected static final String MOCK_TOKEN = "mock-auth-token";

    @LocalServerPort
    protected int port;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected DbCleaner dbCleaner;

    @MockBean
    protected JavaMailSender javaMailSender;

    @MockBean
    protected RedisService redisService;

    @MockBean
    protected TokenProvider tokenProvider;

    @BeforeEach
    void setup() {
        // 포트 설정
        RestAssured.port = port;

        // 메일 발송 Mock 설정
        doReturn(mock(MimeMessage.class))
                .when(javaMailSender).createMimeMessage();
        doNothing()
                .when(javaMailSender).send(any(MimeMessage.class));

        // 기본 토큰 동작 설정
        given(tokenProvider.createToken(anyMap()))
                .willReturn(MOCK_TOKEN);
        given(tokenProvider.isAlive(anyString()))
                .willReturn(true);

        // Redis 기본 동작 설정
        given(redisService.wasRecentlySent(anyString()))
                .willReturn(false);
    }

    protected void setupVerificationSuccess(String email, String code) {
        given(redisService.getVerificationCode(email))
                .willReturn(code);
    }

    protected void setupTokenValidation(String email) {
        given(tokenProvider.extractClaim(MOCK_TOKEN, "email"))
                .willReturn(email);
    }
}
