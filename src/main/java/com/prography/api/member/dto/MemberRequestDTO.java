package com.prography.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public class MemberRequestDTO {

	public record authLogin(
		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "로그인 아이디", example = "admin")
		String loginId,

		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "비밀번호", example = "admin1234")
		String password
	) {
	}
}
