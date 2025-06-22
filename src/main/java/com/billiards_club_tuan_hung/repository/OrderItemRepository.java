package com.billiards_club_tuan_hung.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billiards_club_tuan_hung.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(long orderId);

    List<OrderItem> findByMenuItemId(long menuItemId);
}
