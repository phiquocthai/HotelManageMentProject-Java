package com.example.hotel_management_project.Services;

import com.example.hotel_management_project.DTO.Request.LoginRequest;
import com.example.hotel_management_project.Models.Entity.Staff;

public interface AuthService {
    Staff loginUser(LoginRequest loginRequest);
}
