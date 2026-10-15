package com.lotusstay.javafx.model;

import java.math.BigDecimal;

public class Invoice {
    public Long id;
    public Booking booking;
    public Customer customer;
    public Room room;
    public BigDecimal roomFee;
    public BigDecimal serviceFee;
    public BigDecimal penaltyFee;
    public BigDecimal total;
    public String issuedDate;
}
