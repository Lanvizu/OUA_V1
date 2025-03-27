package OUA.OUA_V1.member.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CodeVerificationRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일의 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "인증 코드를 입력해주세요")
        String code
        ) {
}
