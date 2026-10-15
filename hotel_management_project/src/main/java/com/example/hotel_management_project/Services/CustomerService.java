package com.example.hotel_management_project.Services;

import com.example.hotel_management_project.DTO.Request.CustomerRequest;
import com.example.hotel_management_project.DTO.Response.CustomerResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    CustomerResponse updateCustomer(Long id, CustomerRequest request);

    PageResponse<CustomerResponse> getCustomer(Pageable pageable, CustomerRequest request);
    PageResponse<CustomerResponse> getAllCustomers(Pageable pageable);
    void deleteCustomer(Long id);
}
