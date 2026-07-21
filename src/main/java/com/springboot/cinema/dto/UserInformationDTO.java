package com.springboot.cinema.dto;

import com.springboot.cinema.entity.Role;

public class UserInformationDTO {
    private int userId;
    private String fullName;
    private Role role;

    public UserInformationDTO() {
    }

    public UserInformationDTO(int userId, String fullName, Role role) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserInformationDTO{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                '}';
    }
}
