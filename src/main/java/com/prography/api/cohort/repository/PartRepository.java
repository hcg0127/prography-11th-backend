package com.prography.api.cohort.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prography.api.cohort.domain.Part;

public interface PartRepository extends JpaRepository<Part, Long> {
}
