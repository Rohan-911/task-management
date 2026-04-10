package com.task.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.user.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
}