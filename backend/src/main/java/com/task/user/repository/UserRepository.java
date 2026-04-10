package com.task.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.task.user.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	// ✅ Get user with roles (for login / profile)
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.username = :username")
	Optional<User> findByUsernameWithRoles(@Param("username") String username);

	// ✅ Get ALL users with roles (FIXED MAIN ISSUE)
	@Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role")
	List<User> findAllUsersWithRoles();

	// ✅ Get user by ID with roles
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.userId = :id")
	Optional<User> findByIdWithRoles(@Param("id") Integer id);

	// ✅ Check username exists
	boolean existsByUsername(String username);

	// ✅ Search users
	List<User> findByUsernameContainingIgnoreCase(String username);
}