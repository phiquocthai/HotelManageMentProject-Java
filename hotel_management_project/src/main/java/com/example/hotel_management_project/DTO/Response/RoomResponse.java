package com.example.hotel_management_project.DTO.Response;

import com.example.hotel_management_project.Models.Entity.Staff;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
public class RoomResponse {
    private Long id;
    private Long roomTypeId;
    private String roomTypeName;
    private String roomNumber;
    private Integer floor;
    private String status;
    private String imagePath;
    private String note;

    private Staff createdBy;

    private LocalDateTime createdAt;


    private Staff modifiedBy;


    private LocalDateTime modifiedAt;

    private Boolean active;
}
