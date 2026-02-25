package com.prography.api.session.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prography.api.global.common.CommonResponse;
import com.prography.api.session.dto.SessionResponseDTO;
import com.prography.api.session.service.SessionCommandService;

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
@RequestMapping("/admin/qrcodes")
public class AdminQrcodeController {

	private final SessionCommandService sessionCommandService;

	@PutMapping("{qrCodeId}")
	@Operation(
		summary = "QR 코드 갱신",
		description = "기존 QR 코드를 즉시 만료시키고, 동일 일정에 새 QR 코드를 생성합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공"),
		@ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "QR_NOT_FOUND",
					summary = "해당 ID의 QR 코드가 존재하지 않음",
					value = """
						"code": "QR_NOT_FOUND",
						"message": "QR 코드를 찾을 수 없습니다."
						"""
				)))
	})
	public ResponseEntity<CommonResponse<SessionResponseDTO.CreateQrcodeResult>> refreshQrcode(
		@Schema(description = "기존 QR 코드 ID") @PathVariable(name = "qrCodeId") Long qrCodeId) {
		SessionResponseDTO.CreateQrcodeResult result = sessionCommandService.refreshQrcode(qrCodeId);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.OK);
	}
}
