package com.example.hotel_management_project.DTO.Response;

import com.example.hotel_management_project.Models.Enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionResponse {
    private Long id;
    private String code;
    private String name;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;
}
