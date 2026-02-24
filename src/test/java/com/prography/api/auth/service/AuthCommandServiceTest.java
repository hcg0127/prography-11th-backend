package com.prography.api.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.prography.api.auth.dto.AuthRequestDTO;
import com.prography.api.auth.dto.AuthResponseDTO;
import com.prography.api.global.error.BusinessException;
import com.prography.api.member.domain.Member;
import com.prography.api.member.domain.MemberStatus;
import com.prography.api.member.exception.AuthErrorCode;
import com.prography.api.member.exception.MemberErrorCode;
import com.prography.api.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class AuthCommandServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthCommandService authCommandService;

	@org.junit.jupiter.api.Nested
	@DisplayName("로그인 테스트")
	class LoginTest {

		@Test
		@DisplayName("로그인 성공: 아이디와 비밀번호 일치 / 탈퇴하지 않은 회원")
		void login_success() {

			// given
			String loginId = "testMember";
			String password = "rawPassword";
			String encodedPassword = "encodedPassword";

			Member member = Member.builder()
				.loginId(loginId)
				.password(encodedPassword)
				.status(MemberStatus.ACTIVE)
				.build();

			AuthRequestDTO.AuthLogin request = new AuthRequestDTO.AuthLogin(loginId, password);

			given(memberRepository.findMemberByLoginId(loginId)).willReturn(Optional.of(member));
			given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);

			// when
			AuthResponseDTO.AuthLoginResult response = authCommandService.login(request);

			// then
			assertThat(response).isNotNull();
			assertThat(response.loginId()).isEqualTo(loginId);
		}

		@Test
		@DisplayName("로그인 실패: 존재하지 않는 아이디")
		void login_fail_id_not_found() {

			// given
			String loginId = "testMember";
			AuthRequestDTO.AuthLogin request = new AuthRequestDTO.AuthLogin(loginId, "password");

			// when
			given(memberRepository.findMemberByLoginId(loginId)).willReturn(Optional.empty());

			// then
			assertThatThrownBy(() -> authCommandService.login(request))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(AuthErrorCode.LOGIN_FAILED);
		}

		@Test
		@DisplayName("로그인 실패: 비밀번호 불일치")
		void login_fail_password_mismatch() {

			// given
			String loginId = "testMember";
			String wrongPassword = "wrongPassword";

			Member member = Member.builder()
				.loginId(loginId)
				.password(wrongPassword)
				.status(MemberStatus.ACTIVE)
				.build();

			AuthRequestDTO.AuthLogin request = new AuthRequestDTO.AuthLogin(loginId, wrongPassword);

			given(memberRepository.findMemberByLoginId(loginId)).willReturn(Optional.of(member));
			given(passwordEncoder.matches(wrongPassword, member.getPassword())).willReturn(false);

			// when & then
			assertThatThrownBy(() -> authCommandService.login(request))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(AuthErrorCode.LOGIN_FAILED);
		}

		@Test
		@DisplayName("로그인 실패: 탈퇴한 회원")
		void login_fail_withdrawn_member() {

			// given
			String loginId = "withdrawnMember";
			String password = "password";

			Member member = Member.builder()
				.loginId(loginId)
				.password("encodedPassword")
				.status(MemberStatus.WITHDRAWN)
				.build();

			AuthRequestDTO.AuthLogin request = new AuthRequestDTO.AuthLogin(loginId, password);

			given(memberRepository.findMemberByLoginId(loginId)).willReturn(Optional.of(member));
			given(passwordEncoder.matches(password, member.getPassword())).willReturn(true);

			// when & then
			assertThatThrownBy(() -> authCommandService.login(request))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(MemberErrorCode.MEMBER_WITHDRAWN);
		}
	}
}