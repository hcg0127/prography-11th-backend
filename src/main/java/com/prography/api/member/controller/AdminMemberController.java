package com.prography.api.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prography.api.global.common.CommonResponse;
import com.prography.api.member.dto.MemberRequestDTO;
import com.prography.api.member.dto.MemberResponseDTO;
import com.prography.api.member.service.MemberCommandService;

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

	@PostMapping()
	@Operation(
		summary = "회원 등록",
		description = "신규 회원을 등록하고, 기수에 배정하며, 보증금을 초기화합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 등록 성공"),
		@ApiResponse(responseCode = "409", description = "이미 사용 중인 loginId",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "DUPLICATE_LOGIN_ID",
					summary = "회원 없음",
					value = """
						"errorCode": "DUPLICATE_LOGIN_ID",
						"message": "이미 사용 중인 로그인 아이디입니다."
						"""
				))),
		@ApiResponse(responseCode = "404", description = "cohortId에 해당하는 기수 없음",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "COHORT_NOT_FOUND",
					summary = "기수 없음",
					value = """
						"errorCode": "COHORT_NOT_FOUND",
						"message": "기수를 찾을 수 없습니다."
						"""
				))),
		@ApiResponse(responseCode = "404", description = "partId에 해당하는 파트 없음",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "TEAM_NOT_FOUND",
					summary = "팀 없음",
					value = """
						"errorCode": "TEAM_NOT_FOUND",
						"message": "팀을 찾을 수 없습니다."
						"""
				))),
		@ApiResponse(responseCode = "404", description = "teamId에 해당하는 팀 없음",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "PART_NOT_FOUND",
					summary = "파트 없음",
					value = """
						"errorCode": "PART_NOT_FOUND",
						"message": "파트를 찾을 수 없습니다."
						"""
				)))
	})
	public ResponseEntity<CommonResponse<MemberResponseDTO.CreateMemberResult>> createMember(
		@Valid @RequestBody MemberRequestDTO.CreateMember request) {
		MemberResponseDTO.CreateMemberResult result = memberCommandService.createMember(request);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.CREATED);
	}
}
