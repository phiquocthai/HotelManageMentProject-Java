package com.example.hotel_management_project.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {
    private Long id;
    private Long shiftAssignmentId;
    private String staffName;
    private String workShiftName;
    private String shiftName;
    private LocalDate workDate;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status;
    private String note;
    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;
}
