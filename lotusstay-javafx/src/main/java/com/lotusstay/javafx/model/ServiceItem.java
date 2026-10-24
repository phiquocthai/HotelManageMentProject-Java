package com.lotusstay.javafx.model;

import java.math.BigDecimal;

public class ServiceItem {
    public Long id;
    public String code;       // DV001, DV002...
    public String name;       // Tên dịch vụ (100 ký tự)
    public BigDecimal price;  // Đơn giá (Decimal 12,0)
    public String imagePath;
    public boolean active = true;

    @Override
    public String toString() {
        return (code != null ? code + " - " : "") + name;
    }
}
