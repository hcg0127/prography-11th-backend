package com.prography.api.member.domain;

import java.util.ArrayList;
import java.util.List;

import com.prography.api.attendance.domain.DepositHistory;
import com.prography.api.cohort.domain.Cohort;
import com.prography.api.cohort.domain.Part;
import com.prography.api.cohort.domain.Team;
import com.prography.api.global.common.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cohort_members")
public class CohortMember extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Builder.Default
	private int excuseCount = 0;

	@Column(nullable = false)
	@Builder.Default
	private int deposit = 100000;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cohort_id")
	private Cohort cohort;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "part_id")
	private Part part;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	@OneToMany(mappedBy = "cohortMember", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<DepositHistory> depositHistoryList = new ArrayList<>();
}
