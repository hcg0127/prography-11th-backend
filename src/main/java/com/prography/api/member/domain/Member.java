package com.prography.api.member.domain;

import com.prography.api.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "members")
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String loginId;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private MemberStatus status = MemberStatus.ACTIVE;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private MemberRole role = MemberRole.MEMBER;

	public void updateProfile(String name, String phone) {
		if (name != null)
			this.name = name;
		if (phone != null)
			this.phone = phone;
	}

	public void withdrawn() {
		this.status = MemberStatus.WITHDRAWN;
	}
}
