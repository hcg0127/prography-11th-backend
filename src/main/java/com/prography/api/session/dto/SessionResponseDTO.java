package com.prography.api.session.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.prography.api.attendance.domain.AttendanceStatus;
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
		public static CreateSessionResult of(Session session, Qrcode qrcode, AttendanceSummary attendanceSummary) {

			boolean qrActive = qrcode != null
				&& qrcode.getExpiredAt().isAfter(Instant.now())
				&& session.getStatus() != SessionStatus.CANCELLED;

			return CreateSessionResult.builder()
				.id(session.getId())
				.cohortId(session.getCohort().getId())
				.title(session.getTitle())
				.date(session.getDate())
				.time(session.getTime())
				.location(session.getLocation())
				.status(session.getStatus())
				.attendanceSummary(attendanceSummary)
				.qrActive(qrActive)
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
		public static AttendanceSummary from(List<Object[]> results, Integer total) {

			Integer present = 0, absent = 0, late = 0, excused = 0;

			for (Object[] result : results) {
				AttendanceStatus status = (AttendanceStatus)result[0];
				Integer count = (Integer)result[1];

				switch (status) {
					case PRESENT -> present = count;
					case ABSENT -> absent = count;
					case LATE -> late = count;
					case EXCUSED -> excused = count;
				}
			}

			return new AttendanceSummary(present, absent, late, excused, total);
		}

		public static AttendanceSummary fromMap(Map<AttendanceStatus, Integer> counts) {
			Integer present = counts.getOrDefault(AttendanceStatus.PRESENT, 0);
			Integer absent = counts.getOrDefault(AttendanceStatus.ABSENT, 0);
			Integer late = counts.getOrDefault(AttendanceStatus.LATE, 0);
			Integer excused = counts.getOrDefault(AttendanceStatus.EXCUSED, 0);

			Integer total = present + absent + late + excused;

			return new AttendanceSummary(present, absent, late, excused, total);
		}

		public static AttendanceSummary empty() {
			return new AttendanceSummary(0, 0, 0, 0, 0);
		}
	}

	@Builder
	public record SessionProfile(
		Long id,
		String title,
		LocalDate date,
		LocalTime time,
		String location,
		SessionStatus status,
		Instant createdAt,
		Instant updatedAt
	) {
		public static SessionProfile from(Session session) {
			return SessionProfile.builder()
				.id(session.getId())
				.title(session.getTitle())
				.date(session.getDate())
				.time(session.getTime())
				.location(session.getLocation())
				.status(session.getStatus())
				.createdAt(session.getCreatedAt())
				.updatedAt(session.getUpdatedAt())
				.build();
		}
	}

	@Builder
	public record CreateQrcodeResult(
		Long id,
		Long sessionId,
		String hashValue,
		Instant createdAt,
		Instant updatedAt
	) {
		public static CreateQrcodeResult of(Qrcode qrcode) {
			return CreateQrcodeResult.builder()
				.id(qrcode.getId())
				.sessionId(qrcode.getSession().getId())
				.hashValue(qrcode.getHashValue())
				.createdAt(qrcode.getCreatedAt())
				.updatedAt(qrcode.getUpdatedAt())
				.build();
		}
	}
}
