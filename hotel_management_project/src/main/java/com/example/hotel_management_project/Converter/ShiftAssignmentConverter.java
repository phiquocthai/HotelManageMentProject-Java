package com.example.hotel_management_project.Converter;

import com.example.hotel_management_project.DTO.Request.ShiftAssignmentRequest;
import com.example.hotel_management_project.DTO.Response.ShiftAssignmentResponse;
import com.example.hotel_management_project.Models.Entity.ShiftAssignment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ShiftAssignmentConverter {

    private final ModelMapper modelMapper;

    public ShiftAssignmentConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ShiftAssignment toShiftAssignmentEntity(ShiftAssignmentRequest request) {
        return modelMapper.map(request, ShiftAssignment.class);
    }

    public ShiftAssignmentResponse toShiftAssignmentResponse(ShiftAssignment entity) {
        ShiftAssignmentResponse response = modelMapper.map(entity, ShiftAssignmentResponse.class);
        
        // Gán rõ ràng thông tin liên kết để đảm bảo null-safety
        if (entity.getStaff() != null) {
            response.setStaffId(entity.getStaff().getId());
            // response.setStaffName(entity.getStaff().getFullName()); // Mở chú thích nếu entity Staff có getter tên
        }
        if (entity.getWorkShift() != null) {
            response.setWorkShiftId(entity.getWorkShift().getId());
            response.setShiftName(entity.getWorkShift().getShiftName());
        }
        if (entity.getWorkDate() != null) {
            response.setWorkDate(entity.getWorkDate().toString());
        }
        
        return response;
    }
}