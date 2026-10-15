package com.example.hotel_management_project.Models.Enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    CHUA_THANH_TOAN("Chưa thanh toán"),
    THANH_TOAN_MOT_PHAN("Thanh toán một phần"),
    DA_THANH_TOAN("Đã thanh toán");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

    public static PaymentStatus fromLabel(String label) {
        for (PaymentStatus s : values()) {
            if (s.label.equalsIgnoreCase(label)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái thanh toán: " + label);
    }
}
