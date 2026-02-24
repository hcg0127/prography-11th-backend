package com.prography.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class MemberRequestDTO {

	public record CreateMember(
		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "로그인 아이디", example = "user1")
		String loginId,

		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "비밀번호", example = "password123")
		String password,

		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "이름", example = "홍길동")
		String name,

		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "전화번호", example = "010-1234-5678")
		String phone,

		@NotNull(message = "필수 입력값입니다.")
		@Schema(description = "기수 ID", example = "2")
		Long cohortId,

		@Schema(description = "파트 ID", example = "6")
		Long partId,

		@Schema(description = "팀 ID", example = "1")
		Long teamId
	) {
	}
}
