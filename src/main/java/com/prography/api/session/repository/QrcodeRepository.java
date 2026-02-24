package com.prography.api.session.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prography.api.session.domain.Qrcode;
import com.prography.api.session.domain.Session;

public interface QrcodeRepository extends JpaRepository<Qrcode, Long> {
	Qrcode findTopBySessionOrderByExpiredAtDesc(Session session);
}
