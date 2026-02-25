package com.prography.api.session.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.prography.api.session.domain.SessionStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SessionRequestDTO {

	public record CreateSession(
		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "제목", example = "정기 모임")
		String title,

		@NotNull(message = "필수 입력값입니다.")
		@JsonFormat(pattern = "yyyy-MM-dd")
		@Schema(description = "날짜 (yyyy-MM-dd)", example = "2026-03-01")
		LocalDate date,

		@NotNull(message = "필수 입력값입니다.")
		@JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
		@Schema(description = "시간 (HH:mm)", type = "string", example = "14:00")
		LocalTime time,

		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "장소", example = "강남")
		String location
	) {
	}

	public record UpdateSession(
		@Schema(description = "제목", example = "정기 모임")
		String title,

		@JsonFormat(pattern = "yyyy-MM-dd")
		@Schema(description = "날짜 (yyyy-MM-dd)", example = "2026-03-01")
		LocalDate date,

		@JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
		@Schema(description = "시간 (HH:mm)", type = "string", example = "14:00")
		LocalTime time,

		@Schema(description = "장소", example = "강남")
		String location,

		@Schema(description = "상태 (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED", examples = "IN_PROGRESS")
		SessionStatus status
	) {
	}

	public record GetSessionListAdmin(
		@JsonFormat(pattern = "yyyy-MM-dd")
		@Schema(description = "시작 날짜 필터 (yyyy-MM-dd)", example = "2026-03-01")
		LocalDate dateFrom,

		@JsonFormat(pattern = "yyyy-MM-dd")
		@Schema(description = "종료 날짜 (yyyy-MM-dd)", example = "2026-03-01")
		LocalDate dateTo,

		@Schema(description = "상태 필터 (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED", examples = "SCHEDULED")
		SessionStatus status
	) {
	}
}
