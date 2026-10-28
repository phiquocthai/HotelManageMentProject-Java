package com.example.hotel_management_project.Converter;

import com.example.hotel_management_project.DTO.Request.PaymentMethodRequest;
import com.example.hotel_management_project.DTO.Response.PaymentMethodResponse;
import com.example.hotel_management_project.Models.Entity.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodConverter {

    public PaymentMethod toPaymentMethodEntity(PaymentMethodRequest request) {
        return PaymentMethod.builder()
                .name(request.getName())
                .qrImage(request.getQrImage())
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .accountHolderName(request.getAccountHolderName())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    public PaymentMethodResponse toPaymentMethodResponse(PaymentMethod entity) {
        PaymentMethodResponse response = PaymentMethodResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .qrImage(entity.getQrImage())
                .bankName(entity.getBankName())
                .accountNumber(entity.getAccountNumber())
                .accountHolderName(entity.getAccountHolderName())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();

        if (entity.getCreatedBy() != null) {
            response.setCreatedBy(entity.getCreatedBy().getFullName());
        }
        if (entity.getModifiedBy() != null) {
            response.setModifiedBy(entity.getModifiedBy().getFullName());
        }

        return response;
    }

    public void updateEntity(PaymentMethodRequest request, PaymentMethod entity) {
        entity.setName(request.getName());
        entity.setQrImage(request.getQrImage());
        entity.setBankName(request.getBankName());
        entity.setAccountNumber(request.getAccountNumber());
        entity.setAccountHolderName(request.getAccountHolderName());
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }
}
