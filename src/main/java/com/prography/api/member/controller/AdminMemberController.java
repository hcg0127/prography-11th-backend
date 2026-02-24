package com.prography.api.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prography.api.global.common.CommonResponse;
import com.prography.api.global.common.PageResponse;
import com.prography.api.member.dto.MemberRequestDTO;
import com.prography.api.member.dto.MemberResponseDTO;
import com.prography.api.member.service.MemberCommandService;
import com.prography.api.member.service.MemberQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class AdminMemberController {

	private final MemberCommandService memberCommandService;
	private final MemberQueryService memberQueryService;

	@PostMapping()
	@Operation(
		summary = "회원 등록",
		description = "신규 회원을 등록하고, 기수에 배정하며, 보증금을 초기화합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공"),
		@ApiResponse(responseCode = "409", description = "중복 및 충돌",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "DUPLICATE_LOGIN_ID",
					summary = "이미 사용 중인 loginId",
					value = """
						"code": "DUPLICATE_LOGIN_ID",
						"message": "이미 사용 중인 로그인 아이디입니다."
						"""
				))),
		@ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = {
					@ExampleObject(
						name = "COHORT_NOT_FOUND",
						summary = "cohortId에 해당하는 기수 없음",
						value = """
							"code": "COHORT_NOT_FOUND",
							"message": "기수를 찾을 수 없습니다."
							"""
					),
					@ExampleObject(
						name = "TEAM_NOT_FOUND",
						summary = "partId에 해당하는 파트 없음",
						value = """
							"code": "TEAM_NOT_FOUND",
							"message": "팀을 찾을 수 없습니다."
							"""
					),
					@ExampleObject(
						name = "PART_NOT_FOUND",
						summary = "teamId에 해당하는 팀 없음",
						value = """
							"code": "PART_NOT_FOUND",
							"message": "파트를 찾을 수 없습니다."
							"""
					)
				}))
	})
	public ResponseEntity<CommonResponse<MemberResponseDTO.CreateMemberResult>> createMember(
		@Valid @RequestBody MemberRequestDTO.CreateMember request) {
		MemberResponseDTO.CreateMemberResult result = memberCommandService.createMember(request);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.CREATED);
	}

	@GetMapping()
	@Operation(
		summary = "회원 대시보드 조회",
		description = "회원 목록을 페이징, 필터링, 검색 조건으로 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공")
	})
	public ResponseEntity<CommonResponse<PageResponse<MemberResponseDTO.GetMemberDashboardResult>>> getMemberDashboard(
		@ModelAttribute @Valid MemberRequestDTO.GetMemberDashboard request) {
		PageResponse<MemberResponseDTO.GetMemberDashboardResult> result = memberQueryService.getMemberDashboard(
			request);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@Operation(
		summary = "회원 상세 조회",
		description = "회원의 상세 정보를 기수/파트/팀 정보와 함께 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공"),
		@ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "MEMBER_NOT_FOUND",
					summary = "회원 없음",
					value = """
						"code": "MEMBER_NOT_FOUND",
						"message": "회원을 찾을 수 없습니다."
						"""
				)))
	})
	public ResponseEntity<CommonResponse<MemberResponseDTO.CreateMemberResult>> getCohortMemberById(
		@Schema(description = "회원 ID") @PathVariable(name = "id") Long id) {
		MemberResponseDTO.CreateMemberResult result = memberQueryService.getCohortMemberById(id);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.OK);
	}

	@PutMapping("{id}")
	@Operation(
		summary = "회원 수정",
		description = "회원 정보를 수정합니다. 모든 필드는 optional이며, 전달된 필드만 수정됩니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공"),
		@ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = {
					@ExampleObject(
						name = "COHORT_NOT_FOUND",
						summary = "cohortId에 해당하는 기수 없음",
						value = """
							"code": "COHORT_NOT_FOUND",
							"message": "기수를 찾을 수 없습니다."
							"""
					),
					@ExampleObject(
						name = "TEAM_NOT_FOUND",
						summary = "partId에 해당하는 파트 없음",
						value = """
							"code": "TEAM_NOT_FOUND",
							"message": "팀을 찾을 수 없습니다."
							"""
					),
					@ExampleObject(
						name = "PART_NOT_FOUND",
						summary = "teamId에 해당하는 팀 없음",
						value = """
							"code": "PART_NOT_FOUND",
							"message": "파트를 찾을 수 없습니다."
							"""
					)
				}))
	})
	public ResponseEntity<CommonResponse<MemberResponseDTO.CreateMemberResult>> updateMemberProfile(
		@Valid @RequestBody MemberRequestDTO.UpdateMember request,
		@Schema(description = "회원 ID") @PathVariable(name = "id") Long id) {
		MemberResponseDTO.CreateMemberResult result = memberCommandService.updateMemberProfile(request, id);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.OK);
	}
}
