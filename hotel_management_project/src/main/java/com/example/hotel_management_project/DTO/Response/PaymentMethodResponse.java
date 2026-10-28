package com.example.hotel_management_project.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodResponse {
    private Long id;
    private String name;
    private String qrImage;
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private Boolean active;
    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;
}
