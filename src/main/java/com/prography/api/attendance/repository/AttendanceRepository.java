package com.prography.api.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prography.api.attendance.domain.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
}
