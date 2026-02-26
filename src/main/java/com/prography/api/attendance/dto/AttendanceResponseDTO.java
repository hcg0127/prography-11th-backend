package com.prography.api.attendance.dto;

import java.time.Instant;

import com.prography.api.attendance.domain.Attendance;
import com.prography.api.attendance.domain.AttendanceStatus;

import lombok.Builder;

public class AttendanceResponseDTO {

	@Builder
	public record QrcodeAttendanceCheckResult(
		Long id,
		Long sessionId,
		Long memberId,
		AttendanceStatus status,
		Integer lateMinutes,
		Integer penaltyAmount,
		String reason,
		Instant checkedInAt,
		Instant createdAt,
		Instant updatedAt
	) {
		public static QrcodeAttendanceCheckResult from(Attendance attendance) {
			return QrcodeAttendanceCheckResult.builder()
				.id(attendance.getId())
				.sessionId(attendance.getSession().getId())
				.memberId(attendance.getMember().getId())
				.status(attendance.getStatus())
				.lateMinutes(attendance.getLateMinutes())
				.penaltyAmount(attendance.getPenaltyAmount())
				.reason(attendance.getReason())
				.checkedInAt(attendance.getCheckedInAt())
				.createdAt(attendance.getCreatedAt())
				.updatedAt(attendance.getUpdatedAt())
				.build();
		}
	}
}
