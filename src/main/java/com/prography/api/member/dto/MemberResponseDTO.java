package com.prography.api.member.dto;

import java.time.Instant;

import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.domain.Part;
import com.prography.api.cohort.domain.Team;
import com.prography.api.member.domain.CohortMember;
import com.prography.api.member.domain.Member;
import com.prography.api.member.domain.MemberRole;
import com.prography.api.member.domain.MemberStatus;

import lombok.Builder;

public class MemberResponseDTO {

	@Builder
	public record MemberProfile(
		Long id,
		String loginId,
		String name,
		String phone,
		MemberStatus status,
		MemberRole role,
		Instant createdAt,
		Instant updatedAt
	) {
		public static MemberProfile from(Member member) {
			return new MemberProfile(
				member.getId(),
				member.getLoginId(),
				member.getName(),
				member.getPhone(),
				member.getStatus(),
				member.getRole(),
				member.getCreatedAt(),
				member.getUpdatedAt()
			);
		}
	}

	@Builder
	public record CreateMemberResult(
		Long id,
		String loginId,
		String name,
		String phone,
		MemberStatus status,
		MemberRole role,
		Integer generation,
		String partName,
		String teamName,
		Instant createdAt,
		Instant updatedAt
	) {
		public static CreateMemberResult of(Member member, Cohort cohort, Team team, Part part) {
			return new CreateMemberResult(
				member.getId(),
				member.getLoginId(),
				member.getName(),
				member.getPhone(),
				member.getStatus(),
				member.getRole(),
				cohort != null ? cohort.getGeneration() : null,
				part != null ? part.getName() : null,
				team != null ? team.getName() : null,
				member.getCreatedAt(),
				member.getUpdatedAt()
			);
		}
	}

	@Builder
	public record GetMemberDashboardResult(
		Long id,
		String loginId,
		String name,
		String phone,
		MemberStatus status,
		MemberRole role,
		Integer generation,
		String partName,
		String teamName,
		Integer deposit,
		Instant createdAt,
		Instant updatedAt
	) {
		public static GetMemberDashboardResult of(Member member, Cohort cohort, Team team, Part part,
			CohortMember cohortMember) {
			return new GetMemberDashboardResult(
				member.getId(),
				member.getLoginId(),
				member.getName(),
				member.getPhone(),
				member.getStatus(),
				member.getRole(),
				cohort.getGeneration(),
				part != null ? part.getName() : null,
				team != null ? team.getName() : null,
				cohortMember.getDeposit(),
				member.getCreatedAt(),
				member.getUpdatedAt()
			);
		}
	}
}
