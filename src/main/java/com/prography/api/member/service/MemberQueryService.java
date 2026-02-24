package com.prography.api.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prography.api.global.error.BusinessException;
import com.prography.api.member.domain.Member;
import com.prography.api.member.dto.MemberResponseDTO;
import com.prography.api.member.exception.MemberErrorCode;
import com.prography.api.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberQueryService {

	private final MemberRepository memberRepository;

	public MemberResponseDTO.MemberProfile getMemberById(Long id) {

		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

		return MemberResponseDTO.MemberProfile.from(member);
	}
}
