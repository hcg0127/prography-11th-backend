package com.prography.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public class AuthRequestDTO {

	public record AuthLogin(
		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "로그인 아이디", example = "admin")
		String loginId,

		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "비밀번호", example = "admin1234")
		String password
	) {
	}
}
