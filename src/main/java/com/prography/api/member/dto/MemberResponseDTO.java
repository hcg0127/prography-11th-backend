package com.prography.api.member.dto;

import java.time.Instant;

import com.prography.api.member.domain.Member;
import com.prography.api.member.domain.MemberRole;
import com.prography.api.member.domain.MemberStatus;

import lombok.Builder;

public class MemberResponseDTO {

	@Builder
	public record AuthLogin(
		Long id,
		String loginId,
		String name,
		String phone,
		MemberStatus status,
		MemberRole role,
		Instant createdAt,
		Instant updatedAt
	) {
		public static AuthLogin from(Member member) {
			return new AuthLogin(
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
}
