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
@RequestMapping("/auth")
public class AuthController {

	private final MemberCommandService memberCommandService;

	@PostMapping("/login")
	@Operation(
		summary = "로그인",
		description = "loginId와 password로 회원 인증을 수행합니다. 토큰을 발급하지 않으며, 비밀번호 검증 결과만 반환합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그인 성공"),
		@ApiResponse(responseCode = "401", description = "로그인 실패",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "LOGIN_FAILED",
					summary = "ID 없음/비번 불일치",
					value = """
						"errorCode": "LOGIN_FAILED",
						"message": "loginId가 존재하지 않거나 비밀번호가 일치하지 않습니다."
						"""
				))),
		@ApiResponse(responseCode = "403", description = "접근 거부",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "MEMBER_WITHDRAWN",
					summary = "탈퇴한 회원",
					value = """
						"errorCode": "MEMBER_WITHDRAWN",
						"message": "이미 탈퇴한 회원입니다."
						"""
				)))
	})
	public ResponseEntity<CommonResponse<MemberResponseDTO.AuthLogin>> login(
		@Valid @RequestBody MemberRequestDTO.authLogin request) {
		MemberResponseDTO.AuthLogin result = memberCommandService.login(request);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.OK);
	}
}
