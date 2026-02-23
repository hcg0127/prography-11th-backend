package com.prography.api.session.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prography.api.session.domain.Session;

public interface SessionRepository extends JpaRepository<Session, Long>, SessionRepositoryCustom {
}
