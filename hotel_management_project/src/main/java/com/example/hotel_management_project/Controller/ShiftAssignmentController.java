package com.example.hotel_management_project.Controller;

import com.example.hotel_management_project.DTO.Request.ShiftAssignmentRequest;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.ShiftAssignmentResponse;
import com.example.hotel_management_project.Services.ShiftAssignmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/shift-assignments")
public class ShiftAssignmentController {

    private final ShiftAssignmentService shiftAssignmentService;

    public ShiftAssignmentController(ShiftAssignmentService shiftAssignmentService) {
        this.shiftAssignmentService = shiftAssignmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createShiftAssignment(@RequestBody @Valid ShiftAssignmentRequest request) {
        ShiftAssignmentResponse response = shiftAssignmentService.createShiftAssignment(request);
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> result = new HashMap<>();
        result.put("shiftAssignment", response);

        apiResponse.setMessage("Phân ca làm việc thành công");
        apiResponse.setResult(result);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateShiftAssignment(@PathVariable Long id, @RequestBody @Valid ShiftAssignmentRequest request) {
        ShiftAssignmentResponse response = shiftAssignmentService.updateShiftAssignment(id, request);
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> result = new HashMap<>();
        result.put("shiftAssignment", response);

        apiResponse.setMessage("Cập nhật phân ca làm việc thành công");
        apiResponse.setResult(result);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/search")
    public PageResponse<ShiftAssignmentResponse> searchShiftAssignment(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String staffName,
            @RequestParam(required = false) Long workShiftId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        return shiftAssignmentService.searchShiftAssignments(staffName, workShiftId, workDate, pageable);
    }

    @GetMapping
    public PageResponse<ShiftAssignmentResponse> getAllShiftAssignments(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        return shiftAssignmentService.getAllShiftAssignments(pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteShiftAssignment(@PathVariable Long id) {
        shiftAssignmentService.deleteShiftAssignment(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Xóa phân ca làm việc thành công");
        return ResponseEntity.ok(apiResponse);
    }
}
