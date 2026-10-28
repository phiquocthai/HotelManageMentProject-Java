package com.example.hotel_management_project.Services.Implements;

import com.example.hotel_management_project.Converter.PaymentMethodConverter;
import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.DTO.Request.PaymentMethodRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.PaymentMethodResponse;
import com.example.hotel_management_project.Models.Entity.PaymentMethod;
import com.example.hotel_management_project.Models.Enums.ErrorCode;
import com.example.hotel_management_project.Repository.PaymentMethodRepo;
import com.example.hotel_management_project.Services.PaymentMethodService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepo paymentMethodRepo;
    private final PaymentMethodConverter paymentMethodConverter;

    public PaymentMethodServiceImpl(PaymentMethodRepo paymentMethodRepo, PaymentMethodConverter paymentMethodConverter) {
        this.paymentMethodRepo = paymentMethodRepo;
        this.paymentMethodConverter = paymentMethodConverter;
    }

    @Override
    public PaymentMethodResponse createPaymentMethod(PaymentMethodRequest request) {
        if (paymentMethodRepo.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.PAYMENT_METHOD_EXISTED);
        }

        PaymentMethod entity = paymentMethodConverter.toPaymentMethodEntity(request);
        if (entity.getActive() == null) {
            entity.setActive(true);
        }
        entity = paymentMethodRepo.save(entity);
        return paymentMethodConverter.toPaymentMethodResponse(entity);
    }

    @Override
    public PaymentMethodResponse updatePaymentMethod(Long id, PaymentMethodRequest request) {
        PaymentMethod existing = paymentMethodRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_METHOD_NOT_FOUND));

        if (!existing.getName().equalsIgnoreCase(request.getName())
                && paymentMethodRepo.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.PAYMENT_METHOD_EXISTED);
        }

        paymentMethodConverter.updateEntity(request, existing);
        existing = paymentMethodRepo.save(existing);
        return paymentMethodConverter.toPaymentMethodResponse(existing);
    }

    @Override
    public PageResponse<PaymentMethodResponse> getAllPaymentMethods(Pageable pageable) {
        // Nếu entity có trường active thì lấy các bản ghi có active = true, ngược lại findAll
        Page<PaymentMethod> page = paymentMethodRepo.findByActiveTrue(pageable);
        List<PaymentMethodResponse> content = page.getContent().stream()
                .map(paymentMethodConverter::toPaymentMethodResponse)
                .collect(Collectors.toList());

        return PageResponse.<PaymentMethodResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    public List<PaymentMethodResponse> getAllPaymentMethods() {
        return paymentMethodRepo.findByActiveTrue().stream()
                .map(paymentMethodConverter::toPaymentMethodResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePaymentMethod(Long id) {
        PaymentMethod paymentMethod = paymentMethodRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_METHOD_NOT_FOUND));

        // Nếu có trường active thì set active = false (0), ngược lại xóa cứng
        if (paymentMethod.getActive() != null) {
            paymentMethod.setActive(false);
            paymentMethodRepo.save(paymentMethod);
        } else {
            paymentMethodRepo.delete(paymentMethod);
        }
    }
}
