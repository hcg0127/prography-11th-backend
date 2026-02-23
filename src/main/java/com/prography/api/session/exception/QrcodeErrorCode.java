package com.prography.api.session.exception;

import org.springframework.http.HttpStatus;

import com.prography.api.global.error.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QrcodeErrorCode implements BaseErrorCode {

	QR_NOT_FOUND(HttpStatus.NOT_FOUND, "QR_NOT_FOUND", "QR 코드를 찾을 수 없습니다."),
	QR_INVALID(HttpStatus.BAD_REQUEST, "QR_INVALID", "유효하지 않은 QR 코드입니다."),
	QR_EXPIRED(HttpStatus.BAD_REQUEST, "QR_EXPIRED", "만료된 QR 코드입니다."),
	QR_ALREADY_ACTIVE(HttpStatus.CONFLICT, "QR_ALREADY_ACTIVE", "이미 활성화된 QR 코드가 있습니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
