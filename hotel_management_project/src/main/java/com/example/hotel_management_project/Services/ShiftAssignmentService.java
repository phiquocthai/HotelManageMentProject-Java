package com.example.hotel_management_project.Services;

import com.example.hotel_management_project.DTO.Request.ShiftAssignmentRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.ShiftAssignmentResponse;
import org.springframework.data.domain.Pageable;

public interface ShiftAssignmentService {
    PageResponse<ShiftAssignmentResponse> getAllShiftAssignments(Pageable pageable);
    ShiftAssignmentResponse createShiftAssignment(ShiftAssignmentRequest shiftAssignmentRequest);
    ShiftAssignmentResponse updateShiftAssignment(Long id, ShiftAssignmentRequest shiftAssignmentRequest);
    void deleteShiftAssignmentById(Long id);
}