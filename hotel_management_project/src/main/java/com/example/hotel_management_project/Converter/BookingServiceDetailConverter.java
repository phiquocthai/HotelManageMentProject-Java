package com.example.hotel_management_project.Converter;

import com.example.hotel_management_project.DTO.Request.BookingServiceDetailRequest;
import com.example.hotel_management_project.DTO.Response.BookingServiceDetailResponse;
import com.example.hotel_management_project.Models.Entity.Booking;
import com.example.hotel_management_project.Models.Entity.BookingServiceDetail;
import com.example.hotel_management_project.Models.Entity.Service;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class BookingServiceDetailConverter {

    public BookingServiceDetail toEntity(BookingServiceDetailRequest request, Booking booking, Service service) {
        return BookingServiceDetail.builder()
                .booking(booking)
                .service(service)
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice() != null ? request.getUnitPrice() : service.getPrice())
                .usedAt(request.getUsedAt() != null ? request.getUsedAt() : LocalDateTime.now())
                .build();
    }

    public BookingServiceDetailResponse toResponse(BookingServiceDetail entity) {
        BigDecimal unitPrice = entity.getUnitPrice() != null ? entity.getUnitPrice() : BigDecimal.ZERO;
        int qty = entity.getQuantity() != null ? entity.getQuantity() : 0;
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(qty));

        BookingServiceDetailResponse response = BookingServiceDetailResponse.builder()
                .id(entity.getId())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .totalPrice(total)
                .usedAt(entity.getUsedAt())
                .build();

        if (entity.getBooking() != null) {
            response.setBookingId(entity.getBooking().getId());
            if (entity.getBooking().getRoom() != null) {
                response.setRoomNumber(entity.getBooking().getRoom().getRoomNumber());
            }
            if (entity.getBooking().getCustomer() != null) {
                response.setCustomerName(entity.getBooking().getCustomer().getFullName());
            }
        }

        if (entity.getService() != null) {
            response.setServiceId(entity.getService().getId());
            response.setServiceName(entity.getService().getName());
        }

        return response;
    }

    public void updateEntity(BookingServiceDetailRequest request, BookingServiceDetail entity, Booking booking, Service service) {
        if (booking != null) {
            entity.setBooking(booking);
        }
        if (service != null) {
            entity.setService(service);
        }
        if (request.getQuantity() != null) {
            entity.setQuantity(request.getQuantity());
        }
        if (request.getUnitPrice() != null) {
            entity.setUnitPrice(request.getUnitPrice());
        }
        if (request.getUsedAt() != null) {
            entity.setUsedAt(request.getUsedAt());
        }
    }
}
