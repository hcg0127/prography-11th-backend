package com.prography.api.attendance.exception;

import org.springframework.http.HttpStatus;

import com.prography.api.global.error.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttendanceErrorCode implements BaseErrorCode {

	ATTENDANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "ATTENDANCE_NOT_FOUND", "출결 기록을 찾을 수 없습니다."),
	ATTENDANCE_ALREADY_CHECKED(HttpStatus.CONFLICT, "ATTENDANCE_ALREADY_CHECKED", "이미 출결 체크가 완료되었습니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
