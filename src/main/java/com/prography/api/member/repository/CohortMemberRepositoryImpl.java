package com.prography.api.member.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.prography.api.cohort.domain.QCohort;
import com.prography.api.cohort.domain.QPart;
import com.prography.api.cohort.domain.QTeam;
import com.prography.api.member.domain.CohortMember;
import com.prography.api.member.domain.MemberStatus;
import com.prography.api.member.domain.QCohortMember;
import com.prography.api.member.domain.QMember;
import com.prography.api.member.dto.MemberRequestDTO;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CohortMemberRepositoryImpl implements CohortMemberRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QCohortMember cm = QCohortMember.cohortMember;
	private final QMember m = QMember.member;
	private final QCohort c = QCohort.cohort;
	private final QPart p = QPart.part;
	private final QTeam t = QTeam.team;

	@Override
	public List<CohortMember> findAllForDashboard(MemberRequestDTO.GetMemberDashboard request) {

		return queryFactory
			.selectFrom(cm)
			.join(cm.member, m).fetchJoin()
			.join(cm.cohort, c).fetchJoin()
			.leftJoin(cm.part, p).fetchJoin()
			.leftJoin(cm.team, t).fetchJoin()
			.where(
				eqStatus(request.status()),
				containsSearch(request.searchType(), request.searchValue())
			)
			.orderBy(cm.id.desc())
			.fetch();

	}

	private BooleanExpression eqStatus(MemberStatus status) {
		return status != null ? QMember.member.status.eq(status) : null;
	}

	private BooleanExpression containsSearch(String type, String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return switch (type) {
			case "name" -> QMember.member.name.contains(value);
			case "loginId" -> QMember.member.loginId.contains(value);
			case "phone" -> QMember.member.phone.contains(value);
			default -> null;
		};
	}
}
