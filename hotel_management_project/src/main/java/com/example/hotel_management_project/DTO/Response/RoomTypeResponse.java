package com.example.hotel_management_project.DTO.Response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RoomTypeResponse {
    private Long id;
    private String name;
    private BigDecimal basePrice;
    private Integer maxOccupancy;
    private String imagePath;
    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;
    private Boolean active;
}
