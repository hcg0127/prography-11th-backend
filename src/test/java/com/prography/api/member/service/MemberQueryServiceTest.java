package com.prography.api.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.domain.Part;
import com.prography.api.cohort.domain.Team;
import com.prography.api.global.common.PageResponse;
import com.prography.api.global.error.BusinessException;
import com.prography.api.member.domain.CohortMember;
import com.prography.api.member.domain.Member;
import com.prography.api.member.domain.MemberRole;
import com.prography.api.member.domain.MemberStatus;
import com.prography.api.member.dto.MemberRequestDTO;
import com.prography.api.member.dto.MemberResponseDTO;
import com.prography.api.member.exception.MemberErrorCode;
import com.prography.api.member.repository.CohortMemberRepository;
import com.prography.api.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberQueryServiceTest {

	@Mock
	private MemberRepository memberRepository;
	@InjectMocks
	private MemberQueryService memberQueryService;
	@Mock
	private CohortMemberRepository cohortMemberRepository;

	private CohortMember createCohortMember(Long id, String loginId, int gen, String partName, String teamName) {
		Member member = Member.builder()
			.id(id)
			.loginId(loginId)
			.name("Name" + id)
			.phone("010-0000-0000")
			.status(MemberStatus.ACTIVE)
			.role(MemberRole.MEMBER)
			.build();

		Cohort cohort = Cohort.builder().generation(gen).build();
		Part part = Part.builder().name(partName).build();
		Team team = Team.builder().name(teamName).build();

		return CohortMember.builder()
			.member(member)
			.cohort(cohort)
			.part(part)
			.team(team)
			.deposit(100000)
			.build();
	}

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

	@Nested
	@DisplayName("회원 대시보드 조회 (메모리 필터링 & 페이징)")
	class GetMemberDashboardTest {

		@Test
		@DisplayName("성공: DB에서 조회된 데이터 중 기수, 파트, 팀 조건에 맞는 데이터만 메모리 필터링되어 반환된다.")
		void success_memory_filtering() {

			// given
			CohortMember memberA = createCohortMember(1L, "UserA", 11, "SERVER", "Team A"); // 대상
			CohortMember memberB = createCohortMember(2L, "UserB", 11, "WEB", "Team B");    // 파트 불일치
			CohortMember memberC = createCohortMember(3L, "UserC", 12, "SERVER", "Team A"); // 기수 불일치

			List<CohortMember> dbResult = List.of(memberA, memberB, memberC);

			MemberRequestDTO.GetMemberDashboard request = new MemberRequestDTO.GetMemberDashboard(
				0, 10, null, null,
				11, "SERVER", null, null);

			given(cohortMemberRepository.findAllForDashboard(any())).willReturn(dbResult);

			// when
			PageResponse<MemberResponseDTO.GetMemberDashboardResult> response =
				memberQueryService.getMemberDashboard(request);

			// then
			assertThat(response.totalElements()).isEqualTo(1);
			assertThat(response.content()).hasSize(1);
			assertThat(response.content().get(0).loginId()).isEqualTo("UserA");
		}

		@Test
		@DisplayName("성공: 필터링 후 데이터가 페이지 크기보다 많으면 올바르게 페이징(subList) 처리된다.")
		void success_manual_paging() {

			// given
			List<CohortMember> dbResult = new ArrayList<>();
			for (int i = 1; i <= 15; i++) {
				dbResult.add(createCohortMember((long)i, "User" + i, 11, "SERVER", "Team A"));
			}

			MemberRequestDTO.GetMemberDashboard request = new MemberRequestDTO.GetMemberDashboard(
				1, 10, null, null,
				11, "SERVER", "Team A", null);

			given(cohortMemberRepository.findAllForDashboard(any())).willReturn(dbResult);

			// when
			PageResponse<MemberResponseDTO.GetMemberDashboardResult> response =
				memberQueryService.getMemberDashboard(request);

			// then
			assertThat(response.totalElements()).isEqualTo(15);
			assertThat(response.totalPages()).isEqualTo(2);
			assertThat(response.content()).hasSize(5);
			assertThat(response.content().get(0).loginId()).isEqualTo("User11");
		}

		@Test
		@DisplayName("성공: 검색 조건이 null이면 모든 데이터를 반환한다.")
		void success_no_filter() {

			// given
			CohortMember memberA = createCohortMember(1L, "UserA", 11, "SERVER", "Team A");
			List<CohortMember> dbResult = List.of(memberA);

			MemberRequestDTO.GetMemberDashboard request = new MemberRequestDTO.GetMemberDashboard(
				0, 10, null, null, null, null, null, null);

			given(cohortMemberRepository.findAllForDashboard(any())).willReturn(dbResult);

			// when
			PageResponse<MemberResponseDTO.GetMemberDashboardResult> response =
				memberQueryService.getMemberDashboard(request);

			// then
			assertThat(response.totalElements()).isEqualTo(1);
			assertThat(response.content().get(0).loginId()).isEqualTo("UserA");
		}
	}

	@Nested
	@DisplayName("회원 상세 조회 테스트")
	class GetCohortMemberByIdTest {

		@Test
		@DisplayName("성공: 회원을 찾고, 연관된 기수/파트/팀 정보를 조회하여 반환한다.")
		void success() {

			// given
			Long memberId = 1L;

			Member member = Member.builder()
				.id(memberId)
				.loginId("testUser")
				.name("홍길동")
				.build();

			Cohort cohort = Cohort.builder().generation(11).build();
			Part part = Part.builder().name("SERVER").build();
			Team team = Team.builder().name("Team A").build();

			CohortMember cohortMember = CohortMember.builder()
				.member(member)
				.cohort(cohort)
				.part(part)
				.team(team)
				.build();

			given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
			given(cohortMemberRepository.findByMember(member)).willReturn(cohortMember);

			// when
			MemberResponseDTO.CreateMemberResult result = memberQueryService.getCohortMemberById(memberId);

			// then
			assertThat(result).isNotNull();
			assertThat(result.loginId()).isEqualTo("testUser");
			assertThat(result.generation()).isEqualTo(11);
			assertThat(result.partName()).isEqualTo("SERVER");
			assertThat(result.teamName()).isEqualTo("Team A");

			verify(memberRepository).findById(memberId);
			verify(cohortMemberRepository).findByMember(member);
		}

		@Test
		@DisplayName("실패: 존재하지 않는 회원 ID로 조회 시 MEMBER_NOT_FOUND 예외가 발생한다.")
		void fail_member_not_found() {

			// given
			Long unknownId = 999L;

			given(memberRepository.findById(unknownId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> memberQueryService.getCohortMemberById(unknownId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
		}
	}
}