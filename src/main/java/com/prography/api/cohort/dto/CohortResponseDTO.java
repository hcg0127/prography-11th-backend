package com.prography.api.cohort.dto;

import java.time.Instant;

import com.prography.api.cohort.domain.Cohort;

import lombok.Builder;

public class CohortResponseDTO {

	@Builder
	public record GetCohortResult(
		Long id,
		Integer generation,
		String name,
		Instant createdAt
	) {
		public static GetCohortResult from(Cohort cohort) {
			return GetCohortResult.builder()
				.id(cohort.getId())
				.generation(cohort.getGeneration())
				.name(cohort.getName())
				.createdAt(cohort.getCreatedAt())
				.build();
		}
	}

}
