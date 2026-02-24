package com.prography.api.cohort.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.domain.Part;
import com.prography.api.cohort.domain.Team;
import com.prography.api.cohort.dto.CohortResponseDTO;
import com.prography.api.cohort.exception.CohortErrorCode;
import com.prography.api.cohort.repository.CohortRepository;
import com.prography.api.global.error.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CohortQueryService {

	private final CohortRepository cohortRepository;

	public List<CohortResponseDTO.GetCohortResult> getCohortList() {

		List<Cohort> cohortList = cohortRepository.findAll();

		return cohortList.stream()
			.map(CohortResponseDTO.GetCohortResult::from)
			.toList();
	}

	public CohortResponseDTO.GetCohortDetailResult getCohortDetail(Long cohortId) {

		Cohort cohort = cohortRepository.findById(cohortId)
			.orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_NOT_FOUND));

		List<Part> partList = cohort.getPartList();
		List<Team> teamList = cohort.getTeamList();

		return CohortResponseDTO.GetCohortDetailResult.of(
			cohort, partList, teamList);
	}
}
