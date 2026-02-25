package com.prography.api.session.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.prography.api.cohort.domain.Cohort;
import com.prography.api.session.domain.QSession;
import com.prography.api.session.domain.Session;
import com.prography.api.session.domain.SessionStatus;
import com.prography.api.session.dto.SessionRequestDTO;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QSession session = QSession.session;

	@Override
	public List<Session> findSessionsByCohortAndCond(Cohort cohort, SessionRequestDTO.GetSessionListAdmin request) {
		return queryFactory
			.selectFrom(session)
			.where(
				session.cohort.eq(cohort),
				dateGoe(request.dateFrom()),
				dateLoe(request.dateTo()),
				statusEq(request.status())
			)
			.orderBy(session.date.desc(), session.time.desc())
			.fetch();
	}

	private BooleanExpression dateGoe(LocalDate dateFrom) {
		return dateFrom != null ? session.date.goe(dateFrom) : null;
	}

	private BooleanExpression dateLoe(LocalDate dateTo) {
		return dateTo != null ? session.date.loe(dateTo) : null;
	}

	private BooleanExpression statusEq(SessionStatus status) {
		return status != null ? session.status.eq(status) : null;
	}
}
