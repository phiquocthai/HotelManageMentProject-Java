package com.example.hotel_management_project.Services;

import com.example.hotel_management_project.DTO.Request.ShiftAssignmentRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.ShiftAssignmentResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ShiftAssignmentService {
    ShiftAssignmentResponse createShiftAssignment(ShiftAssignmentRequest request);
    ShiftAssignmentResponse updateShiftAssignment(Long id, ShiftAssignmentRequest request);
    PageResponse<ShiftAssignmentResponse> getAllShiftAssignments(Pageable pageable);
    PageResponse<ShiftAssignmentResponse> searchShiftAssignments(String staffName, Long workShiftId, LocalDate workDate, Pageable pageable);
    List<ShiftAssignmentResponse> getAllShiftAssignments();
    void deleteShiftAssignment(Long id);
}
