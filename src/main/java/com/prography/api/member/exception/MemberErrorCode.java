package com.prography.api.member.exception;

import org.springframework.http.HttpStatus;

import com.prography.api.global.error.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

	MEMBER_WITHDRAWN(HttpStatus.FORBIDDEN, "MEMBER_WITHDRAWN", "탈퇴한 회원입니다."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."),
	MEMBER_ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "MEMBER_ALREADY_WITHDRAWN", "이미 탈퇴한 회원입니다."),
	COHORT_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "COHORT_MEMBER_NOT_FOUND", "기수 회원 정보를 찾을 수 없습니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
