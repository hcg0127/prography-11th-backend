package com.prography.api.cohort.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prography.api.cohort.dto.CohortResponseDTO;
import com.prography.api.cohort.service.CohortQueryService;
import com.prography.api.global.common.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
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
}
