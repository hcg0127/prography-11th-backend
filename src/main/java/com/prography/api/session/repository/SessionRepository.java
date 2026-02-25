package com.prography.api.session.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prography.api.cohort.domain.Cohort;
import com.prography.api.session.domain.Session;
import com.prography.api.session.domain.SessionStatus;

public interface SessionRepository extends JpaRepository<Session, Long>, SessionRepositoryCustom {

	List<Session> findAllByCohortAndStatusNot(Cohort cohort, SessionStatus status);
}
