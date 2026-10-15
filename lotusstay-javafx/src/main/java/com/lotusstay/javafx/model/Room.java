package com.lotusstay.javafx.model;

public class Room {
    public Long id;
    public String number;
    public RoomType roomType;
    public Integer floor;
    public String status; // TRONG / DANG_O / DANG_DON / BAO_TRI
    public String notes;

    public String statusLabel() {
        return switch (status == null ? "" : status) {
            case "TRONG" -> "Trống";
            case "DANG_O" -> "Đang ở";
            case "DANG_DON" -> "Đang dọn";
            case "BAO_TRI" -> "Bảo trì";
            default -> status;
        };
    }
}
