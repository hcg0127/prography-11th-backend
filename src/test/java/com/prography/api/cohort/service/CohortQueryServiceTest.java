package com.prography.api.cohort.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
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
import com.prography.api.cohort.dto.CohortResponseDTO;
import com.prography.api.cohort.exception.CohortErrorCode;
import com.prography.api.cohort.repository.CohortRepository;
import com.prography.api.global.error.BusinessException;

@ExtendWith(MockitoExtension.class)
class CohortQueryServiceTest {

	@Mock
	private CohortRepository cohortRepository;
	@InjectMocks
	private CohortQueryService cohortQueryService;

	@Nested
	@DisplayName("기수 목록 조회 테스트")
	class GetCohortListTest {

		@Test
		@DisplayName("성공: 저장된 모든 기수 목록을 조회하여 DTO 리스트로 반환한다.")
		void success() {

			// given
			Cohort cohort10 = Cohort.builder()
				.id(1L)
				.generation(10)
				.name("10기")
				.build();

			Cohort cohort11 = Cohort.builder()
				.id(2L)
				.generation(11)
				.name("11기")
				.build();

			List<Cohort> mockCohorts = List.of(cohort10, cohort11);

			given(cohortRepository.findAll()).willReturn(mockCohorts);

			// when
			List<CohortResponseDTO.GetCohortResult> result = cohortQueryService.getCohortList();

			// then
			assertThat(result).hasSize(2);
			assertThat(result.get(0).generation()).isEqualTo(10);
			assertThat(result.get(0).name()).isEqualTo("10기");
			assertThat(result.get(1).generation()).isEqualTo(11);
			assertThat(result.get(1).name()).isEqualTo("11기");

			verify(cohortRepository, times(1)).findAll();
		}

		@Test
		@DisplayName("성공: 데이터가 없으면 빈 리스트를 반환한다.")
		void success_empty() {

			// given
			given(cohortRepository.findAll()).willReturn(Collections.emptyList());

			// when
			List<CohortResponseDTO.GetCohortResult> result = cohortQueryService.getCohortList();

			// then
			assertThat(result).isEmpty();
			assertThat(result).isNotNull();

			verify(cohortRepository, times(1)).findAll();
		}
	}

	@Nested
	@DisplayName("기수 상세 조회 테스트")
	class GetCohortDetailTest {

		@Test
		@DisplayName("성공: 기수를 조회하면 연관된 파트와 팀 목록이 DTO에 포함되어 반환된다.")
		void success() {

			// given
			Long cohortId = 1L;

			List<Part> parts = List.of(
				Part.builder().id(10L).name("SERVER").build(),
				Part.builder().id(11L).name("WEB").build()
			);

			List<Team> teams = List.of(
				Team.builder().id(20L).name("Team A").build(),
				Team.builder().id(21L).name("Team B").build()
			);

			Cohort cohort = Cohort.builder()
				.id(cohortId)
				.generation(11)
				.name("11기")
				.partList(parts)
				.teamList(teams)
				.build();

			given(cohortRepository.findById(cohortId)).willReturn(Optional.of(cohort));

			// when
			CohortResponseDTO.GetCohortDetailResult result = cohortQueryService.getCohortDetail(cohortId);

			// then
			assertThat(result).isNotNull();
			assertThat(result.generation()).isEqualTo(11);
			assertThat(result.name()).isEqualTo("11기");

			assertThat(result.partDTOList()).hasSize(2);
			assertThat(result.partDTOList().get(0).name()).isEqualTo("SERVER");

			assertThat(result.teamDTOList()).hasSize(2);
			assertThat(result.teamDTOList().get(0).name()).isEqualTo("Team A");

			verify(cohortRepository).findById(cohortId);
		}

		@Test
		@DisplayName("성공: 파트나 팀이 없는 경우 빈 리스트로 반환된다.")
		void success_empty_lists() {

			// given
			Long cohortId = 1L;

			Cohort cohort = Cohort.builder()
				.id(cohortId)
				.generation(12)
				.name("12기")
				.partList(Collections.emptyList())
				.teamList(Collections.emptyList())
				.build();

			given(cohortRepository.findById(cohortId)).willReturn(Optional.of(cohort));

			// when
			CohortResponseDTO.GetCohortDetailResult result = cohortQueryService.getCohortDetail(cohortId);

			// then
			assertThat(result.partDTOList()).isEmpty();
			assertThat(result.teamDTOList()).isEmpty();
		}

		@Test
		@DisplayName("실패: 존재하지 않는 기수 ID로 조회 시 COHORT_NOT_FOUND 예외가 발생한다.")
		void fail_cohort_not_found() {

			// given
			Long unknownId = 999L;
			given(cohortRepository.findById(unknownId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> cohortQueryService.getCohortDetail(unknownId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(CohortErrorCode.COHORT_NOT_FOUND);
		}
	}
}