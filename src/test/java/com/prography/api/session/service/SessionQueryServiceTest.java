package com.prography.api.session.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
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

import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.repository.CohortRepository;
import com.prography.api.session.domain.Session;
import com.prography.api.session.domain.SessionStatus;
import com.prography.api.session.dto.SessionResponseDTO;
import com.prography.api.session.repository.SessionRepository;

@ExtendWith(MockitoExtension.class)
class SessionQueryServiceTest {

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private CohortRepository cohortRepository;

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
}