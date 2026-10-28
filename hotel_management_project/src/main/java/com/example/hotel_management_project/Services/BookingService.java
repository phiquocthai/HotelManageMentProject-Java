package com.example.hotel_management_project.Services;

import com.example.hotel_management_project.DTO.Request.BookingRequest;
import com.example.hotel_management_project.DTO.Response.BookingResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
    BookingResponse updateBooking(Long id, BookingRequest request);
    PageResponse<BookingResponse> getAllBookings(Pageable pageable);
    PageResponse<BookingResponse> searchBookings(String keyword, String status, Pageable pageable);
    BookingResponse getBookingById(Long id);
    BookingResponse checkIn(Long id);
    BookingResponse checkOut(Long id);
    BookingResponse cancelBooking(Long id);
    void deleteBooking(Long id);
}
