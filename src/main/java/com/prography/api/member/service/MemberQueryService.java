package com.prography.api.member.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prography.api.cohort.service.CohortManager;
import com.prography.api.global.common.PageResponse;
import com.prography.api.global.error.BusinessException;
import com.prography.api.member.domain.CohortMember;
import com.prography.api.member.domain.Member;
import com.prography.api.member.dto.MemberRequestDTO;
import com.prography.api.member.dto.MemberResponseDTO;
import com.prography.api.member.exception.MemberErrorCode;
import com.prography.api.member.repository.CohortMemberRepository;
import com.prography.api.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberQueryService {

	private final MemberRepository memberRepository;
	private final CohortMemberRepository cohortMemberRepository;
	private final CohortManager cohortManager;

	public MemberResponseDTO.MemberProfile getMemberById(Long id) {

		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

		return MemberResponseDTO.MemberProfile.from(member);
	}

	public PageResponse<MemberResponseDTO.GetMemberDashboardResult> getMemberDashboard(
		MemberRequestDTO.GetMemberDashboard request) {

		List<CohortMember> candidates = cohortMemberRepository.findAllForDashboard(request);

		List<CohortMember> filteredList = candidates.stream()
			.filter(cm -> isMatchGeneration(cm, request.generation()))
			.filter(cm -> isMatchPartName(cm, request.partName()))
			.filter(cm -> isMatchTeamName(cm, request.teamName()))
			.toList();

		Pageable pageable = request.toPageable();
		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), filteredList.size());

		List<CohortMember> pagedList = new ArrayList<>();
		if (start <= end) {
			pagedList = filteredList.subList(start, end);
		}

		Page<CohortMember> finalPage = new PageImpl<>(pagedList, pageable, filteredList.size());

		Page<MemberResponseDTO.GetMemberDashboardResult> dtoPage = finalPage.map(cm ->
			MemberResponseDTO.GetMemberDashboardResult.of(
				cm.getMember(), cm.getCohort(), cm.getTeam(), cm.getPart(), cm
			));

		return PageResponse.from(dtoPage);
	}

	private boolean isMatchGeneration(CohortMember cm, Integer generation) {
		if (generation == null)
			return true;
		return cm.getCohort().getGeneration().equals(generation);
	}

	private boolean isMatchPartName(CohortMember cm, String partName) {
		if (partName == null || partName.isBlank())
			return true;
		return cm.getPart() != null && cm.getPart().getName().equals(partName);
	}

	private boolean isMatchTeamName(CohortMember cm, String teamName) {
		if (teamName == null || teamName.isBlank())
			return true;
		return cm.getTeam() != null && cm.getTeam().getName().equals(teamName);
	}

	public MemberResponseDTO.CreateMemberResult getCohortMemberById(Long id) {

		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

		CohortMember cohortMember = cohortMemberRepository.findByMember(member);

		return MemberResponseDTO.CreateMemberResult.of(member, cohortMember.getCohort(), cohortMember.getTeam(),
			cohortMember.getPart());
	}
}
