package com.example.hotel_management_project.DTO.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftAssignmentRequest {

    @NotNull(message = "Mã nhân viên không được để trống")
    private Long staffId;

    @NotNull(message = "Mã ca làm việc không được để trống")
    private Long workShiftId;

    @NotNull(message = "Ngày làm việc không được để trống")
    private LocalDate workDate;
}
