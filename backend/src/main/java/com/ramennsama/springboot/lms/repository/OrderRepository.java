package com.ramennsama.springboot.lms.repository;

import com.ramennsama.springboot.lms.entity.Order;
import com.ramennsama.springboot.lms.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime time);
}
