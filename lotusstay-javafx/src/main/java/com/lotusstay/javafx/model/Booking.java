package com.lotusstay.javafx.model;

public class Booking {
    public Long id;
    public Customer customer;
    public Room room;
    public String checkIn;
    public String checkOutPlanned;
    public String status; // DAT_TRUOC / DANG_O / DA_TRA / HUY

    public String statusLabel() {
        return switch (status == null ? "" : status) {
            case "DAT_TRUOC" -> "Đã đặt trước";
            case "DANG_O" -> "Đang lưu trú";
            case "DA_TRA" -> "Đã trả phòng";
            case "HUY" -> "Đã hủy";
            default -> status;
        };
    }
}
