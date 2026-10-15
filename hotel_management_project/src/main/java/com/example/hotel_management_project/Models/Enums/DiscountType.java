package com.example.hotel_management_project.Models.Enums;

import lombok.Getter;

@Getter
public enum DiscountType {
    PHAN_TRAM("Phần trăm"),
    SO_TIEN("Số tiền");

    private final String label;

    DiscountType(String label) {
        this.label = label;
    }

    public static DiscountType fromLabel(String label) {
        for (DiscountType s : values()) {
            if (s.label.equalsIgnoreCase(label)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy loại giảm giá: " + label);
    }
}
