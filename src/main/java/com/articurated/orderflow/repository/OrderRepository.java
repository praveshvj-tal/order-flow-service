package com.articurated.orderflow.repository;

import com.articurated.orderflow.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

