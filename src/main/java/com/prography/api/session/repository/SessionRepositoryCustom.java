package com.prography.api.session.repository;

import java.util.List;

import com.prography.api.cohort.domain.Cohort;
import com.prography.api.session.domain.Session;
import com.prography.api.session.dto.SessionRequestDTO;

public interface SessionRepositoryCustom {

	List<Session> findSessionsByCohortAndCond(Cohort cohort, SessionRequestDTO.GetSessionListAdmin request);
}
