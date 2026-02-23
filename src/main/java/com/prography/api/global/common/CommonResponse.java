package com.prography.api.global.common;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.prography.api.global.error.BaseErrorCode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"success", "data", "error"})
public class CommonResponse<T> {

	private final boolean success;
	private final T data;
	private final ErrorDetail error;

	public static <T> CommonResponse<T> success(T data) {
		return new CommonResponse<>(true, data, null);
	}

	public static <T> CommonResponse<T> error(BaseErrorCode errorCode) {
		return new CommonResponse<>(false, null, new ErrorDetail(errorCode.getCode(), errorCode.getMessage()));
	}

	public static <T> CommonResponse<T> error(String code, String message) {
		return new CommonResponse<>(false, null, new ErrorDetail(code, message));
	}

	public record ErrorDetail(String code, String message) {
	}
}
