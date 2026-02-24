package com.prography.api.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prography.api.cohort.domain.Cohort;
import com.prography.api.member.domain.CohortMember;
import com.prography.api.member.domain.Member;

public interface CohortMemberRepository extends JpaRepository<CohortMember, Long>, CohortMemberRepositoryCustom {

	CohortMember findByMember(Member member);

	Optional<CohortMember> findByMemberAndCohort(Member member, Cohort cohort);

	Optional<CohortMember> findTopByMemberOrderByIdDesc(Member member);
}
