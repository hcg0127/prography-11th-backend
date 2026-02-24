package com.prography.api.session.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
}
