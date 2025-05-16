package OUA.OUA_V1.member.controller.request;

import jakarta.validation.constraints.NotBlank;

public record NewPasswordRequest(

        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}
