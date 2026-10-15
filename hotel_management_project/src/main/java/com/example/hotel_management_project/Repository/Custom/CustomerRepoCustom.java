package com.example.hotel_management_project.Repository.Custom;

import com.example.hotel_management_project.DTO.Request.CustomerRequest;
import com.example.hotel_management_project.Models.Entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerRepoCustom {

    Page<Customer> findCustomer(Pageable pageable, CustomerRequest customerRequest);

}
