package com.prography.api.session.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prography.api.attendance.repository.AttendanceRepository;
import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.repository.CohortRepository;
import com.prography.api.global.error.BusinessException;
import com.prography.api.session.domain.Qrcode;
import com.prography.api.session.domain.Session;
import com.prography.api.session.domain.SessionStatus;
import com.prography.api.session.dto.SessionRequestDTO;
import com.prography.api.session.dto.SessionResponseDTO;
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
public class SessionCommandService {

	private final SessionRepository sessionRepository;
	private final CohortRepository cohortRepository;
	private final QrcodeRepository qrcodeRepository;
	private final AttendanceRepository attendanceRepository;

	@Value("${app.current-cohort.generation}")
	private Integer currentGeneration;

	public SessionResponseDTO.CreateSessionResult createSession(SessionRequestDTO.CreateSession request) {

		Cohort cohort = cohortRepository.findByGeneration(currentGeneration);

		Session session = Session.builder()
			.title(request.title())
			.date(request.date())
			.time(request.time())
			.location(request.location())
			.cohort(cohort)
			.build();

		Qrcode qrcode = Qrcode.builder()
			.hashValue(UUID.randomUUID().toString())
			.session(session)
			.expiredAt(Instant.now().plus(1, ChronoUnit.DAYS))
			.build();

		SessionResponseDTO.AttendanceSummary summary = SessionResponseDTO.AttendanceSummary.empty();

		sessionRepository.save(session);
		qrcodeRepository.save(qrcode);

		return SessionResponseDTO.CreateSessionResult.of(session, qrcode, summary);
	}

	private Session validateSession(Long id) {

		Session session = sessionRepository.findById(id)
			.orElseThrow(() -> new BusinessException(SessionErrorCode.SESSION_NOT_FOUND));

		if (session.getStatus() == SessionStatus.CANCELLED) {
			throw new BusinessException(SessionErrorCode.SESSION_ALREADY_CANCELLED);
		}

		return session;
	}

	private SessionResponseDTO.CreateSessionResult getCreateSessionResult(Session session) {
		Qrcode qrcode = qrcodeRepository.findTopBySessionOrderByExpiredAtDesc(session)
			.orElse(null);

		List<Object[]> statusCounts = attendanceRepository.countByStatusBySessionId(session.getId());
		Integer totalAttendance = attendanceRepository.countBySessionId(session.getId());

		SessionResponseDTO.AttendanceSummary summary = SessionResponseDTO.AttendanceSummary
			.from(statusCounts, totalAttendance);

		return SessionResponseDTO.CreateSessionResult.of(session, qrcode, summary);
	}

	public SessionResponseDTO.CreateSessionResult updateSession(Long id, SessionRequestDTO.UpdateSession request) {

		Session session = validateSession(id);

		session.updateSession(request);

		return getCreateSessionResult(session);
	}

	public SessionResponseDTO.CreateSessionResult deleteSession(Long id) {

		Session session = validateSession(id);

		session.deleteSession();

		return getCreateSessionResult(session);
	}

	public SessionResponseDTO.CreateQrcodeResult createQrcode(Long sessionId) {

		Session session = sessionRepository.findById(sessionId)
			.orElseThrow(() -> new BusinessException(SessionErrorCode.SESSION_NOT_FOUND));

		Qrcode qrcode = qrcodeRepository.findTopBySessionOrderByExpiredAtDesc(session)
			.orElse(null);

		if (qrcode != null && qrcode.getExpiredAt().isAfter(Instant.now())) {
			throw new BusinessException(QrcodeErrorCode.QR_ALREADY_ACTIVE);
		}

		Qrcode newQrcode = Qrcode.builder()
			.hashValue(UUID.randomUUID().toString())
			.session(session)
			.expiredAt(Instant.now().plus(1, ChronoUnit.DAYS))
			.build();

		qrcodeRepository.save(newQrcode);

		return SessionResponseDTO.CreateQrcodeResult.of(newQrcode);
	}

	public SessionResponseDTO.CreateQrcodeResult refreshQrcode(Long qrCodeId) {

		Qrcode qrcode = qrcodeRepository.findById(qrCodeId)
			.orElseThrow(() -> new BusinessException(QrcodeErrorCode.QR_NOT_FOUND));

		qrcode.expire();

		Qrcode newQrcode = Qrcode.builder()
			.hashValue(UUID.randomUUID().toString())
			.session(qrcode.getSession())
			.expiredAt(Instant.now().plus(1, ChronoUnit.DAYS))
			.build();

		qrcodeRepository.save(newQrcode);

		return SessionResponseDTO.CreateQrcodeResult.of(newQrcode);
	}
}
