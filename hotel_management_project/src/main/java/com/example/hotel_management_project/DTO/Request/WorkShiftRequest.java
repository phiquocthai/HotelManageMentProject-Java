package com.example.hotel_management_project.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class WorkShiftRequest {
    @NotBlank(message = "Tên ca làm việc không được để trống")
    @Size(max = 20, message = "Tên ca làm việc không được vượt quá 20 ký tự")
    private String name;
    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalTime startTime;
    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


}
