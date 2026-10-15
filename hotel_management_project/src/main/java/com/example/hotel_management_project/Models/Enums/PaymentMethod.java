package com.example.hotel_management_project.Models.Enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    TIEN_MAT("Tiền mặt"),
    MOMO("MoMo"),
    CHUYEN_KHOAN("Chuyển khoản");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public static PaymentMethod fromLabel(String label) {
        for (PaymentMethod s : values()) {
            if (s.label.equalsIgnoreCase(label)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy phương thức thanh toán: " + label);
    }
}
