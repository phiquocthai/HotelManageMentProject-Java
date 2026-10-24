package com.example.hotel_management_project.Controller;

import com.example.hotel_management_project.DTO.Request.WorkShiftRequest;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.WorkShiftResponse;
import com.example.hotel_management_project.Models.Entity.WorkShift;
import com.example.hotel_management_project.Services.WorkShiftService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/workshift")
public class WorkShiftController {

    // Các phương thức xử lý yêu cầu liên quan đến WorkShift sẽ được định nghĩa ở đây
    private final WorkShiftService workShiftService;

    public WorkShiftController(WorkShiftService workShiftService) {
        this.workShiftService = workShiftService;
    }
    @GetMapping
    public PageResponse<WorkShiftResponse> getAllWorkShifts(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                            @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                            @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        return workShiftService.getAllWorkShifts(pageable);
    }
    @PostMapping
    public ResponseEntity<ApiResponse> createWorkShift(@RequestBody @Valid WorkShiftRequest workShiftRequest) {
        Map<String, Object> response = new HashMap<>();
        response.put("Ca làm việc:", workShiftService.createWorkShift(workShiftRequest));
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Thêm ca làm việc thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateWorkShift(@PathVariable Long id, @RequestBody @Valid WorkShiftRequest workShiftRequest) {
        WorkShiftResponse response = workShiftService.updateWorkShift(id, workShiftRequest);
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("Ca làm việc:", response);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Cập nhật ca làm việc thành công");
        apiResponse.setResult(responseMap);
        return ResponseEntity.ok(apiResponse);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteWorkShift(@PathVariable Long id) {
        workShiftService.deleteWorkShiftById(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Xóa ca làm việc thành công");
        return ResponseEntity.ok(apiResponse);
    }
}
