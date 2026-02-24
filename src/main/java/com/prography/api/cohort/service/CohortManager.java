package com.prography.api.cohort.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.exception.CohortErrorCode;
import com.prography.api.cohort.repository.CohortRepository;
import com.prography.api.global.error.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CohortManager {

	private final CohortRepository cohortRepository;

	@Transactional(readOnly = true)
	@Cacheable(value = "currentCohort")
	public Cohort getCurrentCohort() {

		return cohortRepository.findFirstByActiveTrue()
			.orElseThrow(() -> new BusinessException(CohortErrorCode.ACTIVE_COHORT_NOT_FOUND));
	}
}
