package com.prography.api.cohort.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.dto.CohortResponseDTO;
import com.prography.api.cohort.repository.CohortRepository;

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

			// Repository Mocking
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
}