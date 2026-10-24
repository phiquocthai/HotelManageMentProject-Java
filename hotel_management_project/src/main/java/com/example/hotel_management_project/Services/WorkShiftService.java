package com.example.hotel_management_project.Services;

import com.example.hotel_management_project.DTO.Request.WorkShiftRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.WorkShiftResponse;
import org.springframework.data.domain.Pageable;

public interface WorkShiftService {
    PageResponse<WorkShiftResponse> getAllWorkShifts(Pageable pageable);
    WorkShiftResponse createWorkShift(WorkShiftRequest workShiftRequest);
    WorkShiftResponse updateWorkShift(Long id, WorkShiftRequest workShiftRequest);
    void deleteWorkShiftById(Long id);

}
