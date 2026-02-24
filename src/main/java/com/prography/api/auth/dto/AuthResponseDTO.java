package com.prography.api.auth.dto;

import java.time.Instant;

import com.prography.api.member.domain.Member;
import com.prography.api.member.domain.MemberRole;
import com.prography.api.member.domain.MemberStatus;

import lombok.Builder;

public class AuthResponseDTO {

	@Builder
	public record AuthLoginResult(
		Long id,
		String loginId,
		String name,
		String phone,
		MemberStatus status,
		MemberRole role,
		Instant createdAt,
		Instant updatedAt
	) {
		public static AuthLoginResult from(Member member) {
			return new AuthLoginResult(
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
