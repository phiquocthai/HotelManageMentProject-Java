package com.example.hotel_management_project.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRequest {

    @NotNull(message = "Mã phân ca không được để trống")
    private Long shiftAssignmentId;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    @NotBlank(message = "Trạng thái chấm công không được để trống")
    private String status;

    @Size(max = 255, message = "Ghi chú không được vượt quá 255 ký tự")
    private String note;
}
