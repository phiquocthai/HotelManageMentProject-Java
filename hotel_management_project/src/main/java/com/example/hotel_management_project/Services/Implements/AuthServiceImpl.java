package com.example.hotel_management_project.Services.Implements;

import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.DTO.Request.LoginRequest;
import com.example.hotel_management_project.Models.Entity.Staff;
import com.example.hotel_management_project.Models.Enums.ErrorCode;
import com.example.hotel_management_project.Repository.AuthRepo;
import com.example.hotel_management_project.Services.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthRepo authRepo;
    private final PasswordEncoder passwordEncoder;
    public AuthServiceImpl(AuthRepo authRepo, PasswordEncoder passwordEncoder) {
        this.authRepo = authRepo;
        this.passwordEncoder = passwordEncoder;
    }
    public Staff loginUser(LoginRequest loginRequest) {
        Staff user = authRepo
                .findByUsernameOrEmailOrPhoneNumber(loginRequest.getAccount())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));



        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        return user;
    }
}
