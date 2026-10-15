package com.example.hotel_management_project.Converter;

import com.example.hotel_management_project.DTO.Request.CustomerRequest;
import com.example.hotel_management_project.DTO.Response.CustomerResponse;
import com.example.hotel_management_project.Models.Entity.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerConverter {

    private final ModelMapper modelMapper;

    public CustomerConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Customer toCustomerEntity(CustomerRequest customerRequest) {
        Customer customerEntity = modelMapper.map(customerRequest, Customer.class);
        return customerEntity;
    }

    public CustomerResponse toCustomerResponse(Customer customerEntity) {
        CustomerResponse customerResponse = modelMapper.map(customerEntity, CustomerResponse.class);
        if (customerEntity.getCreatedBy() != null) {
            customerResponse.setCreatedBy(customerEntity.getCreatedBy().getFullName());
        }
        if (customerEntity.getModifiedBy() != null) {
            customerResponse.setModifiedBy(customerEntity.getModifiedBy().getFullName());
        }
        return customerResponse;
    }

    public void updateEntity(CustomerRequest request, Customer entity) {
        entity.setFullName(request.getFullName());
        entity.setCitizenId(request.getCitizenId());
        entity.setEmail(request.getEmail());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setImagePath(request.getImagePath());
    }
}
