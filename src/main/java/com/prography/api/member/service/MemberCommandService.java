package com.prography.api.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prography.api.global.error.BusinessException;
import com.prography.api.member.domain.Member;
import com.prography.api.member.domain.MemberStatus;
import com.prography.api.member.dto.MemberRequestDTO;
import com.prography.api.member.dto.MemberResponseDTO;
import com.prography.api.member.exception.AuthErrorCode;
import com.prography.api.member.exception.MemberErrorCode;
import com.prography.api.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MemberCommandService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberResponseDTO.AuthLogin login(MemberRequestDTO.authLogin request) {

		Member member = memberRepository.findMemberByLoginId(request.loginId())
			.orElseThrow(() -> new BusinessException(AuthErrorCode.LOGIN_FAILED));

		if (!passwordEncoder.matches(request.password(), member.getPassword())) {
			throw new BusinessException(AuthErrorCode.LOGIN_FAILED);
		}

		if (member.getStatus() == MemberStatus.WITHDRAWN) {
			throw new BusinessException(MemberErrorCode.MEMBER_WITHDRAWN);
		}

		return MemberResponseDTO.AuthLogin.from(member);
	}
}
