package com.example.hotel_management_project.Services;

import com.example.hotel_management_project.DTO.Request.PaymentMethodRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.PaymentMethodResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentMethodService {
    PaymentMethodResponse createPaymentMethod(PaymentMethodRequest request);
    PaymentMethodResponse updatePaymentMethod(Long id, PaymentMethodRequest request);
    PageResponse<PaymentMethodResponse> getAllPaymentMethods(Pageable pageable);
    List<PaymentMethodResponse> getAllPaymentMethods();
    void deletePaymentMethod(Long id);
}
