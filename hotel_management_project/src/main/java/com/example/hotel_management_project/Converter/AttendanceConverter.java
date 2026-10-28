package com.example.hotel_management_project.Converter;

import com.example.hotel_management_project.DTO.Request.AttendanceRequest;
import com.example.hotel_management_project.DTO.Response.AttendanceResponse;
import com.example.hotel_management_project.Models.Entity.Attendance;
import com.example.hotel_management_project.Models.Entity.ShiftAssignment;
import com.example.hotel_management_project.Models.Enums.AttendanceStatus;
import org.springframework.stereotype.Component;

@Component
public class AttendanceConverter {

    public Attendance toAttendanceEntity(AttendanceRequest request, ShiftAssignment shiftAssignment) {
        return Attendance.builder()
                .shiftAssignment(shiftAssignment)
                .checkInTime(request.getCheckInTime())
                .checkOutTime(request.getCheckOutTime())
                .status(AttendanceStatus.fromLabel(request.getStatus()))
                .note(request.getNote())
                .build();
    }

    public AttendanceResponse toAttendanceResponse(Attendance entity) {
        AttendanceResponse response = AttendanceResponse.builder()
                .id(entity.getId())
                .checkInTime(entity.getCheckInTime())
                .checkOutTime(entity.getCheckOutTime())
                .status(entity.getStatus() != null ? entity.getStatus().getLabel() : null)
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();

        if (entity.getShiftAssignment() != null) {
            ShiftAssignment sa = entity.getShiftAssignment();
            response.setShiftAssignmentId(sa.getId());
            response.setWorkDate(sa.getWorkDate());
            if (sa.getStaff() != null) {
                response.setStaffName(sa.getStaff().getFullName());
            }
            if (sa.getWorkShift() != null) {
                response.setWorkShiftName(sa.getWorkShift().getShiftName());
                response.setShiftName(sa.getWorkShift().getShiftName());
            }
        }

        if (entity.getCreatedBy() != null) {
            response.setCreatedBy(entity.getCreatedBy().getFullName());
        }
        if (entity.getModifiedBy() != null) {
            response.setModifiedBy(entity.getModifiedBy().getFullName());
        }

        return response;
    }

    public void updateEntity(AttendanceRequest request, Attendance entity, ShiftAssignment shiftAssignment) {
        if (shiftAssignment != null) {
            entity.setShiftAssignment(shiftAssignment);
        }
        entity.setCheckInTime(request.getCheckInTime());
        entity.setCheckOutTime(request.getCheckOutTime());
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            entity.setStatus(AttendanceStatus.fromLabel(request.getStatus()));
        }
        entity.setNote(request.getNote());
    }
}
