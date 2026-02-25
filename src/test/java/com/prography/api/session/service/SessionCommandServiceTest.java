package com.prography.api.session.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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

@ExtendWith(MockitoExtension.class)
class SessionCommandServiceTest {

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private CohortRepository cohortRepository;

	@Mock
	private QrcodeRepository qrcodeRepository;

	@Mock
	private AttendanceRepository attendanceRepository;

	@InjectMocks
	private SessionCommandService sessionCommandService;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(sessionCommandService, "currentGeneration", 11);
	}

	@Nested
	@DisplayName("일정 생성 테스트")
	class CreateSessionTest {

		@Test
		@DisplayName("성공: 현재 기수 정보로 일정이 생성되고, QR 코드도 함께 생성된다.")
		void success() {

			// given
			SessionRequestDTO.CreateSession request = new SessionRequestDTO.CreateSession(
				"정기 모임",
				LocalDate.of(2026, 3, 1),
				LocalTime.of(14, 0),
				"강남");

			Cohort cohort = Cohort.builder().id(1L).generation(11).build();

			given(cohortRepository.findByGeneration(11)).willReturn(cohort);

			given(sessionRepository.save(any(Session.class))).willAnswer(invocation -> {
				Session s = invocation.getArgument(0);
				ReflectionTestUtils.setField(s, "id", 100L);
				return s;
			});

			// when
			SessionResponseDTO.CreateSessionResult result = sessionCommandService.createSession(request);

			// then
			ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
			verify(sessionRepository).save(sessionCaptor.capture());
			Session savedSession = sessionCaptor.getValue();

			assertThat(savedSession.getTitle()).isEqualTo("정기 모임");
			assertThat(savedSession.getStatus()).isEqualTo(SessionStatus.SCHEDULED);
			assertThat(savedSession.getCohort().getGeneration()).isEqualTo(11);

			ArgumentCaptor<Qrcode> qrCaptor = ArgumentCaptor.forClass(Qrcode.class);
			verify(qrcodeRepository).save(qrCaptor.capture());
			Qrcode savedQr = qrCaptor.getValue();

			assertThat(savedQr.getSession()).isEqualTo(savedSession);
			assertThat(savedQr.getHashValue()).isNotNull();

			assertThat(result.qrActive()).isTrue();
			assertThat(result.attendanceSummary().present()).isEqualTo(0);
		}
	}

	@Nested
	@DisplayName("일정 수정 테스트")
	class UpdateSessionTest {

		@Test
		@DisplayName("성공: 일정을 수정하면 최신 QR 코드를 조회하여 함께 반환한다.")
		void success_with_qr() {

			// given
			Long sessionId = 1L;
			Cohort cohort = Cohort.builder().id(1L).generation(11).build();

			SessionRequestDTO.UpdateSession request = new SessionRequestDTO.UpdateSession(
				"수정된 제목",
				LocalDate.of(2026, 4, 1),
				LocalTime.of(15, 0),
				"수정된 장소",
				SessionStatus.IN_PROGRESS);

			Session session = Session.builder()
				.id(sessionId)
				.title("원래 제목")
				.cohort(cohort)
				.status(SessionStatus.SCHEDULED)
				.build();

			Qrcode qrcode = Qrcode.builder()
				.id(10L)
				.session(session)
				.expiredAt(Instant.now().plus(1, ChronoUnit.DAYS))
				.build();

			given(sessionRepository.findById(sessionId)).willReturn(Optional.of(session));

			given(qrcodeRepository.findTopBySessionOrderByExpiredAtDesc(session))
				.willReturn(Optional.of(qrcode));

			given(attendanceRepository.countByStatusBySessionId(any())).willReturn(Collections.emptyList());
			given(attendanceRepository.countBySessionId(any())).willReturn(0);

			// when
			SessionResponseDTO.CreateSessionResult result = sessionCommandService.updateSession(sessionId, request);

			// then
			assertThat(result.title()).isEqualTo("수정된 제목");
			assertThat(result.status()).isEqualTo(SessionStatus.IN_PROGRESS);

			assertThat(result.qrActive()).isTrue();

			verify(qrcodeRepository).findTopBySessionOrderByExpiredAtDesc(session);
		}

		@Test
		@DisplayName("성공: QR 코드가 없는 경우(null)에도 일정 수정은 정상적으로 수행된다.")
		void success_no_qr() {

			// given
			Long sessionId = 1L;
			Cohort cohort = Cohort.builder().id(1L).generation(11).build();
			SessionRequestDTO.UpdateSession request = new SessionRequestDTO.UpdateSession(
				"수정된 제목", LocalDate.now(), LocalTime.now(), "장소", SessionStatus.COMPLETED);

			Session session = Session.builder()
				.id(sessionId)
				.cohort(cohort)
				.status(SessionStatus.SCHEDULED)
				.build();

			given(sessionRepository.findById(sessionId)).willReturn(Optional.of(session));

			given(qrcodeRepository.findTopBySessionOrderByExpiredAtDesc(session))
				.willReturn(Optional.empty());

			given(attendanceRepository.countByStatusBySessionId(any())).willReturn(Collections.emptyList());
			given(attendanceRepository.countBySessionId(any())).willReturn(0);

			// when
			SessionResponseDTO.CreateSessionResult result = sessionCommandService.updateSession(sessionId, request);

			// then
			assertThat(result.title()).isEqualTo("수정된 제목");

			assertThat(result.qrActive()).isFalse();
		}

		@Test
		@DisplayName("실패: 해당 ID의 일정이 존재하지 않으면 SESSION_NOT_FOUND(404) 에러가 발생한다.")
		void fail_session_not_found() {

			// given
			Long unknownId = 999L;
			SessionRequestDTO.UpdateSession request = new SessionRequestDTO.UpdateSession(
				"수정 시도", LocalDate.now(), LocalTime.now(), "장소", SessionStatus.IN_PROGRESS);

			given(sessionRepository.findById(unknownId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> sessionCommandService.updateSession(unknownId, request))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(SessionErrorCode.SESSION_NOT_FOUND);

			verify(qrcodeRepository, never()).findTopBySessionOrderByExpiredAtDesc(any());
			verify(attendanceRepository, never()).countByStatusBySessionId(any());
			verify(attendanceRepository, never()).countBySessionId(any());
		}

		@Test
		@DisplayName("실패: 이미 취소된 일정을 다시 취소하려 하면 SESSION_ALREADY_CANCELLED 예외가 발생한다.")
		void fail_already_cancelled() {

			// given
			Long sessionId = 1L;
			SessionRequestDTO.UpdateSession request = new SessionRequestDTO.UpdateSession(
				"제목", LocalDate.now(), LocalTime.now(), "장소", SessionStatus.CANCELLED);

			Session session = Session.builder()
				.id(sessionId)
				.status(SessionStatus.CANCELLED)
				.build();

			given(sessionRepository.findById(sessionId)).willReturn(Optional.of(session));

			// when & then
			assertThatThrownBy(() -> sessionCommandService.updateSession(sessionId, request))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(SessionErrorCode.SESSION_ALREADY_CANCELLED);

			verify(qrcodeRepository, never()).findTopBySessionOrderByExpiredAtDesc(any());
			verify(attendanceRepository, never()).countByStatusBySessionId(any());
		}
	}

	@Nested
	@DisplayName("일정 삭제(취소) 테스트")
	class DeleteSessionTest {

		@Test
		@DisplayName("성공: SCHEDULED 상태인 일정을 삭제하면 CANCELLED로 변경된다.")
		void success() {

			// given
			Long sessionId = 1L;
			Cohort cohort = Cohort.builder().id(1L).generation(11).build();
			Session session = Session.builder()
				.id(sessionId).cohort(cohort).status(SessionStatus.SCHEDULED).build();

			given(sessionRepository.findById(sessionId)).willReturn(Optional.of(session));

			given(qrcodeRepository.findTopBySessionOrderByExpiredAtDesc(session)).willReturn(Optional.empty());
			given(attendanceRepository.countByStatusBySessionId(sessionId)).willReturn(Collections.emptyList());
			given(attendanceRepository.countBySessionId(sessionId)).willReturn(0);

			// when
			SessionResponseDTO.CreateSessionResult result = sessionCommandService.deleteSession(sessionId);

			// then
			assertThat(result.status()).isEqualTo(SessionStatus.CANCELLED);
			verify(sessionRepository).findById(sessionId);
		}

		@Test
		@DisplayName("실패: 해당 ID의 일정이 존재하지 않으면 SESSION_NOT_FOUND(404) 에러가 발생한다.")
		void fail_session_not_found() {

			// given
			Long unknownId = 99L;
			given(sessionRepository.findById(unknownId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> sessionCommandService.deleteSession(unknownId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(SessionErrorCode.SESSION_NOT_FOUND);

			verify(attendanceRepository, never()).countByStatusBySessionId(any());
		}

		@Test
		@DisplayName("실패: 이미 취소된(CANCELLED) 상태의 일정을 삭제하려 하면 SESSION_ALREADY_CANCELLED(400) 에러가 발생한다.")
		void fail_already_cancelled() {

			// given
			Long sessionId = 1L;

			Session alreadyCancelledSession = Session.builder()
				.id(sessionId)
				.status(SessionStatus.CANCELLED)
				.build();

			given(sessionRepository.findById(sessionId)).willReturn(Optional.of(alreadyCancelledSession));

			// when & then
			assertThatThrownBy(() -> sessionCommandService.deleteSession(sessionId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(SessionErrorCode.SESSION_ALREADY_CANCELLED);

			verify(attendanceRepository, never()).countByStatusBySessionId(any());
		}
	}

	@Nested
	@DisplayName("QR 코드 생성 테스트")
	class CreateQrcodeTest {

		@Test
		@DisplayName("성공: 이전에 발급된 QR 코드가 없으면 새 QR 코드를 생성한다.")
		void success_first_time() {

			// given
			Long sessionId = 1L;
			Cohort cohort = Cohort.builder().id(1L).generation(11).build();
			Session session = Session.builder().id(sessionId).cohort(cohort).build();

			given(sessionRepository.findById(sessionId)).willReturn(Optional.of(session));

			given(qrcodeRepository.findTopBySessionOrderByExpiredAtDesc(session))
				.willReturn(Optional.empty());

			// when
			SessionResponseDTO.CreateQrcodeResult result = sessionCommandService.createQrcode(sessionId);

			// then
			assertThat(result.sessionId()).isEqualTo(sessionId);
			assertThat(result.hashValue()).isNotNull();

			verify(qrcodeRepository).save(any(Qrcode.class));
		}

		@Test
		@DisplayName("성공: 기존 QR 코드가 있지만 이미 만료된 경우, 새 QR 코드를 생성한다.")
		void success_when_expired_exists() {

			// given
			Long sessionId = 1L;
			Cohort cohort = Cohort.builder().id(1L).generation(11).build();
			Session session = Session.builder().id(sessionId).cohort(cohort).build();

			Qrcode expiredQrcode = Qrcode.builder()
				.session(session)
				.expiredAt(Instant.now().minus(1, ChronoUnit.DAYS))
				.build();

			given(sessionRepository.findById(sessionId)).willReturn(Optional.of(session));
			given(qrcodeRepository.findTopBySessionOrderByExpiredAtDesc(session))
				.willReturn(Optional.of(expiredQrcode));

			// when
			SessionResponseDTO.CreateQrcodeResult result = sessionCommandService.createQrcode(sessionId);

			// then
			assertThat(result.hashValue()).isNotNull();
			verify(qrcodeRepository).save(any(Qrcode.class));
		}

		@Test
		@DisplayName("실패: 해당 세션에 아직 만료되지 않은(활성) QR 코드가 있다면 QR_ALREADY_ACTIVE 예외가 발생한다.")
		void fail_active_qr_exists() {

			// given
			Long sessionId = 1L;
			Session session = Session.builder().id(sessionId).build();

			Qrcode activeQrcode = Qrcode.builder()
				.session(session)
				.expiredAt(Instant.now().plus(1, ChronoUnit.DAYS))
				.build();

			given(sessionRepository.findById(sessionId)).willReturn(Optional.of(session));
			given(qrcodeRepository.findTopBySessionOrderByExpiredAtDesc(session))
				.willReturn(Optional.of(activeQrcode));

			// when & then
			assertThatThrownBy(() -> sessionCommandService.createQrcode(sessionId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(QrcodeErrorCode.QR_ALREADY_ACTIVE);

			verify(qrcodeRepository, never()).save(any());
		}

		@Test
		@DisplayName("실패: 세션을 찾을 수 없으면 SESSION_NOT_FOUND 예외가 발생한다.")
		void fail_session_not_found() {

			// given
			Long unknownId = 999L;
			given(sessionRepository.findById(unknownId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> sessionCommandService.createQrcode(unknownId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(SessionErrorCode.SESSION_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("QR 코드 갱신(Refresh) 테스트")
	class RefreshQrcodeTest {

		@Test
		@DisplayName("성공: 기존 QR 코드를 만료시키고, 동일한 세션의 새 QR 코드를 발급한다.")
		void success() {
			
			// given
			Long qrCodeId = 100L;
			Long sessionId = 1L;

			Session session = Session.builder().id(sessionId).build();

			Qrcode oldQrcode = Qrcode.builder()
				.id(qrCodeId)
				.session(session)
				.expiredAt(Instant.now().plus(1, ChronoUnit.DAYS))
				.build();

			given(qrcodeRepository.findById(qrCodeId)).willReturn(Optional.of(oldQrcode));

			ArgumentCaptor<Qrcode> newQrCaptor = ArgumentCaptor.forClass(Qrcode.class);

			// when
			SessionResponseDTO.CreateQrcodeResult result = sessionCommandService.refreshQrcode(qrCodeId);

			// then
			assertThat(oldQrcode.getExpiredAt()).isBeforeOrEqualTo(Instant.now());

			verify(qrcodeRepository).save(newQrCaptor.capture());
			Qrcode newQrcode = newQrCaptor.getValue();

			assertThat(newQrcode.getSession().getId()).isEqualTo(sessionId); // 세션 유지 확인
			assertThat(newQrcode.getHashValue()).isNotNull();
			assertThat(newQrcode.getHashValue()).isNotEqualTo(oldQrcode.getHashValue()); // 해시 변경 확인
			assertThat(newQrcode.getExpiredAt()).isAfter(Instant.now()); // 미래 시간인지 확인

			assertThat(result.hashValue()).isEqualTo(newQrcode.getHashValue());
		}

		@Test
		@DisplayName("실패: 요청한 QR 코드 ID가 존재하지 않으면 QR_NOT_FOUND 예외가 발생한다.")
		void fail_qr_not_found() {

			// given
			Long unknownQrId = 999L;
			given(qrcodeRepository.findById(unknownQrId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> sessionCommandService.refreshQrcode(unknownQrId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(QrcodeErrorCode.QR_NOT_FOUND);

			verify(qrcodeRepository, never()).save(any());
		}
	}
}