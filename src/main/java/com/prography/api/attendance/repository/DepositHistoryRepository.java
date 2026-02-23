package com.prography.api.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prography.api.attendance.domain.DepositHistory;

public interface DepositHistoryRepository extends JpaRepository<DepositHistory, Integer> {
}
