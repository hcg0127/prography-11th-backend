package com.prography.api.session.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prography.api.session.domain.Qrcode;
import com.prography.api.session.domain.Session;

public interface QrcodeRepository extends JpaRepository<Qrcode, Long> {
	Optional<Qrcode> findTopBySessionOrderByExpiredAtDesc(Session session);

	List<Qrcode> findAllBySessionIdIn(List<Long> sessionIdList);
}
