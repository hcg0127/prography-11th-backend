package com.prography.api.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prography.api.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
}
