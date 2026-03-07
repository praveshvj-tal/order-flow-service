package com.articurated.orderflow.repository;

import com.articurated.orderflow.entity.OrderStateHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStateHistoryRepository extends JpaRepository<OrderStateHistory, Long> {
    List<OrderStateHistory> findByEntityIdOrderByTimestampAsc(Long entityId);
}

