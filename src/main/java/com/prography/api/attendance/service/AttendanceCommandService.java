package com.prography.api.attendance.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prography.api.attendance.domain.Attendance;
import com.prography.api.attendance.domain.AttendanceStatus;
import com.prography.api.attendance.domain.DepositHistory;
import com.prography.api.attendance.domain.DepositType;
import com.prography.api.attendance.dto.AttendanceRequestDTO;
import com.prography.api.attendance.dto.AttendanceResponseDTO;
import com.prography.api.attendance.exception.AttendanceErrorCode;
import com.prography.api.attendance.exception.DepositErrorCode;
import com.prography.api.attendance.repository.AttendanceRepository;
import com.prography.api.attendance.repository.DepositHistoryRepository;
import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.service.CohortManager;
import com.prography.api.global.error.BusinessException;
import com.prography.api.member.domain.CohortMember;
import com.prography.api.member.domain.Member;
import com.prography.api.member.domain.MemberStatus;
import com.prography.api.member.exception.MemberErrorCode;
import com.prography.api.member.repository.CohortMemberRepository;
import com.prography.api.member.repository.MemberRepository;
import com.prography.api.session.domain.Qrcode;
import com.prography.api.session.domain.Session;
import com.prography.api.session.domain.SessionStatus;
import com.prography.api.session.exception.QrcodeErrorCode;
import com.prography.api.session.exception.SessionErrorCode;
import com.prography.api.session.repository.QrcodeRepository;
import com.prography.api.session.repository.SessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AttendanceCommandService {

	private final QrcodeRepository qrcodeRepository;
	private final SessionRepository sessionRepository;
	private final MemberRepository memberRepository;
	private final AttendanceRepository attendanceRepository;
	private final CohortManager cohortManager;
	private final CohortMemberRepository cohortMemberRepository;
	private final DepositHistoryRepository depositHistoryRepository;

	public AttendanceResponseDTO.QrcodeAttendanceCheckResult qrcodeAttendanceCheck(
		AttendanceRequestDTO.QrcodeAttendanceCheck request) {

		Qrcode qrcode = qrcodeRepository.findByHashValue(request.hashValue())
			.orElseThrow(() -> new BusinessException(QrcodeErrorCode.QR_INVALID));

		if (qrcode.getExpiredAt().isBefore(Instant.now())) {
			throw new BusinessException(QrcodeErrorCode.QR_EXPIRED);
		}

		Session session = sessionRepository.findSessionById(qrcode.getSession().getId());

		if (session.getStatus() != SessionStatus.IN_PROGRESS) {
			throw new BusinessException(SessionErrorCode.SESSION_NOT_IN_PROGRESS);
		}

		Member member = memberRepository.findById(request.memberId())
			.orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

		if (member.getStatus() == MemberStatus.WITHDRAWN) {
			throw new BusinessException(MemberErrorCode.MEMBER_WITHDRAWN);
		}

		Attendance attendance = attendanceRepository.findBySessionAndMember(session, member)
			.orElse(null);

		if (attendance != null) {
			throw new BusinessException(AttendanceErrorCode.ATTENDANCE_ALREADY_CHECKED);
		}

		Cohort cohort = cohortManager.getCurrentCohort();
		CohortMember cohortMember = cohortMemberRepository.findByMemberAndCohort(member, cohort)
			.orElseThrow(() -> new BusinessException(MemberErrorCode.COHORT_MEMBER_NOT_FOUND));

		LocalDateTime datetime = LocalDateTime.of(session.getDate(), session.getTime());
		AttendanceStatus attendanceStatus =
			(LocalDateTime.now().isAfter(datetime)) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
		int penaltyAmount = 0;
		int lateMinutes = 0;

		if (attendanceStatus == AttendanceStatus.LATE) {
			Duration duration = Duration.between(datetime, LocalDateTime.now());
			lateMinutes = (int)duration.toMinutes();
			penaltyAmount = Math.min(lateMinutes * 500, 10000);
		}

		Attendance newAttendance = Attendance.builder()
			.session(session)
			.member(member)
			.checkedInAt(Instant.now())
			.status(attendanceStatus)
			.lateMinutes(lateMinutes)
			.penaltyAmount(penaltyAmount)
			.qrcode(qrcode)
			.reason(null)
			.build();
		attendanceRepository.save(newAttendance);

		if (penaltyAmount > 0) {
			if (cohortMember.getDeposit() < penaltyAmount) {
				throw new BusinessException(DepositErrorCode.DEPOSIT_INSUFFICIENT);
			}
			cohortMember.minusDeposit(penaltyAmount);
			DepositHistory depositHistory = DepositHistory.builder()
				.amount(penaltyAmount)
				.balanceAfter(cohortMember.getDeposit())
				.attendance(newAttendance)
				.cohortMember(cohortMember)
				.description(null)
				.type(DepositType.PENALTY)
				.build();

			depositHistoryRepository.save(depositHistory);
		}

		return AttendanceResponseDTO.QrcodeAttendanceCheckResult.from(newAttendance);
	}
}
