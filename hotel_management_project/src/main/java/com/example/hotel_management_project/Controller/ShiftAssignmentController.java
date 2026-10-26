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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/shift-assignment")
public class ShiftAssignmentController {

    private final ShiftAssignmentService shiftAssignmentService;

    public ShiftAssignmentController(ShiftAssignmentService shiftAssignmentService) {
        this.shiftAssignmentService = shiftAssignmentService;
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

    @PostMapping
    public ResponseEntity<ApiResponse> createShiftAssignment(@RequestBody @Valid ShiftAssignmentRequest shiftAssignmentRequest) {
        Map<String, Object> response = new HashMap<>();
        response.put("Phân ca:", shiftAssignmentService.createShiftAssignment(shiftAssignmentRequest));
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Phân ca làm việc thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateShiftAssignment(
            @PathVariable Long id,
            @RequestBody @Valid ShiftAssignmentRequest shiftAssignmentRequest) {
        ShiftAssignmentResponse response = shiftAssignmentService.updateShiftAssignment(id, shiftAssignmentRequest);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("Phân ca:", response);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Cập nhật phân ca thành công");
        apiResponse.setResult(responseMap);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteShiftAssignment(@PathVariable Long id) {
        shiftAssignmentService.deleteShiftAssignmentById(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Xóa phân ca thành công");
        return ResponseEntity.ok(apiResponse);
    }
}