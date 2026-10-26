package com.example.hotel_management_project.DTO.Request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShiftAssignmentRequest {

    @NotNull(message = "Mã nhân viên không được để trống")
    private Long staffId;

    @NotNull(message = "Mã ca làm việc không được để trống")
    private Long workShiftId;

    @NotNull(message = "Ngày làm việc không được để trống")
    private LocalDate workDate;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}