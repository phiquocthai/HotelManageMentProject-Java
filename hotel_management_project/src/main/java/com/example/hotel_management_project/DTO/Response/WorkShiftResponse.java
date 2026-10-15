package com.example.hotel_management_project.DTO.Response;

import lombok.Data;

@Data
public class WorkShiftResponse {
    private Long id;
    private String name;
    private String startTime;
    private String endTime;
    private Boolean active;
}
