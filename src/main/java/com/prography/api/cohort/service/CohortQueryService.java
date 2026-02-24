package com.prography.api.cohort.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.dto.CohortResponseDTO;
import com.prography.api.cohort.repository.CohortRepository;

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
}
