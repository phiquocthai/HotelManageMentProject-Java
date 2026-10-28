package com.example.hotel_management_project.DTO.Request;

import com.example.hotel_management_project.Models.Enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
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
public class BookingRequest {
    @NotNull(message = "Mã khách hàng không được để trống")
    private Long customerId;

    @NotNull(message = "Mã phòng không được để trống")
    private Long roomId;

    @NotNull(message = "Ngày nhận phòng không được để trống")
    private LocalDate checkInDate;

    @NotNull(message = "Ngày trả phòng dự kiến không được để trống")
    private LocalDate expectedCheckOutDate;

    private LocalDateTime actualCheckInTime;
    private LocalDateTime actualCheckOutTime;
    private Integer numberOfGuests;
    private String note;
    private BookingStatus status;
}
