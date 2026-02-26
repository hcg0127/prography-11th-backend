package com.prography.api.attendance.exception;

import org.springframework.http.HttpStatus;

import com.prography.api.global.error.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DepositErrorCode implements BaseErrorCode {

	DEPOSIT_INSUFFICIENT(HttpStatus.BAD_REQUEST, "DEPOSIT_INSUFFICIENT", "보증금 잔액이 부족합니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
