package com.prography.api.session.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prography.api.global.common.CommonResponse;
import com.prography.api.session.dto.SessionResponseDTO;
import com.prography.api.session.service.SessionQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sessions")
public class SessionController {

	private final SessionQueryService sessionQueryService;

	@GetMapping()
	@Operation(
		summary = "일정 목록 조회 (회원용)",
		description = "현재 기수(11기)의 일정 목록을 조회합니다. CANCELLED 상태의 일정은 제외됩니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공")
	})
	public ResponseEntity<CommonResponse<List<SessionResponseDTO.SessionProfile>>> getSessionList() {
		List<SessionResponseDTO.SessionProfile> result = sessionQueryService.getSessionList();
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.OK);
	}
}
