package com.example.hotel_management_project.Controller;

import com.example.hotel_management_project.DTO.Request.BookingServiceDetailRequest;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import com.example.hotel_management_project.DTO.Response.BookingServiceDetailResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.Services.BookingServiceDetailService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/booking-service-details", "/api/booking-service-detail", "/api/booking-services"})
public class BookingServiceDetailController {

    private final BookingServiceDetailService bookingServiceDetailService;

    public BookingServiceDetailController(BookingServiceDetailService bookingServiceDetailService) {
        this.bookingServiceDetailService = bookingServiceDetailService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createDetail(@RequestBody @Valid BookingServiceDetailRequest request) {
        BookingServiceDetailResponse response = bookingServiceDetailService.createDetail(request);
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> result = new HashMap<>();
        result.put("detail", response);
        apiResponse.setMessage("Thêm dịch vụ phòng thành công");
        apiResponse.setResult(result);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateDetail(@PathVariable Long id, @RequestBody @Valid BookingServiceDetailRequest request) {
        BookingServiceDetailResponse response = bookingServiceDetailService.updateDetail(id, request);
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> result = new HashMap<>();
        result.put("detail", response);
        apiResponse.setMessage("Cập nhật dịch vụ phòng thành công");
        apiResponse.setResult(result);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getDetailById(@PathVariable Long id) {
        BookingServiceDetailResponse response = bookingServiceDetailService.getDetailById(id);
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> result = new HashMap<>();
        result.put("detail", response);
        apiResponse.setResult(result);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse> getDetailsByBookingId(@PathVariable Long bookingId) {
        List<BookingServiceDetailResponse> response = bookingServiceDetailService.getDetailsByBookingId(bookingId);
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> result = new HashMap<>();
        result.put("details", response);
        apiResponse.setResult(result);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public PageResponse<BookingServiceDetailResponse> getAllDetails(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long bookingId) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        return bookingServiceDetailService.getAllDetails(bookingId, pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDetail(@PathVariable Long id) {
        bookingServiceDetailService.deleteDetail(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Xóa dịch vụ phòng thành công");
        return ResponseEntity.ok(apiResponse);
    }
}
