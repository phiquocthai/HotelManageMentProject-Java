package com.example.hotel_management_project.Services.Implements;

import com.example.hotel_management_project.Converter.BookingServiceDetailConverter;
import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.DTO.Request.BookingServiceDetailRequest;
import com.example.hotel_management_project.DTO.Response.BookingServiceDetailResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.Models.Entity.Booking;
import com.example.hotel_management_project.Models.Entity.BookingServiceDetail;
import com.example.hotel_management_project.Models.Entity.Service;
import com.example.hotel_management_project.Models.Enums.ErrorCode;
import com.example.hotel_management_project.Repository.BookingRepo;
import com.example.hotel_management_project.Repository.BookingServiceDetailRepo;
import com.example.hotel_management_project.Repository.ServiceRepo;
import com.example.hotel_management_project.Services.BookingServiceDetailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class BookingServiceDetailServiceImpl implements BookingServiceDetailService {

    private final BookingServiceDetailRepo bookingServiceDetailRepo;
    private final BookingRepo bookingRepo;
    private final ServiceRepo serviceRepo;
    private final BookingServiceDetailConverter bookingServiceDetailConverter;

    public BookingServiceDetailServiceImpl(BookingServiceDetailRepo bookingServiceDetailRepo,
                                          BookingRepo bookingRepo,
                                          ServiceRepo serviceRepo,
                                          BookingServiceDetailConverter bookingServiceDetailConverter) {
        this.bookingServiceDetailRepo = bookingServiceDetailRepo;
        this.bookingRepo = bookingRepo;
        this.serviceRepo = serviceRepo;
        this.bookingServiceDetailConverter = bookingServiceDetailConverter;
    }

    @Override
    @Transactional
    public BookingServiceDetailResponse createDetail(BookingServiceDetailRequest request) {
        Booking booking = bookingRepo.findById(request.getBookingId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND));

        Service service = serviceRepo.findById(request.getServiceId())
                .orElseThrow(() -> new CustomException(ErrorCode.SERVICE_NOT_FOUND));

        if (request.getUnitPrice() == null) {
            request.setUnitPrice(service.getPrice());
        }

        BookingServiceDetail detail = bookingServiceDetailConverter.toEntity(request, booking, service);
        detail = bookingServiceDetailRepo.save(detail);

        return bookingServiceDetailConverter.toResponse(detail);
    }

    @Override
    @Transactional
    public BookingServiceDetailResponse updateDetail(Long id, BookingServiceDetailRequest request) {
        BookingServiceDetail existing = bookingServiceDetailRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_SERVICE_DETAIL_NOT_FOUND));

        Booking booking = existing.getBooking();
        if (request.getBookingId() != null && !existing.getBooking().getId().equals(request.getBookingId())) {
            booking = bookingRepo.findById(request.getBookingId())
                    .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND));
        }

        Service service = existing.getService();
        if (request.getServiceId() != null && !existing.getService().getId().equals(request.getServiceId())) {
            service = serviceRepo.findById(request.getServiceId())
                    .orElseThrow(() -> new CustomException(ErrorCode.SERVICE_NOT_FOUND));
        }

        bookingServiceDetailConverter.updateEntity(request, existing, booking, service);
        existing = bookingServiceDetailRepo.save(existing);

        return bookingServiceDetailConverter.toResponse(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingServiceDetailResponse getDetailById(Long id) {
        BookingServiceDetail existing = bookingServiceDetailRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_SERVICE_DETAIL_NOT_FOUND));
        return bookingServiceDetailConverter.toResponse(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingServiceDetailResponse> getDetailsByBookingId(Long bookingId) {
        return bookingServiceDetailRepo.findByBookingId(bookingId).stream()
                .map(bookingServiceDetailConverter::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingServiceDetailResponse> getAllDetails(Long bookingId, Pageable pageable) {
        Page<BookingServiceDetail> page = (bookingId != null)
                ? bookingServiceDetailRepo.findByBookingId(bookingId, pageable)
                : bookingServiceDetailRepo.findAll(pageable);
        List<BookingServiceDetailResponse> content = page.getContent().stream()
                .map(bookingServiceDetailConverter::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<BookingServiceDetailResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    @Transactional
    public void deleteDetail(Long id) {
        BookingServiceDetail existing = bookingServiceDetailRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_SERVICE_DETAIL_NOT_FOUND));
        bookingServiceDetailRepo.delete(existing);
    }
}
