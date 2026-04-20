package com.task.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.user.entity.UserRoles;
import com.task.user.entity.UserRolesId;

public interface UserRolesRepository extends JpaRepository<UserRoles, UserRolesId> {
}