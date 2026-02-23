package com.prography.api.global.error;

import lombok.Getter;

@Getter
public class SystemException extends RuntimeException {

	private final BaseErrorCode errorCode;

	public SystemException(BaseErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public SystemException(BaseErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
