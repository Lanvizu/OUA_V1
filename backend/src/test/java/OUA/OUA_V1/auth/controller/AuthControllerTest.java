package OUA.OUA_V1.auth.controller;

import OUA.OUA_V1.auth.controller.request.LoginRequest;
import OUA.OUA_V1.auth.exception.DeletedAccountException;
import OUA.OUA_V1.auth.exception.LoginFailedException;
import OUA.OUA_V1.auth.service.AuthService;
import OUA.OUA_V1.global.util.CookieManager;
import OUA.OUA_V1.util.ControllerTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doReturn;

@DisplayName("인증 컨트롤러 테스트")
class AuthControllerTest extends ControllerTest {

    @MockBean
    private CookieManager cookieManager;

    @MockBean
    private AuthService authService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "ValidPass123!";
    private static final String MOCK_TOKEN = "mock-auth-token";

    @DisplayName("로그인 성공 - 토큰 쿠키 반환")
    @Test
    void login_Success() {
        // Given
        LoginRequest request = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        // AuthFacade.login()이 MOCK_TOKEN을 반환하도록 설정
        given(authFacade.login(any(LoginRequest.class))).willReturn(MOCK_TOKEN);

        // CookieManager가 ResponseCookie를 반환하도록 설정
        ResponseCookie mockCookie = ResponseCookie.from("authToken", MOCK_TOKEN)
                .httpOnly(true).secure(false).domain("localhost").path("/")
                .sameSite("Lax").maxAge(3600).build();
        doReturn(mockCookie).when(cookieManager).createTokenCookie(MOCK_TOKEN);

        // When & Then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .header(HttpHeaders.SET_COOKIE, containsString("authToken=" + MOCK_TOKEN));
    }

    @DisplayName("로그인 실패 - 비밀번호 불일치 시 401 반환")
    @Test
    void login_Fail_InvalidPassword() {
        // Given
        LoginRequest request = new LoginRequest(TEST_EMAIL, "wrongPassword");
        // AuthFacade.login()이 예외를 던지도록 설정
        given(authFacade.login(any(LoginRequest.class))).willThrow(new LoginFailedException());

        // When & Then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/auth/login")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("로그인 실패 - 탈퇴 회원 시 401 반환")
    @Test
    void login_Fail_DeletedAccount() {
        // Given
        LoginRequest request = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        given(authFacade.login(any(LoginRequest.class))).willThrow(new DeletedAccountException());

        // When & Then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/v1/auth/login")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("로그아웃 성공 - 쿠키 삭제")
    @Test
    void logout_Success() {
        // Given
        ResponseCookie clearCookie = ResponseCookie.from("authToken")
                .httpOnly(true).secure(false).domain("localhost").path("/")
                .sameSite("Lax").maxAge(0).build();
        doReturn(clearCookie).when(cookieManager).clearTokenCookie();

        given(authService.isTokenValid(MOCK_TOKEN)).willReturn(true);
        given(cookieManager.extractToken(any(HttpServletRequest.class)))
                .willReturn(MOCK_TOKEN);

        // When & Then
        RestAssured.given()
                .cookie("authToken", MOCK_TOKEN)
                .when()
                .post("/v1/auth/logout")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .header(HttpHeaders.SET_COOKIE, containsString("authToken="));
    }

    @DisplayName("status 엔드포인트 - 200 OK")
    @Test
    void status_Success() {
        given(authService.isTokenValid(MOCK_TOKEN)).willReturn(true);
        given(cookieManager.extractToken(any(HttpServletRequest.class)))
                .willReturn(MOCK_TOKEN);

        RestAssured.given()
                .when()
                .get("/v1/auth/status")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}