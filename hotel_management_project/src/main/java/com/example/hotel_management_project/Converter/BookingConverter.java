package com.example.hotel_management_project.Converter;

import com.example.hotel_management_project.DTO.Request.BookingRequest;
import com.example.hotel_management_project.DTO.Response.BookingResponse;
import com.example.hotel_management_project.Models.Entity.Booking;
import com.example.hotel_management_project.Models.Entity.Customer;
import com.example.hotel_management_project.Models.Entity.Room;
import com.example.hotel_management_project.Models.Enums.BookingStatus;
import org.springframework.stereotype.Component;

@Component
public class BookingConverter {

    public Booking toBookingEntity(BookingRequest request, Customer customer, Room room) {
        BookingStatus status = request.getStatus() != null ? request.getStatus() : BookingStatus.DAT_TRUOC;
        return Booking.builder()
                .customer(customer)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .expectedCheckOutDate(request.getExpectedCheckOutDate())
                .actualCheckInTime(request.getActualCheckInTime())
                .actualCheckOutTime(request.getActualCheckOutTime())
                .numberOfGuests(request.getNumberOfGuests() != null ? request.getNumberOfGuests() : 1)
                .note(request.getNote())
                .status(status)
                .build();
    }

    public BookingResponse toBookingResponse(Booking entity) {
        if (entity == null) return null;
        BookingResponse response = new BookingResponse();
        response.setId(entity.getId());
        if (entity.getCustomer() != null) {
            response.setCustomerId(entity.getCustomer().getId());
            response.setCustomerName(entity.getCustomer().getFullName());
            response.setCustomerPhone(entity.getCustomer().getPhoneNumber());
        }
        if (entity.getRoom() != null) {
            response.setRoomId(entity.getRoom().getId());
            response.setRoomNumber(entity.getRoom().getRoomNumber());
            if (entity.getRoom().getRoomType() != null) {
                response.setRoomTypeName(entity.getRoom().getRoomType().getName());
            }
        }
        response.setCheckInDate(entity.getCheckInDate());
        response.setExpectedCheckOutDate(entity.getExpectedCheckOutDate());
        response.setActualCheckInTime(entity.getActualCheckInTime());
        response.setActualCheckOutTime(entity.getActualCheckOutTime());
        response.setNumberOfGuests(entity.getNumberOfGuests());
        response.setNote(entity.getNote());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setModifiedAt(entity.getModifiedAt());
        return response;
    }

    public void updateEntity(BookingRequest request, Booking entity, Customer customer, Room room) {
        if (customer != null) {
            entity.setCustomer(customer);
        }
        if (room != null) {
            entity.setRoom(room);
        }
        if (request.getCheckInDate() != null) {
            entity.setCheckInDate(request.getCheckInDate());
        }
        if (request.getExpectedCheckOutDate() != null) {
            entity.setExpectedCheckOutDate(request.getExpectedCheckOutDate());
        }
        if (request.getActualCheckInTime() != null) {
            entity.setActualCheckInTime(request.getActualCheckInTime());
        }
        if (request.getActualCheckOutTime() != null) {
            entity.setActualCheckOutTime(request.getActualCheckOutTime());
        }
        if (request.getNumberOfGuests() != null) {
            entity.setNumberOfGuests(request.getNumberOfGuests());
        }
        if (request.getNote() != null) {
            entity.setNote(request.getNote());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
    }
}
