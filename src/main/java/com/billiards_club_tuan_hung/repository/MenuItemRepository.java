package com.billiards_club_tuan_hung.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billiards_club_tuan_hung.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategoryIgnoreCase(String category);
    List<MenuItem> findByNameContainingIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
