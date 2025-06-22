package com.billiards_club_tuan_hung.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billiards_club_tuan_hung.entity.WorkingSession;

public interface WorkingSessionRepository extends JpaRepository<WorkingSession, Long> {
    List<WorkingSession> findByStaffId(long staffId);

    Optional<WorkingSession> findByStaffIdAndEndTimeIsNull(Long staffId);

    boolean existsByStaffIdAndEndTimeIsNull(Long staffId);
}