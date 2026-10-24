package com.example.hotel_management_project.Models.Enums;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    DANG_XU_LY("Đang xử lý"),
    THANH_CONG("Thành công"),
    THAT_BAI("Thất bại");

    private final String label;

    TransactionStatus(String label) {
        this.label = label;
    }

    public static TransactionStatus fromLabel(String label) {
        for (TransactionStatus s : values()) {
            if (s.label.equalsIgnoreCase(label)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái giao dịch: " + label);
    }
}
