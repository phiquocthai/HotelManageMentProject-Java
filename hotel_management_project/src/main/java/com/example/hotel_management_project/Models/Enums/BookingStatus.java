package com.example.hotel_management_project.Models.Enums;

import lombok.Getter;

@Getter
public enum BookingStatus {
    DAT_TRUOC("Đặt trước"),
    DANG_O("Đang ở"),
    DA_TRA("Đã trả"),
    DA_HUY("Đã hủy");

    private final String label;

    BookingStatus(String label) {
        this.label = label;
    }

    /**
     * Tìm enum từ label tiếng Việt.
     * @throws IllegalArgumentException nếu không tìm thấy
     */
    public static BookingStatus fromLabel(String label) {
        for (BookingStatus s : values()) {
            if (s.label.equalsIgnoreCase(label)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái đặt phòng: " + label);
    }
}
