package com.prography.api.cohort.domain;

import java.util.ArrayList;
import java.util.List;

import com.prography.api.global.common.BaseTimeEntity;
import com.prography.api.member.domain.CohortMember;
import com.prography.api.session.domain.Session;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "cohorts")
public class Cohort extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private int generation;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private boolean active;

	@OneToMany(mappedBy = "cohort", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Session> sessionList = new ArrayList<>();

	@OneToMany(mappedBy = "cohort", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CohortMember> cohortMemberList = new ArrayList<>();

	@OneToMany(mappedBy = "cohort", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Part> partList = new ArrayList<>();

	@OneToMany(mappedBy = "cohort", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Team> teamList = new ArrayList<>();

	public void activate() {
		this.active = true;
	}

	public void deactivate() {
		this.active = false;
	}
}
