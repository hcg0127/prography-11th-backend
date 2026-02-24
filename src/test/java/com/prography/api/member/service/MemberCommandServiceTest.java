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
import com.prography.api.member.domain.MemberStatus;
import com.prography.api.member.dto.MemberRequestDTO;
import com.prography.api.member.dto.MemberResponseDTO;
import com.prography.api.member.exception.AuthErrorCode;
import com.prography.api.member.exception.MemberErrorCode;
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

	@Nested
	@DisplayName("회원 수정 테스트")
	class UpdateMemberTest {

		@Test
		@DisplayName("Case 1: 기수 ID가 없을 때 - 기본 정보만 수정하고 최신 기수 정보를 조회한다.")
		void update_basic_info_only() {

			// given
			Long memberId = 1L;

			MemberRequestDTO.UpdateMember request = new MemberRequestDTO.UpdateMember(
				"NewName", "010-9999-9999", null, null, null);

			Member member = Member.builder()
				.id(memberId).name("OldName").phone("010-0000-0000").build();

			CohortMember latestLog = CohortMember.builder()
				.member(member).cohort(Cohort.builder().generation(10).build()).build();

			given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
			given(cohortMemberRepository.findTopByMemberOrderByIdDesc(member))
				.willReturn(Optional.of(latestLog));

			// when
			MemberResponseDTO.CreateMemberResult result = memberCommandService.updateMemberProfile(request, memberId);

			// then
			assertThat(member.getName()).isEqualTo("NewName");
			assertThat(member.getPhone()).isEqualTo("010-9999-9999");

			assertThat(result.generation()).isEqualTo(10);

			verify(cohortMemberRepository, times(0)).save(any());
		}

		@Test
		@DisplayName("Case 2: 기수 ID가 있고 기존 이력이 존재할 때 - 해당 기수 이력의 파트/팀을 수정(Update)한다.")
		void update_existing_cohort_member() {

			// given
			Long memberId = 1L;
			Long cohortId = 11L;
			Long newPartId = 2L;

			MemberRequestDTO.UpdateMember request = new MemberRequestDTO.UpdateMember(
				"NewName", "010-9999-9999", cohortId, newPartId, null);

			Member member = Member.builder().id(memberId).build();
			Cohort cohort = Cohort.builder().id(cohortId).generation(11).build();
			Part newPart = Part.builder().id(newPartId).name("SERVER").build();

			CohortMember existingCohortMember = CohortMember.builder()
				.member(member).cohort(cohort).part(null).build();

			given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
			given(cohortRepository.findById(cohortId)).willReturn(Optional.of(cohort));
			given(partRepository.findById(newPartId)).willReturn(Optional.of(newPart));

			given(cohortMemberRepository.findByMemberAndCohort(member, cohort))
				.willReturn(Optional.of(existingCohortMember));

			// when
			MemberResponseDTO.CreateMemberResult result = memberCommandService.updateMemberProfile(request, memberId);

			// then
			assertThat(existingCohortMember.getPart().getName()).isEqualTo("SERVER");

			assertThat(result.partName()).isEqualTo("SERVER");

			verify(cohortMemberRepository, times(0)).save(any());
		}

		@Test
		@DisplayName("Case 3: 기수 ID가 있고 이력이 없을 때 - 새로운 기수 이력을 생성(Insert)한다.")
		void create_new_cohort_member() {

			// given
			Long memberId = 1L;
			Long cohortId = 12L; // 새로운 기수

			MemberRequestDTO.UpdateMember request = new MemberRequestDTO.UpdateMember(
				"Name", "Phone", cohortId, null, null);

			Member member = Member.builder().id(memberId).build();
			Cohort cohort = Cohort.builder().id(cohortId).generation(12).build();

			given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
			given(cohortRepository.findById(cohortId)).willReturn(Optional.of(cohort));

			given(cohortMemberRepository.findByMemberAndCohort(member, cohort))
				.willReturn(Optional.empty());

			CohortMember savedMember = CohortMember.builder().member(member).cohort(cohort).build();
			given(cohortMemberRepository.save(any(CohortMember.class))).willReturn(savedMember);

			// when
			MemberResponseDTO.CreateMemberResult result = memberCommandService.updateMemberProfile(request, memberId);

			// then
			verify(cohortMemberRepository, times(1)).save(any(CohortMember.class));

			assertThat(result.generation()).isEqualTo(12);
		}

		@Test
		@DisplayName("실패: 존재하지 않는 회원 ID면 예외가 발생한다.")
		void fail_member_not_found() {

			// given
			Long unknownId = 999L;
			MemberRequestDTO.UpdateMember request = new MemberRequestDTO.UpdateMember(
				"Name", "Phone", null, null, null
			);

			given(memberRepository.findById(unknownId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> memberCommandService.updateMemberProfile(request, unknownId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("회원 탈퇴 테스트")
	class DeleteMemberTest {

		@Test
		@DisplayName("성공: 정상 상태인 회원은 탈퇴(WITHDRAWN) 상태로 변경된다.")
		void success() {

			// given
			Long memberId = 1L;

			Member member = Member.builder()
				.id(memberId)
				.status(MemberStatus.ACTIVE)
				.build();

			given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

			// when
			MemberResponseDTO.DeleteMemberResult result = memberCommandService.deleteMember(memberId);

			// then
			assertThat(member.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);

			// 2. 반환된 결과 확인
			assertThat(result).isNotNull();
		}

		@Test
		@DisplayName("실패: 이미 탈퇴한 회원은 MEMBER_ALREADY_WITHDRAWN 예외가 발생한다.")
		void fail_already_withdrawn() {

			// given
			Long memberId = 1L;

			Member withdrawnMember = Member.builder()
				.id(memberId)
				.status(MemberStatus.WITHDRAWN)
				.build();

			given(memberRepository.findById(memberId)).willReturn(Optional.of(withdrawnMember));

			// when & then
			assertThatThrownBy(() -> memberCommandService.deleteMember(memberId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(MemberErrorCode.MEMBER_ALREADY_WITHDRAWN);
		}

		@Test
		@DisplayName("실패: 존재하지 않는 회원은 MEMBER_NOT_FOUND 예외가 발생한다.")
		void fail_member_not_found() {

			// given
			Long unknownId = 999L;

			given(memberRepository.findById(unknownId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> memberCommandService.deleteMember(unknownId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
		}
	}
}