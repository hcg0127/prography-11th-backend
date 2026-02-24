package com.prography.api.member.repository;

import java.util.List;

import com.prography.api.member.domain.CohortMember;
import com.prography.api.member.dto.MemberRequestDTO;

public interface CohortMemberRepositoryCustom {

	List<CohortMember> findAllForDashboard(MemberRequestDTO.GetMemberDashboard request);
}
