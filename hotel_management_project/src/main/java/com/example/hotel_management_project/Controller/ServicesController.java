package com.example.hotel_management_project.Controller;

import com.example.hotel_management_project.DTO.Request.ServiceRequest;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.ServiceResponse;
import com.example.hotel_management_project.Services.ServicesService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
public class ServicesController {

    private final ServicesService serviceService;

    public ServicesController(ServicesService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createService(@RequestBody @Valid ServiceRequest request) {
        ServiceResponse serviceResponse = serviceService.createService(request);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Thêm dịch vụ thành công");
        apiResponse.setResult(serviceResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateService(@PathVariable Long id, @RequestBody @Valid ServiceRequest request) {
        ServiceResponse response = serviceService.updateService(id, request);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Cập nhật dịch vụ thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getServiceById(@PathVariable Long id) {
        ServiceResponse response = serviceService.getServiceById(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public PageResponse<ServiceResponse> getAllServices(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                        @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                        @RequestParam(required = false, defaultValue = "ASC") String sortDir,
                                                        @RequestParam(required = false, defaultValue = "id") String sortBy) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        return serviceService.getAllServices(pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Xóa dịch vụ thành công");
        return ResponseEntity.ok(apiResponse);
    }
}
