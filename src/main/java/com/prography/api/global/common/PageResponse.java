package com.prography.api.global.common;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"content", "page", "size", "totalElements", "totalPages"})
public record PageResponse<T>(
	List<T> content,
	int page,
	int size,
	long totalElements,
	int totalPages
) {

	public static <T> PageResponse<T> from(Page<T> pageData) {
		return new PageResponse<>(
			pageData.getContent(),
			pageData.getNumber(),
			pageData.getSize(),
			pageData.getTotalElements(),
			pageData.getTotalPages()
		);
	}
}
