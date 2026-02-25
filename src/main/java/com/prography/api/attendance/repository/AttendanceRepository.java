package com.prography.api.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.prography.api.attendance.domain.Attendance;

import io.lettuce.core.dynamic.annotation.Param;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

	@Query("SELECT a.status, COUNT(a) FROM Attendance a " +
		"WHERE a.session.id = :sessionId " +
		"GROUP BY a.status")
	List<Object[]> countByStatusBySessionId(@Param("sessionId") Long sessionId);

	Integer countBySessionId(Long sessionId);
}
