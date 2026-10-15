package com.example.hotel_management_project.Controller;

import com.example.hotel_management_project.DTO.Request.StaffRequest;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.StaffResponse;
import com.example.hotel_management_project.Services.StaffService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createStaff(@RequestBody @Valid StaffRequest request) {
        StaffResponse response = staffService.createStaff(request);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Thêm nhân viên thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateStaff(@PathVariable Long id, @RequestBody @Valid StaffRequest request) {
        StaffResponse response = staffService.updateStaff(id, request);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Cập nhật nhân viên thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getStaffById(@PathVariable Long id) {
        StaffResponse response = staffService.getStaffById(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public PageResponse<StaffResponse> getAllStaffs(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                    @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                    @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                    @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        return staffService.getAllStaffs(pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Xóa nhân viên thành công");
        return ResponseEntity.ok(apiResponse);
    }
}
