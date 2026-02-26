package com.prography.api.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class AttendanceRequestDTO {

	public record QrcodeAttendanceCheck(
		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "QR 코드 해시값", example = "550e8400-e29b-41d4-a716-446655440000")
		String hashValue,

		@NotNull(message = "필수 입력값입니다.")
		@Schema(description = "회원 ID", example = "1")
		Long memberId
	) {
	}
}
