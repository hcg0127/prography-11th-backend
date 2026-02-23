package com.prography.api.member.exception;

import org.springframework.http.HttpStatus;

import com.prography.api.global.error.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

	LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "LOGIN_FAILED", "로그인 아이디 또는 비밀번호가 올바르지 않습니다."),
	DUPLICATED_LOGIN_ID(HttpStatus.CONFLICT, "DUPLICATED_LOGIN_ID", "이미 사용 중인 로그인 아이디입니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
