package com.prography.api.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prography.api.auth.dto.AuthRequestDTO;
import com.prography.api.auth.dto.AuthResponseDTO;
import com.prography.api.auth.exception.AuthErrorCode;
import com.prography.api.global.error.BusinessException;
import com.prography.api.member.domain.Member;
import com.prography.api.member.domain.MemberStatus;
import com.prography.api.member.exception.MemberErrorCode;
import com.prography.api.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public AuthResponseDTO.AuthLoginResult login(AuthRequestDTO.AuthLogin request) {

		Member member = memberRepository.findMemberByLoginId(request.loginId())
			.orElseThrow(() -> new BusinessException(AuthErrorCode.LOGIN_FAILED));

		if (!passwordEncoder.matches(request.password(), member.getPassword())) {
			throw new BusinessException(AuthErrorCode.LOGIN_FAILED);
		}

		if (member.getStatus() == MemberStatus.WITHDRAWN) {
			throw new BusinessException(MemberErrorCode.MEMBER_WITHDRAWN);
		}

		return AuthResponseDTO.AuthLoginResult.from(member);
	}
}
