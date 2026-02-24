package com.prography.api.cohort.exception;

import org.springframework.http.HttpStatus;

import com.prography.api.global.error.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CohortErrorCode implements BaseErrorCode {

	COHORT_NOT_FOUND(HttpStatus.NOT_FOUND, "COHORT_NOT_FOUND", "기수를 찾을 수 없습니다."),
	PART_NOT_FOUND(HttpStatus.NOT_FOUND, "PART_NOT_FOUND", "파트를 찾을 수 없습니다."),
	TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM_NOT_FOUND", "팀을 찾을 수 없습니다."),

	ACTIVE_COHORT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACTIVE_COHORT_NOT_FOUND", "활성화된 기수가 없습니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
