package com.prography.api.attendance.domain;

import com.prography.api.global.common.BaseTimeEntity;
import com.prography.api.member.domain.CohortMember;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "deposit_histories")
public class DepositHistory extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private DepositType type = DepositType.INITIAL;

	@Column(nullable = false)
	private int amount;

	@Column(nullable = false)
	private int balanceAfter;

	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cohort_member_id")
	private CohortMember cohortMember;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attendance_id")
	private Attendance attendance;
}
