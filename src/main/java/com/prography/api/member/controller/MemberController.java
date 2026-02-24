package com.prography.api.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prography.api.global.common.CommonResponse;
import com.prography.api.member.dto.MemberResponseDTO;
import com.prography.api.member.service.MemberQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

	private final MemberQueryService memberQueryService;

	@GetMapping("/{id}")
	@Operation(
		summary = "회원 조회",
		description = "회원의 기본 정보를 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 조회 성공"),
		@ApiResponse(responseCode = "404", description = "회원 조회 실패",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "MEMBER_NOT_FOUND",
					summary = "회원 없음",
					value = """
						"errorCode": "MEMBER_NOT_FOUND",
						"message": "회원을 찾을 수 없습니다."
						"""
				)))
	})
	public ResponseEntity<CommonResponse<MemberResponseDTO.MemberProfile>> getMemberById(
		@Schema(description = "회원 ID") @PathVariable(name = "id") Long id) {
		MemberResponseDTO.MemberProfile result = memberQueryService.getMemberById(id);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.OK);
	}
}
