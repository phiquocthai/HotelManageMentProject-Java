package com.example.hotel_management_project.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingServiceDetailResponse {

    private Long id;
    private Long bookingId;
    private String roomNumber;
    private String customerName;
    private Long serviceId;
    private String serviceName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private LocalDateTime usedAt;
}
