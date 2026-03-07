package com.articurated.orderflow.repository;

import com.articurated.orderflow.entity.ReturnStateHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnStateHistoryRepository extends JpaRepository<ReturnStateHistory, Long> {
    List<ReturnStateHistory> findByEntityIdOrderByTimestampAsc(Long entityId);
}

