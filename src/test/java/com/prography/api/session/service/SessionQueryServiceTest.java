package com.prography.api.session.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.prography.api.attendance.domain.AttendanceStatus;
import com.prography.api.attendance.repository.AttendanceRepository;
import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.repository.CohortRepository;
import com.prography.api.session.domain.Qrcode;
import com.prography.api.session.domain.Session;
import com.prography.api.session.domain.SessionStatus;
import com.prography.api.session.dto.SessionRequestDTO;
import com.prography.api.session.dto.SessionResponseDTO;
import com.prography.api.session.repository.QrcodeRepository;
import com.prography.api.session.repository.SessionRepository;

@ExtendWith(MockitoExtension.class)
class SessionQueryServiceTest {

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private CohortRepository cohortRepository;

	@Mock
	private QrcodeRepository qrcodeRepository;

	@Mock
	private AttendanceRepository attendanceRepository;

	@InjectMocks
	private SessionQueryService sessionQueryService;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(sessionQueryService, "currentGeneration", 11);
	}

	@Nested
	@DisplayName("회원용 일정 목록 조회 테스트")
	class GetSessionListTest {

		@Test
		@DisplayName("성공: 현재 기수의 일정 중 취소되지 않은 일정 목록을 조회한다.")
		void success() {

			// given
			Cohort cohort = Cohort.builder()
				.id(1L)
				.generation(11)
				.build();

			Session session1 = Session.builder()
				.id(10L)
				.cohort(cohort) // 👈 중요!
				.title("OT")
				.status(SessionStatus.COMPLETED)
				.date(LocalDate.of(2026, 3, 1))
				.time(LocalTime.of(14, 0))
				.build();

			Session session2 = Session.builder()
				.id(11L)
				.cohort(cohort) // 👈 중요!
				.title("정기 세션")
				.status(SessionStatus.SCHEDULED)
				.date(LocalDate.of(2026, 3, 8))
				.time(LocalTime.of(14, 0))
				.build();

			given(cohortRepository.findByGeneration(11)).willReturn(cohort);

			given(sessionRepository.findAllByCohortAndStatusNot(cohort, SessionStatus.CANCELLED))
				.willReturn(List.of(session1, session2));

			// when
			List<SessionResponseDTO.SessionProfile> result = sessionQueryService.getSessionList();

			// then
			assertThat(result).hasSize(2);

			assertThat(result.get(0).title()).isEqualTo("OT");
			assertThat(result.get(0).status()).isEqualTo(SessionStatus.COMPLETED);

			assertThat(result.get(1).title()).isEqualTo("정기 세션");
			assertThat(result.get(1).status()).isEqualTo(SessionStatus.SCHEDULED);

			verify(cohortRepository).findByGeneration(11);
			verify(sessionRepository).findAllByCohortAndStatusNot(eq(cohort), eq(SessionStatus.CANCELLED));
		}

		@Test
		@DisplayName("성공: 조회된 일정이 없으면 빈 리스트를 반환한다.")
		void success_empty_list() {

			// given
			Cohort cohort = Cohort.builder().id(1L).generation(11).build();

			given(cohortRepository.findByGeneration(11)).willReturn(cohort);
			given(sessionRepository.findAllByCohortAndStatusNot(cohort, SessionStatus.CANCELLED))
				.willReturn(Collections.emptyList());

			// when
			List<SessionResponseDTO.SessionProfile> result = sessionQueryService.getSessionList();

			// then
			assertThat(result).isEmpty();
			assertThat(result).isNotNull();
		}
	}

	@Nested
	@DisplayName("관리자용 일정 목록 조회 테스트")
	class GetSessionListAdminTest {

		@Test
		@DisplayName("성공: 조건에 맞는 세션 목록을 조회하고, QR 및 출석 통계를 매핑하여 반환한다.")
		void success() {

			// given
			SessionRequestDTO.GetSessionListAdmin request = new SessionRequestDTO.GetSessionListAdmin(null, null, null);
			Cohort cohort = Cohort.builder().id(1L).generation(11).build();

			Session session1 = Session.builder()
				.id(1L).cohort(cohort).title("세션1").status(SessionStatus.SCHEDULED)
				.build();

			Session session2 = Session.builder()
				.id(2L).cohort(cohort).title("세션2").status(SessionStatus.COMPLETED)
				.build();

			Qrcode qrcode1 = Qrcode.builder()
				.id(100L)
				.session(session1)
				.expiredAt(Instant.now().plus(1, ChronoUnit.DAYS))
				.build();

			List<Object[]> attendanceResults = List.of(
				new Object[] {1L, AttendanceStatus.PRESENT, 10L},
				new Object[] {1L, AttendanceStatus.LATE, 3L}
			);

			given(cohortRepository.findByGeneration(11)).willReturn(cohort);
			given(sessionRepository.findSessionsByCohortAndCond(cohort, request))
				.willReturn(List.of(session1, session2));

			given(qrcodeRepository.findAllBySessionIdIn(anyList())).willReturn(List.of(qrcode1));
			given(attendanceRepository.countStatusBySessionIds(anyList())).willReturn(attendanceResults);

			// when
			List<SessionResponseDTO.CreateSessionResult> result = sessionQueryService.getSessionListAdmin(request);

			// then
			assertThat(result).hasSize(2);

			SessionResponseDTO.CreateSessionResult result1 = result.stream()
				.filter(r -> r.id().equals(1L)).findFirst().orElseThrow();

			assertThat(result1.title()).isEqualTo("세션1");
			assertThat(result1.qrActive()).isTrue();

			assertThat(result1.attendanceSummary().present()).isEqualTo(10);
			assertThat(result1.attendanceSummary().late()).isEqualTo(3);
			assertThat(result1.attendanceSummary().total()).isEqualTo(13);
			assertThat(result1.attendanceSummary().absent()).isEqualTo(0);

			SessionResponseDTO.CreateSessionResult result2 = result.stream()
				.filter(r -> r.id().equals(2L)).findFirst().orElseThrow();

			assertThat(result2.title()).isEqualTo("세션2");
			assertThat(result2.qrActive()).isFalse();
			assertThat(result2.attendanceSummary().total()).isEqualTo(0);

			verify(attendanceRepository).countStatusBySessionIds(anyList());
			verify(qrcodeRepository).findAllBySessionIdIn(anyList());
		}

		@Test
		@DisplayName("성공: 조회된 세션이 없는 경우 빈 리스트를 반환하며, 추가 쿼리는 실행되지 않거나 안전하게 처리된다.")
		void success_empty() {

			// given
			SessionRequestDTO.GetSessionListAdmin request = new SessionRequestDTO.GetSessionListAdmin(null, null, null);
			Cohort cohort = Cohort.builder().id(1L).generation(11).build();

			given(cohortRepository.findByGeneration(11)).willReturn(cohort);
			given(sessionRepository.findSessionsByCohortAndCond(cohort, request))
				.willReturn(Collections.emptyList());

			given(qrcodeRepository.findAllBySessionIdIn(Collections.emptyList())).willReturn(Collections.emptyList());

			// when
			List<SessionResponseDTO.CreateSessionResult> result = sessionQueryService.getSessionListAdmin(request);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("성공: 활성 QR은 true, 만료된 QR이나 없는 경우는 false로 반환한다.")
		void success_qr_active_false() {

			// given
			SessionRequestDTO.GetSessionListAdmin request = new SessionRequestDTO.GetSessionListAdmin(null, null, null);
			Cohort cohort = Cohort.builder().id(1L).generation(11).build();

			Session sessionActive = Session.builder()
				.id(1L).cohort(cohort).title("활성 세션").status(SessionStatus.SCHEDULED).build();

			Session sessionExpired = Session.builder()
				.id(2L).cohort(cohort).title("만료 세션").status(SessionStatus.COMPLETED).build();

			Session sessionNoQr = Session.builder()
				.id(3L).cohort(cohort).title("QR없는 세션").status(SessionStatus.SCHEDULED).build();

			Qrcode qrActive = Qrcode.builder()
				.id(100L).session(sessionActive)
				.expiredAt(Instant.now().plus(1, ChronoUnit.DAYS))
				.build();

			Qrcode qrExpired = Qrcode.builder()
				.id(101L).session(sessionExpired)
				.expiredAt(Instant.now().minus(1, ChronoUnit.DAYS))
				.build();

			given(cohortRepository.findByGeneration(11)).willReturn(cohort);
			given(sessionRepository.findSessionsByCohortAndCond(cohort, request))
				.willReturn(List.of(sessionActive, sessionExpired, sessionNoQr));

			given(qrcodeRepository.findAllBySessionIdIn(anyList()))
				.willReturn(List.of(qrActive, qrExpired));

			given(attendanceRepository.countStatusBySessionIds(anyList()))
				.willReturn(Collections.emptyList());

			// when
			List<SessionResponseDTO.CreateSessionResult> result = sessionQueryService.getSessionListAdmin(request);

			// then
			assertThat(result).hasSize(3);

			SessionResponseDTO.CreateSessionResult r1 = result.stream()
				.filter(r -> r.id().equals(1L))
				.findFirst()
				.get();
			assertThat(r1.qrActive()).isTrue();

			SessionResponseDTO.CreateSessionResult r2 = result.stream()
				.filter(r -> r.id().equals(2L))
				.findFirst()
				.get();
			assertThat(r2.qrActive()).isFalse();

			SessionResponseDTO.CreateSessionResult r3 = result.stream()
				.filter(r -> r.id().equals(3L))
				.findFirst()
				.get();
			assertThat(r3.qrActive()).isFalse();
		}
	}
}