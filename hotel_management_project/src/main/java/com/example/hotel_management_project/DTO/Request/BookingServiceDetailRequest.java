package com.example.hotel_management_project.DTO.Request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class BookingServiceDetailRequest {

    @NotNull(message = "Mã đặt phòng không được để trống")
    private Long bookingId;

    @NotNull(message = "Mã dịch vụ không được để trống")
    private Long serviceId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
    private Integer quantity;

    @NotNull(message = "Đơn giá không được để trống")
    @DecimalMin(value = "0.0", message = "Đơn giá phải lớn hơn hoặc bằng 0")
    private BigDecimal unitPrice;

    private LocalDateTime usedAt;
}
