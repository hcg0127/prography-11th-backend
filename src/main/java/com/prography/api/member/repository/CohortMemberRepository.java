package com.prography.api.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prography.api.member.domain.CohortMember;

public interface CohortMemberRepository extends JpaRepository<CohortMember, Long> {
}
