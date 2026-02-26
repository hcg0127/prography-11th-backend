package com.prography.api.attendance.domain;

import java.time.Instant;

import com.prography.api.global.common.BaseTimeEntity;
import com.prography.api.member.domain.Member;
import com.prography.api.session.domain.Qrcode;
import com.prography.api.session.domain.Session;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attendances")
public class Attendance extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private AttendanceStatus status = AttendanceStatus.PRESENT;

	private int lateMinutes;

	@Column(nullable = false)
	@Builder.Default
	private int penaltyAmount = 0;

	private String reason;

	private Instant checkedInAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id")
	private Session session;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "qrcode_id")
	private Qrcode qrcode;
}
