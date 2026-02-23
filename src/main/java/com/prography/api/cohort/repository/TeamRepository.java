package com.prography.api.cohort.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prography.api.cohort.domain.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
