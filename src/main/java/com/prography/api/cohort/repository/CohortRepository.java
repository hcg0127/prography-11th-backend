package com.prography.api.cohort.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prography.api.cohort.domain.Cohort;

public interface CohortRepository extends JpaRepository<Cohort, Long>, CohortRepositoryCustom {
}
