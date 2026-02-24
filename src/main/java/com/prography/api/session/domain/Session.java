package com.prography.api.session.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.prography.api.attendance.domain.Attendance;
import com.prography.api.cohort.domain.Cohort;
import com.prography.api.global.common.BaseTimeEntity;
import com.prography.api.session.dto.SessionRequestDTO;

import jakarta.persistence.CascadeType;
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
@Table(name = "sessions")
public class Session extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private LocalDate date;

	@Column(nullable = false)
	private LocalTime time;

	@Column(nullable = false)
	private String location;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private SessionStatus status = SessionStatus.SCHEDULED;

	@Column(nullable = false)
	@Builder.Default
	private boolean qrActive = true;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cohort_id")
	private Cohort cohort;

	@OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Qrcode> qrcodeList = new ArrayList<>();

	@OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Attendance> attendanceList = new ArrayList<>();

	public void updateSession(SessionRequestDTO.UpdateSession request) {
		if (request.title() != null)
			this.title = request.title();
		if (request.date() != null)
			this.date = request.date();
		if (request.time() != null)
			this.time = request.time();
		if (request.location() != null)
			this.location = request.location();
		if (request.status() != null)
			this.status = request.status();
	}
}
