package com.lotusstay.javafx.model;

import java.math.BigDecimal;

public class Invoice {
    public Long id;
    public String code;          // HD001, HD002...
    public Booking booking;      // ManyToOne
    public Customer customer;    // ManyToOne
    public Room room;            // ManyToOne
    public BigDecimal roomFee = BigDecimal.ZERO;
    public BigDecimal serviceFee = BigDecimal.ZERO;
    public BigDecimal penaltyFee = BigDecimal.ZERO;
    public BigDecimal discountFee = BigDecimal.ZERO;
    public BigDecimal total = BigDecimal.ZERO;
    public String paymentStatus = "DA_THANH_TOAN"; // CHUA_THANH_TOAN, THANH_TOAN_MOT_PHAN, DA_THANH_TOAN
    public String issuedDate;

    public String paymentStatusLabel() {
        if ("DA_THANH_TOAN".equalsIgnoreCase(paymentStatus)) return "Đã thanh toán";
        if ("THANH_TOAN_MOT_PHAN".equalsIgnoreCase(paymentStatus)) return "Thanh toán một phần";
        return "Chưa thanh toán";
    }

    public String badgeClass() {
        if ("DA_THANH_TOAN".equalsIgnoreCase(paymentStatus)) return "badge-trong";
        if ("THANH_TOAN_MOT_PHAN".equalsIgnoreCase(paymentStatus)) return "badge-dang_o";
        return "badge-dang_don";
    }
}
