package com.task.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.task.user.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.username = :username")
	Optional<User> findByUsernameWithRoles(@Param("username") String username);

	@Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role")
	List<User> findAllUsersWithRoles();

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.userId = :id")
	Optional<User> findByIdWithRoles(@Param("id") Integer id);

	boolean existsByUsername(String username);

	@Query("""
			    SELECT DISTINCT u
			    FROM User u
			    JOIN u.userRoles ur
			    JOIN ur.role r
			    WHERE LOWER(r.roleName) = LOWER(:roleName)
			""")
	List<User> findUsersByRole(@Param("roleName") String roleName);

	List<User> findByUsernameContainingIgnoreCase(String username);
}