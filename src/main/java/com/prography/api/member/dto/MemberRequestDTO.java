package com.prography.api.member.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.prography.api.member.domain.MemberStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class MemberRequestDTO {

	public record CreateMember(
		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "로그인 아이디", example = "user1")
		String loginId,

		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "비밀번호", example = "password123")
		String password,

		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "이름", example = "홍길동")
		String name,

		@NotEmpty(message = "필수 입력값입니다.")
		@Schema(description = "전화번호", example = "010-1234-5678")
		String phone,

		@NotNull(message = "필수 입력값입니다.")
		@Schema(description = "기수 ID", example = "2")
		Long cohortId,

		@Schema(description = "파트 ID", example = "6")
		Long partId,

		@Schema(description = "팀 ID", example = "1")
		Long teamId
	) {
	}

	public record GetMemberDashboard(
		@Schema(description = "페이지 번호 (0-based)", example = "0")
		Integer page,

		@Schema(description = "페이지 크기", example = "10")
		Integer size,

		@Schema(description = "검색 유형: name, loginId, phone", example = "name")
		String searchType,

		@Schema(description = "검색어", example = "관리자")
		String searchValue,

		@Schema(description = "기수 필터", example = "11")
		Integer generation,

		@Schema(description = "파트명 필터", example = "SERVER")
		String partName,

		@Schema(description = "팀명 필터", example = "Team A")
		String teamName,

		@Schema(description = "상태 필터 (ACTIVE, INACTIVE, WITHDRAWN)", example = "ACTIVE")
		MemberStatus status
	) {
		public Integer initPage() {
			if (page == null || page < 0) {
				return 0;
			}
			return page;
		}

		public Integer initSize() {
			if (size == null || size < 0) {
				return 10;
			}
			return Math.min(size, 100);
		}

		public Pageable toPageable() {
			return PageRequest.of(initPage(), initSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
		}
	}
}
