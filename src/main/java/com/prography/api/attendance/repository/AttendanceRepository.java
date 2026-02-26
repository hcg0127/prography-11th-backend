package com.prography.api.attendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prography.api.attendance.domain.Attendance;
import com.prography.api.member.domain.Member;
import com.prography.api.session.domain.Session;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

	@Query("SELECT a.status, COUNT(a) FROM Attendance a " +
		"WHERE a.session.id = :sessionId " +
		"GROUP BY a.status")
	List<Object[]> countByStatusBySessionId(@Param("sessionId") Long sessionId);

	Integer countBySessionId(Long sessionId);

	@Query("SELECT a.session.id, a.status, COUNT(a) " +
		"FROM Attendance a " +
		"WHERE a.session.id IN :sessionIds " +
		"GROUP BY a.session.id, a.status")
	List<Object[]> countStatusBySessionIds(@Param("sessionIds") List<Long> sessionIds);

	Optional<Attendance> findBySessionAndMember(Session session, Member member);
}
