package com.lotusstay.javafx.model;

import java.math.BigDecimal;

public class RoomType {
    public Long id;
    public String name;
    public BigDecimal basePrice;
    public Integer maxGuests;

    @Override public String toString() { return name; }
}
