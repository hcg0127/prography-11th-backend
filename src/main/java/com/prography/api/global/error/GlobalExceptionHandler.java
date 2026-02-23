package com.prography.api.global.error;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.prography.api.global.common.CommonResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<CommonResponse<Void>> handleBusinessException(BusinessException e,
		HttpServletRequest request) {

		log.warn("Business Exception: {} | URI: {}", e.getMessage(), request.getRequestURI());
		BaseErrorCode errorCode = e.getErrorCode();

		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(CommonResponse.error(errorCode));
	}

	@ExceptionHandler(SystemException.class)
	public ResponseEntity<CommonResponse<Void>> handleSystemException(SystemException e, HttpServletRequest request) {

		log.error("System Exception: {} | URI: {}", e.getMessage(), request.getRequestURI());
		BaseErrorCode errorCode = e.getErrorCode();

		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(CommonResponse.error(errorCode));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
		HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
			.map(fieldError -> fieldError.getField() + ": " +
				fieldError.getDefaultMessage())
			.collect(Collectors.joining(", "));

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponse.error(CommonErrorCode.INVALID_INPUT.getCode(), errorMessage));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
		HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		Throwable cause = ex.getCause();
		String details = "요청 데이터의 형식이 맞지 않습니다.";

		if (cause instanceof InvalidFormatException ife) {
			List<JsonMappingException.Reference> path = ife.getPath();
			if (!path.isEmpty()) {
				String fieldName = path.getFirst().getFieldName();
				Class<?> targetType = ife.getTargetType();
				if (targetType != null && targetType.isEnum()) {
					String allowedValue = Arrays.toString(targetType.getEnumConstants());
					details = String.format("'%s' 필드는 다음 값 중 하나여야 합니다: %s", fieldName, allowedValue);
				} else if (targetType != null) {
					details = String.format("'%s' 필드는 '%s' 타입이어야 합니다.", fieldName, targetType.getSimpleName());
				}
			}
		} else if (cause instanceof MismatchedInputException mie) {
			List<JsonMappingException.Reference> path = mie.getPath();
			if (!path.isEmpty()) {
				details = String.format("'%s' 필드의 값이 누락되었거나 형식이 올바르지 않습니다.", path.getFirst().getFieldName());
			}
		} else if (cause instanceof JsonParseException) {
			details = "JSON 문법 오류가 발생했습니다.";
		} else {
			details = "요청 본문을 읽을 수 없습니다.";
		}

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponse.error(CommonErrorCode.INVALID_INPUT.getCode(), details));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<CommonResponse<Void>> handleDataIntegrityViolationException(
		DataIntegrityViolationException dive) {

		log.error("DataIntegrityViolationException occurred: {}", dive.getMessage());

		return ResponseEntity
			.status(HttpStatus.CONFLICT)
			.body(CommonResponse.error(CommonErrorCode.INVALID_INPUT.getCode(), "데이터 제약조건을 위반했습니다."));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<CommonResponse<Void>> handleConstraintViolationException(ConstraintViolationException cve) {

		String errorMessage = cve.getConstraintViolations().stream()
			.map(ConstraintViolation::getMessage)
			.findFirst()
			.orElse("유효하지 않은 데이터입니다.");

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponse.error(CommonErrorCode.INVALID_INPUT.getCode(), errorMessage));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<CommonResponse<Void>> handleException(Exception e, HttpServletRequest request) {

		log.error("Unhandled Exception occurred: [{}] {}", request.getMethod(), request.getRequestURI(), e);

		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(CommonResponse.error(CommonErrorCode.INTERNAL_ERROR));
	}
}
