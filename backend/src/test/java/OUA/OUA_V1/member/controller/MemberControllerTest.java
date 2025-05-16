package OUA.OUA_V1.member.controller;

import OUA.OUA_V1.auth.exception.IllegalTokenException;
import OUA.OUA_V1.member.controller.request.*;
import OUA.OUA_V1.util.ControllerTest;
import OUA.OUA_V1.util.fixture.MemberFixture;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("사용자 컨트롤러 테스트")
public class MemberControllerTest extends ControllerTest {

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        memberRepository.flush();
    }

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_CODE = "123456";
    private static final String MOCK_TOKEN = "mock-auth-token";

    @DisplayName("이메일 인증 요청 성공 - 200 반환")
    @Test
    void requestEmailVerification_Success() {
        // Given
        EmailRequest request = new EmailRequest(TEST_EMAIL);

        given(redisService.wasRecentlySent(TEST_EMAIL)).willReturn(false);

        // When & Then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/members/email-verification")
                .then()
                .statusCode(HttpStatus.OK.value());

        then(redisService).should().setLastSentTime(TEST_EMAIL);
        then(redisService).should().setVerificationCode(eq(TEST_EMAIL), anyString(), anyLong());
    }

    @DisplayName("인증 코드 검증 성공 - 토큰 반환")
    @Test
    void verifyCode_Success() {
        // Given
        CodeVerificationRequest request = new CodeVerificationRequest(TEST_EMAIL, TEST_CODE);

        given(redisService.getVerificationCode(TEST_EMAIL))
                .willReturn(TEST_CODE);
        given(tokenProvider.createToken(anyMap()))
                .willReturn(MOCK_TOKEN);

        // When & Then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/members/code-verification")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", equalTo(MOCK_TOKEN));

        then(redisService).should().deleteVerificationCode(TEST_EMAIL);
    }

    @DisplayName("회원가입 성공 - 201 Created")
    @Test
    void signup_Success() {
        // Given
        MemberCreateRequest request = new MemberCreateRequest(
                TEST_EMAIL,
                "홍길동",
                "nickname",
                "ValidPass123!",
                "01012345678",
                TEST_CODE
        );

        given(tokenProvider.extractClaim(MOCK_TOKEN, "email"))
                .willReturn(TEST_EMAIL);
        given(tokenProvider.isAlive(MOCK_TOKEN))
                .willReturn(true);

        // When & Then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .cookie("authToken", MOCK_TOKEN)
                .body(request)
                .when()
                .post("/v1/members/signup")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("비밀번호 변경 이메일 인증 요청 성공")
    @Test
    void requestPasswordUpdateEmailVerification_Success() {
        // Given
        memberRepository.save(MemberFixture.createTestUser(TEST_EMAIL));
        EmailRequest request = new EmailRequest(TEST_EMAIL);

        // When & Then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/members/password-update-email-verification")
                .then()
                .statusCode(HttpStatus.OK.value());

        then(redisService).should().setLastSentTime(TEST_EMAIL);
    }

    @DisplayName("잘못된 인증 코드 검증 시 400 에러 반환")
    @Test
    void verifyCode_InvalidCode() {
        // Given
        CodeVerificationRequest request = new CodeVerificationRequest(TEST_EMAIL, "wrong_code");
        given(redisService.getVerificationCode(TEST_EMAIL)).willReturn(TEST_CODE);

        // When & Then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/members/code-verification")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("비밀번호 업데이트 성공 - 200 OK")
    @Test
    void updatePassword_Success() {
        // Given
        memberRepository.save(MemberFixture.createTestUser(TEST_EMAIL));
        NewPasswordRequest request = new NewPasswordRequest("NewPass123!");

        given(tokenProvider.extractClaim(MOCK_TOKEN, "email"))
                .willReturn(TEST_EMAIL);
        given(tokenProvider.isAlive(MOCK_TOKEN))
                .willReturn(true);

        // When & Then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .cookie("authToken", MOCK_TOKEN)
                .body(request)
                .when()
                .patch("/v1/members/update-password")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("잘못된 토큰으로 비밀번호 변경 시도 - 401 Unauthorized")
    @Test
    void updatePassword_InvalidToken() {
        // Given
        NewPasswordRequest request = new NewPasswordRequest("NewPass123!");

        given(tokenProvider.isAlive(anyString()))
                .willThrow(new IllegalTokenException());

        // When & Then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .cookie("authToken", "invalid-token")
                .body(request)
                .when()
                .patch("/v1/members/update-password")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    // 로그인 테스트 이후 정보 호출 및 닉네임 수정 테스트 추가.

}
