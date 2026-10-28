package com.example.hotel_management_project.Models.Enums;

import lombok.Getter;

@Getter
public enum RoomStatus {
    TRONG("Trống"),
    DANG_DON("Đang dọn"),
    DANG_O("Đang ở"),
    DANG_BAO_TRI("Đang bảo trì");

    public static class Constants {
        public static final String TRONG = "Trống";
        public static final String DANG_DON = "Đang dọn";
        public static final String DANG_O = "Đang ở";
        public static final String DANG_BAO_TRI = "Đang bảo trì";
    }

    private final String label;

    RoomStatus(String label) {
        this.label = label;
    }

    public static RoomStatus fromLabel(String label) {
        if (label == null || label.isBlank()) {
            return TRONG;
        }
        String trimmed = label.trim();
        for (RoomStatus s : values()) {
            if (s.label.equalsIgnoreCase(trimmed) || s.name().equalsIgnoreCase(trimmed)) {
                return s;
            }
        }
        return TRONG;
    }
}
