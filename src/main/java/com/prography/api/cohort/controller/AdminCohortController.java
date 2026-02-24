package com.prography.api.cohort.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prography.api.cohort.dto.CohortResponseDTO;
import com.prography.api.cohort.service.CohortQueryService;
import com.prography.api.global.common.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/cohorts")
public class AdminCohortController {

	private final CohortQueryService cohortQueryService;

	@GetMapping()
	@Operation(
		summary = "기수 목록 조회",
		description = "전체 기수 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공")
	})
	public ResponseEntity<CommonResponse<List<CohortResponseDTO.GetCohortResult>>> getCohortList(
	) {
		List<CohortResponseDTO.GetCohortResult> result = cohortQueryService.getCohortList();
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.OK);
	}

	@GetMapping("{cohortId}")
	@Operation(
		summary = "기수 상세 조회",
		description = "기수 정보와 소속 파트/팀 목록을 함께 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공"),
		@ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = CommonResponse.ErrorDetail.class),
				examples = {
					@ExampleObject(
						name = "COHORT_NOT_FOUND",
						summary = "cohortId에 해당하는 기수 없음",
						value = """
							"code": "COHORT_NOT_FOUND",
							"message": "기수를 찾을 수 없습니다."
							"""
					)
				}))
	})
	public ResponseEntity<CommonResponse<CohortResponseDTO.GetCohortDetailResult>> getCohortDetail(
		@Schema(description = "기수 ID") @PathVariable(name = "cohortId") Long cohortId
	) {
		CohortResponseDTO.GetCohortDetailResult result = cohortQueryService.getCohortDetail(cohortId);
		return new ResponseEntity<>(CommonResponse.success(result), HttpStatus.OK);
	}
}
