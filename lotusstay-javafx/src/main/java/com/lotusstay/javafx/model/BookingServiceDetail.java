package com.lotusstay.javafx.model;

import java.math.BigDecimal;

public class BookingServiceDetail {
    public Long id;
    public Long bookingId;
    public String roomNumber;
    public String customerName;
    public Long serviceId;
    public String serviceName;
    public String serviceCategory;
    public Integer quantity;
    public BigDecimal unitPrice;
    public BigDecimal totalPrice;
    public String usedAt;

    public BookingServiceDetail() {
    }

    public BookingServiceDetail(Long id, Long bookingId, String roomNumber, String customerName,
                                Long serviceId, String serviceName, String serviceCategory,
                                Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice, String usedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.roomNumber = roomNumber;
        this.customerName = customerName;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceCategory = serviceCategory;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.usedAt = usedAt;
    }
}
