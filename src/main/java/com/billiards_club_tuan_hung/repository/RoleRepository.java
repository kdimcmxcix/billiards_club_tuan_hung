package com.billiards_club_tuan_hung.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billiards_club_tuan_hung.entity.EnumRole;
import com.billiards_club_tuan_hung.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(EnumRole name);
}
