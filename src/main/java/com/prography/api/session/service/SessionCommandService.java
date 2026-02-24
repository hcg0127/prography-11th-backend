package com.prography.api.session.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.repository.CohortRepository;
import com.prography.api.session.domain.Qrcode;
import com.prography.api.session.domain.Session;
import com.prography.api.session.dto.SessionRequestDTO;
import com.prography.api.session.dto.SessionResponseDTO;
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

		sessionRepository.save(session);
		qrcodeRepository.save(qrcode);

		return SessionResponseDTO.CreateSessionResult.of(session, qrcode);
	}
}
