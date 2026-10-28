package com.example.hotel_management_project.Controller;

import com.example.hotel_management_project.DTO.Request.PaymentMethodRequest;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.PaymentMethodResponse;
import com.example.hotel_management_project.Services.PaymentMethodService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createPaymentMethod(@RequestBody @Valid PaymentMethodRequest request) {
        PaymentMethodResponse response = paymentMethodService.createPaymentMethod(request);
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> result = new HashMap<>();
        result.put("paymentMethod", response);

        apiResponse.setMessage("Thêm phương thức thanh toán thành công");
        apiResponse.setResult(result);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePaymentMethod(@PathVariable Long id, @RequestBody @Valid PaymentMethodRequest request) {
        PaymentMethodResponse response = paymentMethodService.updatePaymentMethod(id, request);
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> result = new HashMap<>();
        result.put("paymentMethod", response);

        apiResponse.setMessage("Cập nhật phương thức thanh toán thành công");
        apiResponse.setResult(result);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public PageResponse<PaymentMethodResponse> getAllPaymentMethods(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        return paymentMethodService.getAllPaymentMethods(pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Xóa phương thức thanh toán thành công");
        return ResponseEntity.ok(apiResponse);
    }
}
