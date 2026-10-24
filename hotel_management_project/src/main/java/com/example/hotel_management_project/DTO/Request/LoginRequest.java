package com.example.hotel_management_project.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    @NotBlank(message = "Tài khoản không được để trống")
    @Size(max = 50, message = "Tài khoản không được vượt quá 50 ký tự")
    private String account;
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
    private boolean isRememberMe;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return isRememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        isRememberMe = rememberMe;
    }
}
