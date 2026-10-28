package com.example.hotel_management_project.Services;

import com.example.hotel_management_project.DTO.Request.AttendanceRequest;
import com.example.hotel_management_project.DTO.Response.AttendanceResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.Models.Enums.AttendanceStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceResponse createAttendance(AttendanceRequest request);
    AttendanceResponse updateAttendance(Long id, AttendanceRequest request);
    PageResponse<AttendanceResponse> getAllAttendances(Pageable pageable);
    PageResponse<AttendanceResponse> searchAttendances(
            String staffName, Long workShiftId, LocalDate workDate, AttendanceStatus status, Pageable pageable);
    List<AttendanceResponse> getAllAttendances();
    void deleteAttendance(Long id);
}
