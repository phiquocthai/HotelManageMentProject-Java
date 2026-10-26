package com.example.hotel_management_project.DTO.Response;

import lombok.Data;

@Data
public class ShiftAssignmentResponse {
    private Long id;
    private Long staffId;
    private String staffName;
    private Long workShiftId;
    private String shiftName;
    private String workDate;
}