package com.prography.api.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prography.api.attendance.domain.DepositHistory;
import com.prography.api.attendance.repository.DepositHistoryRepository;
import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.domain.Part;
import com.prography.api.cohort.domain.Team;
import com.prography.api.cohort.exception.CohortErrorCode;
import com.prography.api.cohort.repository.CohortRepository;
import com.prography.api.cohort.repository.PartRepository;
import com.prography.api.cohort.repository.TeamRepository;
import com.prography.api.global.error.BusinessException;
import com.prography.api.member.domain.CohortMember;
import com.prography.api.member.domain.Member;
import com.prography.api.member.dto.MemberRequestDTO;
import com.prography.api.member.dto.MemberResponseDTO;
import com.prography.api.member.exception.AuthErrorCode;
import com.prography.api.member.repository.CohortMemberRepository;
import com.prography.api.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MemberCommandService {

	private final MemberRepository memberRepository;
	private final CohortRepository cohortRepository;
	private final TeamRepository teamRepository;
	private final PartRepository partRepository;
	private final DepositHistoryRepository depositHistoryRepository;
	private final PasswordEncoder passwordEncoder;
	private final CohortMemberRepository cohortMemberRepository;

	public MemberResponseDTO.CreateMemberResult createMember(MemberRequestDTO.CreateMember request) {

		if (memberRepository.findMemberByLoginId(request.loginId()).isPresent()) {
			throw new BusinessException(AuthErrorCode.DUPLICATED_LOGIN_ID);
		}

		Cohort cohort = cohortRepository.findById(request.cohortId())
			.orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_NOT_FOUND));

		Part part = null;
		if (request.partId() != null) {
			part = partRepository.findById(request.partId())
				.orElseThrow(() -> new BusinessException(CohortErrorCode.PART_NOT_FOUND));
		}

		Team team = null;
		if (request.teamId() != null) {
			team = teamRepository.findById(request.teamId())
				.orElseThrow(() -> new BusinessException(CohortErrorCode.TEAM_NOT_FOUND));
		}

		String encodedPassword = passwordEncoder.encode(request.password());

		Member member = Member.builder()
			.loginId(request.loginId())
			.password(encodedPassword)
			.name(request.name())
			.phone(request.phone())
			.build();

		CohortMember cohortMember = CohortMember.builder()
			.member(member)
			.cohort(cohort)
			.part(part)
			.team(team)
			.build();

		DepositHistory depositHistory = DepositHistory.builder()
			.cohortMember(cohortMember)
			.build();

		memberRepository.save(member);
		cohortMemberRepository.save(cohortMember);
		depositHistoryRepository.save(depositHistory);

		return MemberResponseDTO.CreateMemberResult.of(member, cohort, team, part);
	}
}
