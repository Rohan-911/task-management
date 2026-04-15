package com.task.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.user.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

	Optional<UserRole> findByRoleNameIgnoreCase(String roleName);
}