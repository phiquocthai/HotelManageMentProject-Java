package com.example.hotel_management_project.Controller;

import com.example.hotel_management_project.DTO.Request.LoginRequest;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import com.example.hotel_management_project.Models.Entity.Staff;
import com.example.hotel_management_project.Services.AuthService;
import com.example.hotel_management_project.Utils.JWTUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JWTUtils jwtUtils;
    public AuthController(AuthService authService, JWTUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        // Implementation for login logic
        Staff user= authService.loginUser(loginRequest);
        String jwt= jwtUtils.generateTokenFromUser(user.getUsername(), loginRequest.isRememberMe());

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", jwt);
        ApiResponse response = new ApiResponse();
        response.setMessage("Đăng nhập thành công");
        response.setResult(result);
        return ResponseEntity.ok(response);
    }
}
