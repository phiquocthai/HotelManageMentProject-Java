package com.example.hotel_management_project.DTO.Response;

import com.example.hotel_management_project.Models.Enums.Role;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StaffResponse {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String fullName;
    private String jobTitle;
    private Role role;
    private String profileImage;
    private Boolean active;
    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;
}
