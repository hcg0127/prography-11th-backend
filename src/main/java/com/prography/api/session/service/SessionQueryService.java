package com.prography.api.session.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	public List<SessionResponseDTO.CreateSessionResult> getSessionListAdmin(
		SessionRequestDTO.GetSessionListAdmin request) {

		Cohort cohort = cohortRepository.findByGeneration(currentGeneration);
		List<Session> sessionList = sessionRepository.findSessionsByCohortAndCond(cohort, request);

		List<Long> sessionIdList = sessionList.stream().map(Session::getId).toList();

		Map<Long, Qrcode> qrMap = qrcodeRepository.findAllBySessionIdIn(sessionIdList).stream()
			.collect(Collectors.toMap(qr -> qr.getSession().getId(), qr -> qr, (oldV, newV) -> newV));

		Map<Long, SessionResponseDTO.AttendanceSummary> summaryMap = getSummariesBySessionIds(sessionIdList);

		return sessionList.stream()
			.map(session -> SessionResponseDTO.CreateSessionResult.of(
				session,
				qrMap.get(session.getId()),
				summaryMap.getOrDefault(session.getId(), SessionResponseDTO.AttendanceSummary.empty())
			))
			.toList();
	}

	private Map<Long, SessionResponseDTO.AttendanceSummary> getSummariesBySessionIds(List<Long> sessionIdList) {

		if (sessionIdList.isEmpty()) {
			return Collections.emptyMap();
		}

		List<Object[]> results = attendanceRepository.countStatusBySessionIds(sessionIdList);

		Map<Long, Map<AttendanceStatus, Integer>> groupedData = results.stream()
			.collect(Collectors.groupingBy(
				row -> (Long)row[0],
				Collectors.toMap(
					row -> (AttendanceStatus)row[1],
					row -> ((Number)row[2]).intValue()
				)
			));

		Map<Long, SessionResponseDTO.AttendanceSummary> summaryMap = new HashMap<>();

		for (Long sessionId : sessionIdList) {

			Map<AttendanceStatus, Integer> statusCounts = groupedData.getOrDefault(sessionId, Collections.emptyMap());

			summaryMap.put(sessionId, SessionResponseDTO.AttendanceSummary.fromMap(statusCounts));
		}

		return summaryMap;
	}
}
