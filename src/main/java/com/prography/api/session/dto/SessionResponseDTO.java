package com.prography.api.session.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import com.prography.api.session.domain.Qrcode;
import com.prography.api.session.domain.Session;
import com.prography.api.session.domain.SessionStatus;

import lombok.Builder;

public class SessionResponseDTO {

	@Builder
	public record CreateSessionResult(
		Long id,
		Long cohortId,
		String title,
		LocalDate date,
		LocalTime time,
		String location,
		SessionStatus status,
		AttendanceSummary attendanceSummary,
		boolean qrActive,
		Instant createdAt,
		Instant updatedAt
	) {
		public static CreateSessionResult of(Session session, Qrcode qrcode) {
			return CreateSessionResult.builder()
				.id(session.getId())
				.cohortId(session.getCohort().getId())
				.title(session.getTitle())
				.date(session.getDate())
				.time(session.getTime())
				.location(session.getLocation())
				.status(session.getStatus())
				.attendanceSummary(new AttendanceSummary(0, 0, 0, 0, 0))
				.qrActive(qrcode != null && qrcode.getExpiredAt().isAfter(Instant.now()))
				.createdAt(session.getCreatedAt())
				.updatedAt(session.getUpdatedAt())
				.build();

		}
	}

	@Builder
	public record AttendanceSummary(
		Integer present,
		Integer absent,
		Integer late,
		Integer excused,
		Integer total
	) {
	}
}
