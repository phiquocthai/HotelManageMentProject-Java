package com.example.hotel_management_project.DTO.Request;

import com.example.hotel_management_project.Annotation.SDT;
import com.example.hotel_management_project.Models.Enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StaffRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Định dạng email không hợp lệ")
    private String email;

    private String password;
    @NotBlank(message = "Số điện thoại không được để trống")
    @SDT
    private String phoneNumber;

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @NotBlank(message = "Chức danh không được để trống")
    private String jobTitle;

    @NotNull(message = "Vai trò không được để trống")
    private Role role;

    private String profileImage;
    
    private Boolean active;
}
