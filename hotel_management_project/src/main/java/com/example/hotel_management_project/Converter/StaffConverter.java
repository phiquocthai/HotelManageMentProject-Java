package com.example.hotel_management_project.Converter;

import com.example.hotel_management_project.DTO.Request.StaffRequest;
import com.example.hotel_management_project.DTO.Response.StaffResponse;
import com.example.hotel_management_project.Models.Entity.Staff;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class StaffConverter {

    private final ModelMapper modelMapper;

    public StaffConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Staff toStaffEntity(StaffRequest staffRequest) {
        Staff staffEntity = modelMapper.map(staffRequest, Staff.class);
        return staffEntity;
    }

    public StaffResponse toStaffResponse(Staff staffEntity) {
        StaffResponse staffResponse = modelMapper.map(staffEntity, StaffResponse.class);
        if (staffEntity.getCreatedBy() != null) {
            staffResponse.setCreatedBy(staffEntity.getCreatedBy().getFullName());
        }
        if (staffEntity.getModifiedBy() != null) {
            staffResponse.setModifiedBy(staffEntity.getModifiedBy().getFullName());
        }
        return staffResponse;
    }

    public void updateEntity(StaffRequest request, Staff entity) {
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setFullName(request.getFullName());
        entity.setJobTitle(request.getJobTitle());
        entity.setRole(request.getRole());
        entity.setProfileImage(request.getProfileImage());
        
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }
}
