package com.billiards_club_tuan_hung.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billiards_club_tuan_hung.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBilliardsTableId(long tableId);

    Optional<Order> findByBilliardsTableIdAndIsPaidFalse(long tableId);
}
