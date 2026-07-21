package com.springboot.cinema.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Staff {
    @Id
    @Column(name = "staff_id")
    private int id;

    @Column(name = "employee_code")
    private String employeeCode;

    @OneToOne
    @MapsId
    @JoinColumn(name = "staff_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(mappedBy = "staff")
    private List<Booking> bookingList;

    public Staff() {
    }

    public Staff(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Booking> getBookingList() {
        return bookingList;
    }

    public void setBookingList(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }
}
