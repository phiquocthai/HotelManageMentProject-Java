package com.example.hotel_management_project.Services.Implements;

import com.example.hotel_management_project.Converter.BookingConverter;
import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.DTO.Request.BookingRequest;
import com.example.hotel_management_project.DTO.Response.BookingResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.Models.Entity.Booking;
import com.example.hotel_management_project.Models.Entity.Customer;
import com.example.hotel_management_project.Models.Entity.Room;
import com.example.hotel_management_project.Models.Enums.BookingStatus;
import com.example.hotel_management_project.Models.Enums.ErrorCode;
import com.example.hotel_management_project.Models.Enums.RoomStatus;
import com.example.hotel_management_project.Repository.BookingRepo;
import com.example.hotel_management_project.Repository.CustomerRepo;
import com.example.hotel_management_project.Repository.RoomRepo;
import com.example.hotel_management_project.Services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepo bookingRepo;
    private final CustomerRepo customerRepo;
    private final RoomRepo roomRepo;
    private final BookingConverter bookingConverter;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        Customer customer = customerRepo.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomException(ErrorCode.CUSTOMER_NOT_FOUND));

        Room room = roomRepo.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        if (request.getStatus() == BookingStatus.DANG_O) {
            room.setStatus(RoomStatus.DANG_O);
            if (request.getActualCheckInTime() == null) {
                request.setActualCheckInTime(LocalDateTime.now());
            }
            roomRepo.save(room);
        }

        Booking booking = bookingConverter.toBookingEntity(request, customer, room);
        Booking saved = bookingRepo.save(booking);
        return bookingConverter.toBookingResponse(saved);
    }

    @Override
    @Transactional
    public BookingResponse updateBooking(Long id, BookingRequest request) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND));

        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepo.findById(request.getCustomerId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CUSTOMER_NOT_FOUND));
        }

        Room room = null;
        if (request.getRoomId() != null) {
            room = roomRepo.findById(request.getRoomId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        }

        BookingStatus oldStatus = booking.getStatus();
        BookingStatus newStatus = request.getStatus();

        bookingConverter.updateEntity(request, booking, customer, room);

        if (newStatus != null && newStatus != oldStatus) {
            Room targetRoom = booking.getRoom();
            if (targetRoom != null) {
                if (newStatus == BookingStatus.DANG_O) {
                    targetRoom.setStatus(RoomStatus.DANG_O);
                    if (booking.getActualCheckInTime() == null) {
                        booking.setActualCheckInTime(LocalDateTime.now());
                    }
                    roomRepo.save(targetRoom);
                } else if (newStatus == BookingStatus.DA_TRA) {
                    targetRoom.setStatus(RoomStatus.DANG_DON);
                    if (booking.getActualCheckOutTime() == null) {
                        booking.setActualCheckOutTime(LocalDateTime.now());
                    }
                    roomRepo.save(targetRoom);
                } else if (newStatus == BookingStatus.DA_HUY) {
                    if (targetRoom.getStatus() == RoomStatus.DANG_O) {
                        targetRoom.setStatus(RoomStatus.TRONG);
                        roomRepo.save(targetRoom);
                    }
                }
            }
        }

        Booking saved = bookingRepo.save(booking);
        return bookingConverter.toBookingResponse(saved);
    }

    @Override
    public PageResponse<BookingResponse> getAllBookings(Pageable pageable) {
        Page<Booking> page = bookingRepo.findAllWithDetails(pageable);
        List<BookingResponse> content = page.getContent().stream()
                .map(bookingConverter::toBookingResponse)
                .collect(Collectors.toList());

        return PageResponse.<BookingResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    public PageResponse<BookingResponse> searchBookings(String keyword, String statusStr, Pageable pageable) {
        BookingStatus status = null;
        if (statusStr != null && !statusStr.isBlank() && !"Tất cả trạng thái".equalsIgnoreCase(statusStr.trim())) {
            try {
                status = BookingStatus.valueOf(statusStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                try {
                    status = BookingStatus.fromLabel(statusStr.trim());
                } catch (IllegalArgumentException ignored) {}
            }
        }

        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        Page<Booking> page = bookingRepo.searchBookings(kw, status, pageable);
        List<BookingResponse> content = page.getContent().stream()
                .map(bookingConverter::toBookingResponse)
                .collect(Collectors.toList());

        return PageResponse.<BookingResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND));
        return bookingConverter.toBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse checkIn(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND));

        booking.setStatus(BookingStatus.DANG_O);
        booking.setActualCheckInTime(LocalDateTime.now());

        if (booking.getRoom() != null) {
            booking.getRoom().setStatus(RoomStatus.DANG_O);
            roomRepo.save(booking.getRoom());
        }

        Booking saved = bookingRepo.save(booking);
        return bookingConverter.toBookingResponse(saved);
    }

    @Override
    @Transactional
    public BookingResponse checkOut(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND));

        booking.setStatus(BookingStatus.DA_TRA);
        booking.setActualCheckOutTime(LocalDateTime.now());

        if (booking.getRoom() != null) {
            booking.getRoom().setStatus(RoomStatus.DANG_DON);
            roomRepo.save(booking.getRoom());
        }

        Booking saved = bookingRepo.save(booking);
        return bookingConverter.toBookingResponse(saved);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND));

        booking.setStatus(BookingStatus.DA_HUY);

        if (booking.getRoom() != null && booking.getRoom().getStatus() == RoomStatus.DANG_O) {
            booking.getRoom().setStatus(RoomStatus.TRONG);
            roomRepo.save(booking.getRoom());
        }

        Booking saved = bookingRepo.save(booking);
        return bookingConverter.toBookingResponse(saved);
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND));
        bookingRepo.delete(booking);
    }
}
