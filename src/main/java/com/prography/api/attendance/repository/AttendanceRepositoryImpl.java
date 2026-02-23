package com.prography.api.attendance.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AttendanceRepositoryImpl implements AttendanceRepositoryCustom {

	private final JPAQueryFactory queryFactory;
}
