package com.example.hotel_management_project.DTO.Response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerResponse {
    private Long id;
    private String fullName;
    private String citizenId;
    private String email;
    private String phoneNumber;
    private String imagePath;
    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;
    private Boolean active;
}
