package com.example.hotel_management_project.Models.Enums;

import lombok.Getter;

@Getter
public enum RoomStatus {
    TRONG("Trống"),
    DANG_DON("Đang dọn"),
    DANG_O("Đang ở"),
    DANG_BAO_TRI("Đang bảo trì");

    private final String label;

    RoomStatus(String label) {
        this.label = label;
    }

    public static RoomStatus fromLabel(String label) {
        for (RoomStatus s : values()) {
            if (s.label.equalsIgnoreCase(label)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái phòng: " + label);
    }
}
