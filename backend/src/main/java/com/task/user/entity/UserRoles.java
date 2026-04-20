package com.task.user.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "UserRoles")
public class UserRoles {

	@EmbeddedId
	private UserRolesId id;

	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "UserID")
	@JsonBackReference
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("userRoleId")
	@JoinColumn(name = "UserRoleID")
	private UserRole role;
	// Getters & Setters

	public UserRolesId getId() {
		return id;
	}

	public void setId(UserRolesId id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}
}