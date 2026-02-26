package com.prography.api.attendance.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prography.api.attendance.dto.AttendanceRequestDTO;
import com.prography.api.attendance.dto.AttendanceResponseDTO;
import com.prography.api.attendance.service.AttendanceCommandService;
import com.prography.api.global.common.CommonResponse;

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
@RequestMapping("/attendances")
public class AttendanceController {

	private final AttendanceCommandService attendanceCommandService;

	@PostMapping()
	@Operation(
		summary = "QR 출석 체크",
		description = "QR 코드의 hashValue와 memberId를 전송하여 출석 체크를 수행합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "생성"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = {
					@ExampleObject(
						name = "QR_INVALID",
						summary = "hashValue에 해당하는 QR 코드가 없음",
						value = """
							"code": "QR_INVALID",
							"message": "유효하지 않은 QR 코드입니다."
							"""
					),
					@ExampleObject(
						name = "QR_EXPIRED",
						summary = "QR 코드가 만료됨",
						value = """
							"code": "QR_EXPIRED",
							"message": "만료된 QR 코드입니다."
							"""
					),
					@ExampleObject(
						name = "SESSION_NOT_IN_PROGRESS",
						summary = "일정이 IN_PROGRESS 상태가 아님",
						value = """
							"code": "SESSION_NOT_IN_PROGRESS",
							"message": "진행 중인 일정이 아닙니다."
							"""
					),
					@ExampleObject(
						name = "DEPOSIT_INSUFFICIENT",
						summary = "보증금 잔액 부족 (패널티 차감 불가)",
						value = """
							"code": "DEPOSIT_INSUFFICIENT",
							"message": "보증금 잔액이 부족합니다."
							"""
					)
				})),
		@ApiResponse(responseCode = "403", description = "금지된 접근",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "MEMBER_WITHDRAWN",
					summary = "탈퇴한 회원",
					value = """
						"code": "MEMBER_WITHDRAWN",
						"message": "탈퇴한 회원입니다."
						"""
				))),
		@ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = {
					@ExampleObject(
						name = "MEMBER_NOT_FOUND",
						summary = "회원이 존재하지 않음",
						value = """
							"code": "MEMBER_NOT_FOUND",
							"message": "회원을 찾을 수 없습니다."
							"""
					),
					@ExampleObject(
						name = "COHORT_MEMBER_NOT_FOUND",
						summary = "현재 기수의 기수회원 정보가 없음",
						value = """
							"code": "COHORT_MEMBER_NOT_FOUND",
							"message": "기수 회원 정보를 찾을 수 없습니다."
							"""
					)
				})),
		@ApiResponse(responseCode = "409", description = "중복 및 충돌",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = @ExampleObject(
					name = "ATTENDANCE_ALREADY_CHECKED",
					summary = "해당 일정에 이미 출결 기록 존재",
					value = """
						"code": "ATTENDANCE_ALREADY_CHECKED",
						"message": "이미 출결 체크가 완료되었습니다."
						"""
				))),
	})
	public ResponseEntity<CommonResponse<AttendanceResponseDTO.QrcodeAttendanceCheckResult>> qrcodeAttendanceCheck(
		@Valid @RequestBody AttendanceRequestDTO.QrcodeAttendanceCheck request) {
		AttendanceResponseDTO.QrcodeAttendanceCheckResult result =
			attendanceCommandService.qrcodeAttendanceCheck(request);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.CREATED);
	}
}
