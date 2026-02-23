package com.prography.api.cohort.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CohortRepositoryImpl implements CohortRepositoryCustom {

	private final JPAQueryFactory queryFactory;
}
