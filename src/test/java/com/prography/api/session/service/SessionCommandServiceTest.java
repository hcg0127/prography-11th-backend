package com.prography.api.session.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalTime;

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
class SessionCommandServiceTest {

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private CohortRepository cohortRepository;

	@Mock
	private QrcodeRepository qrcodeRepository;

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
		@DisplayName("성공: 현재 기수 정보로 세션이 생성되고, QR 코드도 함께 생성된다.")
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
}