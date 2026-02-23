package com.prography.api.session.exception;

import org.springframework.http.HttpStatus;

import com.prography.api.global.error.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SessionErrorCode implements BaseErrorCode {

	SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "SESSION_NOT_FOUND", "일정을 찾을 수 없습니다."),
	SESSION_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "SESSION_ALREADY_CANCELLED", "이미 취소된 일정입니다."),
	SESSION_NOT_IN_PROGRESS(HttpStatus.BAD_REQUEST, "SESSION_NOT_IN_PROGRESS", "진행 중인 일정이 아닙니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
