package com.example.hotel_management_project.Controller;

import com.example.hotel_management_project.DTO.Request.AttendanceRequest;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import com.example.hotel_management_project.DTO.Response.AttendanceResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.Services.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hotel_management_project.Models.Enums.AttendanceStatus;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createAttendance(@RequestBody @Valid AttendanceRequest request) {
        AttendanceResponse response = attendanceService.createAttendance(request);
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> result = new HashMap<>();
        result.put("attendance", response);

        apiResponse.setMessage("Chấm công thành công");
        apiResponse.setResult(result);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateAttendance(@PathVariable Long id, @RequestBody @Valid AttendanceRequest request) {
        AttendanceResponse response = attendanceService.updateAttendance(id, request);
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> result = new HashMap<>();
        result.put("attendance", response);

        apiResponse.setMessage("Cập nhật bản ghi chấm công thành công");
        apiResponse.setResult(result);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/search")
    public PageResponse<AttendanceResponse> searchAttendances(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String staffName,
            @RequestParam(required = false) Long workShiftId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
            @RequestParam(required = false) String status) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        AttendanceStatus attendanceStatus = parseAttendanceStatus(status);
        return attendanceService.searchAttendances(staffName, workShiftId, workDate, attendanceStatus, pageable);
    }

    @GetMapping
    public PageResponse<AttendanceResponse> getAllAttendances(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        return attendanceService.getAllAttendances(pageable);
    }

    private AttendanceStatus parseAttendanceStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        try {
            return AttendanceStatus.fromLabel(status);
        } catch (Exception ignored) {
            try {
                return AttendanceStatus.valueOf(status.trim().toUpperCase());
            } catch (Exception ignored2) {
                return null;
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Xóa bản ghi chấm công thành công");
        return ResponseEntity.ok(apiResponse);
    }
}
