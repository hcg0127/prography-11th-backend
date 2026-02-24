package com.prography.api.cohort.dto;

import java.time.Instant;
import java.util.List;

import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.domain.Part;
import com.prography.api.cohort.domain.Team;

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

	@Builder
	public record GetCohortDetailResult(
		Long id,
		Integer generation,
		String name,
		Instant createdAt,
		List<PartDTO> partDTOList,
		List<TeamDTO> teamDTOList
	) {
		public static GetCohortDetailResult of(Cohort cohort, List<Part> partList, List<Team> teamList) {
			return GetCohortDetailResult.builder()
				.id(cohort.getId())
				.generation(cohort.getGeneration())
				.name(cohort.getName())
				.createdAt(cohort.getCreatedAt())
				.partDTOList(partList.stream().map(PartDTO::from).toList())
				.teamDTOList(teamList.stream().map(TeamDTO::from).toList())
				.build();

		}
	}

	@Builder
	public record PartDTO(Long id, String name) {
		public static PartDTO from(Part part) {
			return new PartDTO(part.getId(), part.getName());
		}
	}

	@Builder
	public record TeamDTO(Long id, String name) {
		public static TeamDTO from(Team team) {
			return new TeamDTO(team.getId(), team.getName());
		}
	}

}
