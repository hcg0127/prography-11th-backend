package com.prography.api.session.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prography.api.global.common.CommonResponse;
import com.prography.api.session.dto.SessionRequestDTO;
import com.prography.api.session.dto.SessionResponseDTO;
import com.prography.api.session.service.SessionCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/sessions")
public class AdminSessionController {

	private final SessionCommandService sessionCommandService;

	@PostMapping()
	@Operation(
		summary = "일정 생성",
		description = "새 일정을 생성합니다. QR 코드가 자동으로 함께 생성됩니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "생성")
	})
	public ResponseEntity<CommonResponse<SessionResponseDTO.CreateSessionResult>> createSession(
		@Valid @RequestBody SessionRequestDTO.CreateSession request) {
		SessionResponseDTO.CreateSessionResult result = sessionCommandService.createSession(request);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.CREATED);
	}

}
