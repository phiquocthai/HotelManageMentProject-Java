package com.example.hotel_management_project.Services;

import com.example.hotel_management_project.DTO.Request.StaffRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.StaffResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface StaffService {
    StaffResponse createStaff(StaffRequest request);
    StaffResponse updateStaff(Long id, StaffRequest request);
    StaffResponse getStaffById(Long id);
    PageResponse<StaffResponse> getAllStaffs(Pageable pageable);
    void deleteStaff(Long id);
}
