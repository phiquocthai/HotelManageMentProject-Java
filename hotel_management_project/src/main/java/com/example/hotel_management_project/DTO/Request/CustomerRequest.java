package com.example.hotel_management_project.DTO.Request;

import com.example.hotel_management_project.Annotation.CCCD;
import com.example.hotel_management_project.Annotation.SDT;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 30, message = "Họ và tên không được vượt quá 30 ký tự")
    private String fullName;

    @NotBlank
    @CCCD
    private String citizenId;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Định dạng email không hợp lệ")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @SDT
    private String phoneNumber;

    @Size(max = 255, message = "Đường dẫn ảnh không được vượt quá 255 ký tự")
    private String imagePath;
}
