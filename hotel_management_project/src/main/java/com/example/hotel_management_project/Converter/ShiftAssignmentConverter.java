package com.example.hotel_management_project.Converter;

import com.example.hotel_management_project.DTO.Request.ShiftAssignmentRequest;
import com.example.hotel_management_project.DTO.Response.ShiftAssignmentResponse;
import com.example.hotel_management_project.Models.Entity.ShiftAssignment;
import com.example.hotel_management_project.Models.Entity.Staff;
import com.example.hotel_management_project.Models.Entity.WorkShift;
import org.springframework.stereotype.Component;

@Component
public class ShiftAssignmentConverter {

    public ShiftAssignment toShiftAssignmentEntity(ShiftAssignmentRequest request, Staff staff, WorkShift workShift) {
        return ShiftAssignment.builder()
                .staff(staff)
                .workShift(workShift)
                .workDate(request.getWorkDate())
                .build();
    }

    public ShiftAssignmentResponse toShiftAssignmentResponse(ShiftAssignment entity) {
        ShiftAssignmentResponse response = ShiftAssignmentResponse.builder()
                .id(entity.getId())
                .workDate(entity.getWorkDate())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();

        if (entity.getStaff() != null) {
            response.setStaffId(entity.getStaff().getId());
            response.setStaffName(entity.getStaff().getFullName());
        }

        if (entity.getWorkShift() != null) {
            WorkShift ws = entity.getWorkShift();
            response.setWorkShiftId(ws.getId());
            response.setShiftName(ws.getShiftName());
            response.setStartTime(ws.getStartTime());
            response.setEndTime(ws.getEndTime());
        }

        if (entity.getCreatedBy() != null) {
            response.setCreatedBy(entity.getCreatedBy().getFullName());
        }
        if (entity.getModifiedBy() != null) {
            response.setModifiedBy(entity.getModifiedBy().getFullName());
        }

        return response;
    }

    public void updateEntity(ShiftAssignmentRequest request, ShiftAssignment entity, Staff staff, WorkShift workShift) {
        if (staff != null) {
            entity.setStaff(staff);
        }
        if (workShift != null) {
            entity.setWorkShift(workShift);
        }
        if (request.getWorkDate() != null) {
            entity.setWorkDate(request.getWorkDate());
        }
    }
}
