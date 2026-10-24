package com.example.hotel_management_project.Services.Implements;

import com.example.hotel_management_project.Converter.CustomerConverter;
import com.example.hotel_management_project.DTO.Request.CustomerRequest;
import com.example.hotel_management_project.DTO.Response.CustomerResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.Models.Entity.Customer;
import com.example.hotel_management_project.Repository.CustomerRepo;
import com.example.hotel_management_project.Services.CustomerService;
import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.Models.Enums.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepo customerRepo;
    private final CustomerConverter customerConverter;

    public CustomerServiceImpl(CustomerRepo customerRepo, CustomerConverter customerConverter) {
        this.customerRepo = customerRepo;
        this.customerConverter = customerConverter;
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepo.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.CUSTOMER_EXISTED);
        }

        Customer customer = customerConverter.toCustomerEntity(request);
        customer = customerRepo.save(customer);
        return customerConverter.toCustomerResponse(customer);
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer existingCustomer = customerRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CUSTOMER_NOT_FOUND));

        if (!existingCustomer.getEmail().equals(request.getEmail()) && customerRepo.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.CUSTOMER_EXISTED);
        }

        customerConverter.updateEntity(request, existingCustomer);
        existingCustomer = customerRepo.save(existingCustomer);
        return customerConverter.toCustomerResponse(existingCustomer);
    }

    @Override
    public PageResponse<CustomerResponse> getCustomer(Pageable pageable, CustomerRequest customerRequest) {
        String fullName = (customerRequest != null && customerRequest.getFullName() != null && !customerRequest.getFullName().trim().isEmpty())
                ? customerRequest.getFullName().trim() : null;
        String citizenId = (customerRequest != null && customerRequest.getCitizenId() != null && !customerRequest.getCitizenId().trim().isEmpty())
                ? customerRequest.getCitizenId().trim() : null;
        String email = (customerRequest != null && customerRequest.getEmail() != null && !customerRequest.getEmail().trim().isEmpty())
                ? customerRequest.getEmail().trim() : null;
        String phoneNumber = (customerRequest != null && customerRequest.getPhoneNumber() != null && !customerRequest.getPhoneNumber().trim().isEmpty())
                ? customerRequest.getPhoneNumber().trim() : null;

        Page<Customer> page = customerRepo.searchCustomers(fullName, citizenId, email, phoneNumber, pageable);

        List<CustomerResponse> content = page.getContent().stream()
                .map(customerConverter::toCustomerResponse)
                .collect(Collectors.toList());

        return PageResponse.<CustomerResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    public PageResponse<CustomerResponse> getAllCustomers(Pageable pageable) {
        Page<Customer> page = customerRepo.findByActiveTrue(pageable);
        List<CustomerResponse> content = page.getContent().stream()
                .map(customerConverter::toCustomerResponse)
                .collect(Collectors.toList());

        return PageResponse.<CustomerResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CUSTOMER_NOT_FOUND));
        customer.setActive(false);
        customerRepo.save(customer);
    }
}
