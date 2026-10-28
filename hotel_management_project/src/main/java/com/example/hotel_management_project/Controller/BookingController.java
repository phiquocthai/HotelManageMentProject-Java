package com.example.hotel_management_project.Controller;

import com.example.hotel_management_project.DTO.Request.BookingRequest;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import com.example.hotel_management_project.DTO.Response.BookingResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.Services.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/booking", "/api/bookings"})
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createBooking(@RequestBody @Valid BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Tạo đơn đặt phòng thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateBooking(@PathVariable Long id, @RequestBody @Valid BookingRequest request) {
        BookingResponse response = bookingService.updateBooking(id, request);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Cập nhật đơn đặt phòng thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getBookingById(@PathVariable Long id) {
        BookingResponse response = bookingService.getBookingById(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public PageResponse<BookingResponse> getAllBookings(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        if ((keyword != null && !keyword.isBlank()) || (status != null && !status.isBlank())) {
            return bookingService.searchBookings(keyword, status, pageable);
        }
        return bookingService.getAllBookings(pageable);
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<ApiResponse> checkIn(@PathVariable Long id) {
        BookingResponse response = bookingService.checkIn(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Nhận phòng thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<ApiResponse> checkOut(@PathVariable Long id) {
        BookingResponse response = bookingService.checkOut(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Trả phòng thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelBooking(@PathVariable Long id) {
        BookingResponse response = bookingService.cancelBooking(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Hủy đặt phòng thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Xóa đặt phòng thành công");
        return ResponseEntity.ok(apiResponse);
    }
}
