package com.prography.api.session.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepositoryCustom {

	private final JPAQueryFactory queryFactory;
}
