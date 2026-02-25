package com.prography.api.session.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prography.api.attendance.repository.AttendanceRepository;
import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.repository.CohortRepository;
import com.prography.api.session.domain.Session;
import com.prography.api.session.domain.SessionStatus;
import com.prography.api.session.dto.SessionResponseDTO;
import com.prography.api.session.repository.QrcodeRepository;
import com.prography.api.session.repository.SessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SessionQueryService {

	private final SessionRepository sessionRepository;
	private final CohortRepository cohortRepository;
	private final QrcodeRepository qrcodeRepository;
	private final AttendanceRepository attendanceRepository;

	@Value("${app.current-cohort.generation}")
	private Integer currentGeneration;

	public List<SessionResponseDTO.SessionProfile> getSessionList() {

		Cohort cohort = cohortRepository.findByGeneration(currentGeneration);

		List<Session> sessionList = sessionRepository.findAllByCohortAndStatusNot(cohort, SessionStatus.CANCELLED);

		return sessionList.stream()
			.map(SessionResponseDTO.SessionProfile::from)
			.toList();
	}
}
