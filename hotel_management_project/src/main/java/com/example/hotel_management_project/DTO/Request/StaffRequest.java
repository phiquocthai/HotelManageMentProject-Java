package com.example.hotel_management_project.DTO.Request;

import com.example.hotel_management_project.Annotation.SDT;
import com.example.hotel_management_project.Models.Enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StaffRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(max = 30, message = "Tên đăng nhập không được vượt quá 30 ký tự")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Định dạng email không hợp lệ")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    @Size(min = 6, max = 255, message = "Mật khẩu phải từ 6 ký tự trở lên")
    private String password;

    @NotBlank(message = "Số điện thoại không được để trống")
    @SDT
    private String phoneNumber;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 30, message = "Họ và tên không được vượt quá 30 ký tự")
    private String fullName;

    @NotBlank(message = "Chức danh không được để trống")
    @Size(max = 50, message = "Chức danh không được vượt quá 50 ký tự")
    private String jobTitle;

    @NotNull(message = "Vai trò không được để trống")
    private Role role;

    @Size(max = 255, message = "Đường dẫn ảnh không được vượt quá 255 ký tự")
    private String profileImage;
    
    private Boolean active;
}
