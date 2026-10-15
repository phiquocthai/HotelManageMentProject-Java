package com.example.hotel_management_project.DTO.Request;

import lombok.Data;

@Data
public class RoomRequest {
    private Long roomTypeId;
    private String roomNumber;
    private Integer floor;
    private String status;
    private String imagePath;
    private String note;
}
