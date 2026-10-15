package com.example.hotel_management_project.Models.Enums;

import lombok.Getter;

@Getter
public enum AttendanceStatus {
    DU_CONG("Đủ công"),
    DI_TRE("Đi trễ"),
    VE_SOM("Về sớm"),
    VANG_MAT("Vắng mặt");

    private final String label;

    AttendanceStatus(String label) {
        this.label = label;
    }

    public static AttendanceStatus fromLabel(String label) {
        for (AttendanceStatus s : values()) {
            if (s.label.equalsIgnoreCase(label)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái chấm công: " + label);
    }
}
