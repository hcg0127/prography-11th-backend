package com.prography.api.cohort.domain;

import java.util.ArrayList;
import java.util.List;

import com.prography.api.global.common.BaseTimeEntity;
import com.prography.api.member.domain.CohortMember;

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
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "teams", uniqueConstraints = {
	@UniqueConstraint(name = "uk_team_name_cohort", columnNames = {"name", "cohort_id"})
})
public class Team extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cohort_id")
	private Cohort cohort;

	@OneToMany(mappedBy = "team")
	@Builder.Default
	private List<CohortMember> cohortMemberList = new ArrayList<>();
}
