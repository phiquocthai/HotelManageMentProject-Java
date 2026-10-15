package com.lotusstay.javafx.model;

import java.math.BigDecimal;

public class ServiceItem {
    public Long id;
    public String name;
    public BigDecimal price;
    public boolean active = true;

    @Override public String toString() { return name; }
}
