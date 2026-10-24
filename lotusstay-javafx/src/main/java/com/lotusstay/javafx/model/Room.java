package com.lotusstay.javafx.model;

public class Room {
    public Long id;
    public String code;      // P001, P002...
    public String number;    // so_phong: 101, 102...
    public RoomType roomType;// ManyToOne
    public Integer floor;    // tang: 1, 2, 3...
    public String status;    // TRONG, DANG_O, DANG_DON, DANG_BAO_TRI / BAO_TRI
    public String notes;     // ghi_chu: TEXT
    public String imagePath;
    public boolean active = true;

    public String statusLabel() {
        return switch (status == null ? "" : status) {
            case "TRONG" -> "Trống";
            case "DANG_O" -> "Đang ở";
            case "DANG_DON" -> "Đang dọn";
            case "BAO_TRI", "DANG_BAO_TRI" -> "Bảo trì";
            default -> status;
        };
    }

    public String badgeClass() {
        return switch (status == null ? "" : status) {
            case "TRONG" -> "badge-trong";
            case "DANG_O" -> "badge-dang_o";
            case "DANG_DON" -> "badge-dang_don";
            case "BAO_TRI", "DANG_BAO_TRI" -> "badge-bao_tri";
            default -> "badge-neutral";
        };
    }

    @Override
    public String toString() {
        return "Phòng " + number + (roomType != null ? " (" + roomType.name + ")" : "");
    }
}
