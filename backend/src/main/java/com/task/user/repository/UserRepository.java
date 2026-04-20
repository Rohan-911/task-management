package com.task.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.task.user.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByUsername(String username);

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.username = :username")
	Optional<User> findByUsernameWithRoles(@Param("username") String username);

	// Removed findAllUsersWithRoles as we can just use findAll(Pageable) from JpaRepository

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.userId = :id")
	Optional<User> findByIdWithRoles(@Param("id") Integer id);

	boolean existsByUsername(String username);
	boolean existsByEmail(String email); // ✅ ADD THIS

	@Query(value = """
			    SELECT DISTINCT u
			    FROM User u
			    JOIN u.userRoles ur
			    JOIN ur.role r
			    WHERE LOWER(r.roleName) = LOWER(:roleName)
			""", 
			countQuery = """
			    SELECT count(DISTINCT u)
			    FROM User u
			    JOIN u.userRoles ur
			    JOIN ur.role r
			    WHERE LOWER(r.roleName) = LOWER(:roleName)
			""")
	Page<User> findUsersByRole(@Param("roleName") String roleName, Pageable pageable);

	Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}