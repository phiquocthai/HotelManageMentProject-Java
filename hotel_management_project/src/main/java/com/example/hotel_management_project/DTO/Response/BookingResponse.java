package com.example.hotel_management_project.DTO.Response;

import com.example.hotel_management_project.Models.Enums.BookingStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long roomId;
    private String roomNumber;
    private String roomTypeName;
    private LocalDate checkInDate;
    private LocalDate expectedCheckOutDate;
    private LocalDateTime actualCheckInTime;
    private LocalDateTime actualCheckOutTime;
    private Integer numberOfGuests;
    private String note;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
