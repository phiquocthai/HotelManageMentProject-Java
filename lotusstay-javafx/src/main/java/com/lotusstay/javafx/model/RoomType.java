package com.lotusstay.javafx.model;

import java.math.BigDecimal;

public class RoomType {
    public Long id;
    public String code;         // LP01, LP02...
    public String name;         // Tên loại phòng
    public BigDecimal basePrice;// Giá cơ bản (VNĐ)
    public Integer maxGuests;   // Số người tối đa (so_nguoi_toi_da)
    public String imagePath;
    public boolean active = true;

    @Override
    public String toString() {
        return (code != null ? code + " - " : "") + name;
    }
}
