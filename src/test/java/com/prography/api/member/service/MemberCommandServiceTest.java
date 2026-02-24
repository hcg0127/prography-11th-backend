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
import org.springframework.security.crypto.password.PasswordEncoder;

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

@ExtendWith(MockitoExtension.class)
class MemberCommandServiceTest {

	@Mock
	private MemberRepository memberRepository;
	@Mock
	private CohortRepository cohortRepository;
	@Mock
	private PartRepository partRepository;
	@Mock
	private TeamRepository teamRepository;
	@Mock
	private DepositHistoryRepository depositHistoryRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private CohortMemberRepository cohortMemberRepository;
	@InjectMocks
	private MemberCommandService memberCommandService;

	@Nested
	@DisplayName("회원 등록 테스트")
	class CreateMemberTest {

		@Test
		@DisplayName("성공: 모든 정보가 유효하면 회원, 기수회원, 입금내역이 저장된다.")
		void createMember_success() {

			// given
			MemberRequestDTO.CreateMember request = new MemberRequestDTO.CreateMember(
				"newUser", "password123", "홍길동", "010-1234-5678", 1L, 1L, 1L);

			Cohort cohort = Cohort.builder().build();
			Part part = Part.builder().build();
			Team team = Team.builder().build();

			// Mocking
			given(memberRepository.findMemberByLoginId(request.loginId())).willReturn(Optional.empty());
			given(cohortRepository.findById(request.cohortId())).willReturn(Optional.of(cohort));
			given(partRepository.findById(request.partId())).willReturn(Optional.of(part));
			given(teamRepository.findById(request.teamId())).willReturn(Optional.of(team));
			given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");

			// when
			MemberResponseDTO.CreateMemberResult result = memberCommandService.createMember(request);

			// then
			assertThat(result).isNotNull();

			verify(memberRepository, times(1)).save(any(Member.class));
			verify(cohortMemberRepository, times(1)).save(any(CohortMember.class));
			verify(depositHistoryRepository, times(1)).save(any(DepositHistory.class));

			verify(passwordEncoder).encode(request.password());
		}

		@Test
		@DisplayName("실패: 이미 존재하는 아이디면 DUPLICATED_LOGIN_ID 예외가 발생한다.")
		void createMember_fail_duplicate_id() {

			// given
			MemberRequestDTO.CreateMember request = new MemberRequestDTO.CreateMember(
				"existUser", "pw", "name", "phone", 1L, 1L, 1L);

			given(memberRepository.findMemberByLoginId(request.loginId()))
				.willReturn(Optional.of(Member.builder().build()));

			// when & then
			assertThatThrownBy(() -> memberCommandService.createMember(request))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(AuthErrorCode.DUPLICATED_LOGIN_ID);

			verify(memberRepository, times(0)).save(any());
		}

		@Test
		@DisplayName("실패: 기수(Cohort)가 존재하지 않으면 COHORT_NOT_FOUND 예외가 발생한다.")
		void createMember_fail_cohort_not_found() {

			// given
			MemberRequestDTO.CreateMember request = new MemberRequestDTO.CreateMember(
				"user", "pw", "name", "phone", 999L, 1L, 1L
			);

			given(memberRepository.findMemberByLoginId(any())).willReturn(Optional.empty());
			given(cohortRepository.findById(999L)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> memberCommandService.createMember(request))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(CohortErrorCode.COHORT_NOT_FOUND);
		}

		@Test
		@DisplayName("실패: 파트(Part)가 존재하지 않으면 PART_NOT_FOUND 예외가 발생한다.")
		void createMember_fail_part_not_found() {

			// given
			MemberRequestDTO.CreateMember request = new MemberRequestDTO.CreateMember(
				"user", "pw", "name", "phone", 1L, 999L, 1L);

			given(memberRepository.findMemberByLoginId(any())).willReturn(Optional.empty());
			given(cohortRepository.findById(anyLong())).willReturn(Optional.of(Cohort.builder().build()));
			given(partRepository.findById(999L)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> memberCommandService.createMember(request))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(CohortErrorCode.PART_NOT_FOUND);
		}

		@Test
		@DisplayName("실패: 팀(Team)이 존재하지 않으면 TEAM_NOT_FOUND 예외가 발생한다.")
		void createMember_fail_team_not_found() {

			// given
			MemberRequestDTO.CreateMember request = new MemberRequestDTO.CreateMember(
				"user", "pw", "name", "phone", 1L, 1L, 999L
			);

			given(memberRepository.findMemberByLoginId(any())).willReturn(Optional.empty());
			given(cohortRepository.findById(anyLong())).willReturn(Optional.of(Cohort.builder().build()));
			given(partRepository.findById(anyLong())).willReturn(Optional.of(Part.builder().build()));
			given(teamRepository.findById(999L)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> memberCommandService.createMember(request))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(CohortErrorCode.TEAM_NOT_FOUND);
		}
	}

}