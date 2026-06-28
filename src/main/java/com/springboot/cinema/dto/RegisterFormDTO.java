package com.springboot.cinema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegisterFormDTO {
    @NotBlank(message = "Email không được bỏ trống")
    private String email;

    @NotBlank(message = "Tên không được bỏ trống")
    @Pattern(regexp = "^.{2,50}$", message = "Tên quá ngắn hoặc quá dài")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,20}$", message = "Mật khẩu phải có ít nhất 8 ký tự, trong đó có 1 ký tự hoa và 1 chữ số")
    private String hashPassword;

    public RegisterFormDTO() {
    }

    public RegisterFormDTO(String email, String fullName, String phone, String hashPassword) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.hashPassword = hashPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "RegisterFormDTO{" +
                "email='" + email + '\'' +
                ", hashPassword='" + hashPassword + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
