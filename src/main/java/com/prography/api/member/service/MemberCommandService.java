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
import com.prography.api.member.exception.MemberErrorCode;
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

		Cohort cohort = getCohortOrThrow(request.cohortId());
		Part part = (request.partId() != null) ? getPartOrThrow(request.partId()) : null;
		Team team = (request.teamId() != null) ? getTeamOrThrow(request.teamId()) : null;

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

	private Team getTeamOrThrow(Long id) {
		return teamRepository.findById(id)
			.orElseThrow(() -> new BusinessException(CohortErrorCode.TEAM_NOT_FOUND));
	}

	private Part getPartOrThrow(Long id) {
		return partRepository.findById(id)
			.orElseThrow(() -> new BusinessException(CohortErrorCode.PART_NOT_FOUND));
	}

	private Cohort getCohortOrThrow(Long id) {
		return cohortRepository.findById(id)
			.orElseThrow(() -> new BusinessException(CohortErrorCode.COHORT_NOT_FOUND));
	}

	public MemberResponseDTO.CreateMemberResult updateMemberProfile(MemberRequestDTO.UpdateMember request, Long id) {

		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

		member.updateProfile(request.name(), request.phone());

		CohortMember cohortMember = null;

		if (request.cohortId() != null) {
			cohortMember = handleCohortAssignment(member, request);
		} else {
			cohortMember = cohortMemberRepository.findTopByMemberOrderByIdDesc(member)
				.orElse(null);
		}

		return MemberResponseDTO.CreateMemberResult.of(
			member,
			cohortMember != null ? cohortMember.getCohort() : null,
			cohortMember != null ? cohortMember.getTeam() : null,
			cohortMember != null ? cohortMember.getPart() : null
		);
	}

	private CohortMember handleCohortAssignment(Member member, MemberRequestDTO.UpdateMember request) {

		Cohort cohort = getCohortOrThrow(request.cohortId());
		Part part = (request.partId() != null) ? getPartOrThrow(request.partId()) : null;
		Team team = (request.teamId() != null) ? getTeamOrThrow(request.teamId()) : null;

		return cohortMemberRepository.findByMemberAndCohort(member, cohort)
			.map(existing -> {
				existing.updateAssignment(part, team);
				return existing;
			})
			.orElseGet(() -> cohortMemberRepository.save(
				CohortMember.builder()
					.member(member)
					.cohort(cohort)
					.team(team)
					.part(part)
					.build()
			));
	}
}
