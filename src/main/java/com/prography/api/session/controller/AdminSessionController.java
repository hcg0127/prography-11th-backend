package com.prography.api.session.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prography.api.global.common.CommonResponse;
import com.prography.api.session.dto.SessionRequestDTO;
import com.prography.api.session.dto.SessionResponseDTO;
import com.prography.api.session.service.SessionCommandService;

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

	@PutMapping("{id}")
	@Operation(
		summary = "일정 수정",
		description = "일정 정보를 수정합니다. 모든 필드는 optional이며, 전달된 필드만 수정됩니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공"),
		@ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "SESSION_NOT_FOUND",
					summary = "해당 ID의 일정이 존재하지 않음",
					value = """
						"code": "SESSION_NOT_FOUND",
						"message": "일정을 찾을 수 없습니다."
						"""
				))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "SESSION_ALREADY_CANCELLED",
					summary = "이미 취소된 일정은 수정 불가",
					value = """
						"code": "SESSION_ALREADY_CANCELLED",
						"message": "이미 취소된 일정입니다."
						"""
				)))
	})
	public ResponseEntity<CommonResponse<SessionResponseDTO.CreateSessionResult>> updateSession(
		@Schema(description = "일정 ID") @PathVariable(name = "id") Long id,
		@Valid @RequestBody SessionRequestDTO.UpdateSession request) {
		SessionResponseDTO.CreateSessionResult result = sessionCommandService.updateSession(id, request);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.OK);
	}

	@DeleteMapping("{id}")
	@Operation(
		summary = "일정 삭제 (취소)",
		description = "일정을 Soft-delete 처리합니다. 실제 삭제가 아닌 상태를 CANCELLED로 변경합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공"),
		@ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "SESSION_NOT_FOUND",
					summary = "해당 ID의 일정이 존재하지 않음",
					value = """
						"code": "SESSION_NOT_FOUND",
						"message": "일정을 찾을 수 없습니다."
						"""
				))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "SESSION_ALREADY_CANCELLED",
					summary = "이미 취소된 일정은 수정 불가",
					value = """
						"code": "SESSION_ALREADY_CANCELLED",
						"message": "이미 취소된 일정입니다."
						"""
				)))
	})
	public ResponseEntity<CommonResponse<SessionResponseDTO.CreateSessionResult>> deleteSession(
		@Schema(description = "일정 ID") @PathVariable(name = "id") Long id) {
		SessionResponseDTO.CreateSessionResult result = sessionCommandService.deleteSession(id);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.OK);
	}

	@PostMapping("/{sessionId}/qrcodes")
	@Operation(
		summary = "QR 코드 생성",
		description = "해당 일정에 새 QR 코드를 생성합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "생성"),
		@ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "SESSION_NOT_FOUND",
					summary = "해당 ID의 일정이 존재하지 않음",
					value = """
						"code": "SESSION_NOT_FOUND",
						"message": "일정을 찾을 수 없습니다."
						"""
				))),
		@ApiResponse(responseCode = "409", description = "중복 및 충돌",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "QR_ALREADY_ACTIVE",
					summary = "해당 일정에 이미 활성(미만료) QR 코드 존재",
					value = """
						"code": "QR_ALREADY_ACTIVE",
						"message": "이미 활성화된 QR 코드가 있습니다."
						"""
				))),
	})
	public ResponseEntity<CommonResponse<SessionResponseDTO.CreateQrcodeResult>> createQrcode(
		@Schema(description = "일정 ID") @PathVariable(name = "sessionId") Long sessionId) {
		SessionResponseDTO.CreateQrcodeResult result = sessionCommandService.createQrcode(sessionId);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.CREATED);
	}
}
