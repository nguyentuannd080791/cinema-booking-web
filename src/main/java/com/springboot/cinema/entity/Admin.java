package com.springboot.cinema.entity;

import jakarta.persistence.*;

@Entity
public class Admin {
    @Id
    @Column(name = "admin_id")
    private int id;

    @Column(name = "admin_level")
    private String adminLevel;

    @OneToOne
    @MapsId
    @JoinColumn(name = "admin_id", referencedColumnName = "user_id")
    private User user;

    public Admin() {
    }

    public Admin(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
