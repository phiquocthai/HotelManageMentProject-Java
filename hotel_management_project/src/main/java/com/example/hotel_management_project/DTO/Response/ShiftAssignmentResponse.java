package com.example.hotel_management_project.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftAssignmentResponse {

    private Long id;
    private Long staffId;
    private String staffName;
    private Long workShiftId;
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate workDate;

    // Audit fields
    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;
}
