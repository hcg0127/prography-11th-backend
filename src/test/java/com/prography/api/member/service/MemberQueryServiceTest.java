package com.prography.api.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prography.api.global.error.BusinessException;
import com.prography.api.member.domain.Member;
import com.prography.api.member.domain.MemberRole;
import com.prography.api.member.domain.MemberStatus;
import com.prography.api.member.dto.MemberResponseDTO;
import com.prography.api.member.exception.MemberErrorCode;
import com.prography.api.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberQueryServiceTest {

	@Mock
	private MemberRepository memberRepository;
	@InjectMocks
	private MemberQueryService memberQueryService;

	@Nested
	@DisplayName("회원 단건 조회 테스트")
	class GetMemberByIdTest {

		@Test
		@DisplayName("회원 단건 조회 성공: 존재하는 ID")
		void getMemberById_success() {

			// given
			Long memberId = 1L;
			Member member = Member.builder()
				.id(memberId)
				.loginId("user1")
				.password("password")
				.name("홍길동")
				.phone("010-1234-5678")
				.status(MemberStatus.ACTIVE)
				.role(MemberRole.MEMBER)
				.build();

			given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

			// when
			MemberResponseDTO.MemberProfile result = memberQueryService.getMemberById(memberId);

			// then
			assertThat(result).isNotNull();
			assertThat(result.id()).isEqualTo(memberId);
			assertThat(result.loginId()).isEqualTo("user1");
			assertThat(result.name()).isEqualTo("홍길동");
		}

		@Test
		@DisplayName("회원 단건 조회 실패: 존재하지 않는 ID")
		void getMemberById_fail_not_found() {

			// given
			Long memberId = 1L;

			given(memberRepository.findById(memberId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> memberQueryService.getMemberById(memberId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
		}
	}
}