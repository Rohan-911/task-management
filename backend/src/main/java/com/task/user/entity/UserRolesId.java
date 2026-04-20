package com.task.user.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserRolesId implements Serializable {

    private Integer userId;
    private Integer userRoleId;

    public UserRolesId() {}

    public UserRolesId(Integer userId, Integer userRoleId) {
        this.userId = userId;
        this.userRoleId = userRoleId;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getUserRoleId() { return userRoleId; }
    public void setUserRoleId(Integer userRoleId) { this.userRoleId = userRoleId; }

   

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRolesId)) return false;
        UserRolesId that = (UserRolesId) o;
        return userId.equals(that.userId) &&
               userRoleId.equals(that.userRoleId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode() + userRoleId.hashCode();
    }
}