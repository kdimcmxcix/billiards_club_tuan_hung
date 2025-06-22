package com.billiards_club_tuan_hung.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billiards_club_tuan_hung.entity.BilliardsTable;

public interface BilliardsTableRepository extends JpaRepository<BilliardsTable, Long> {
    List<BilliardsTable> findByInUseFalse();
}
