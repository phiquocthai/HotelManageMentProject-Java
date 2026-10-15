package com.example.hotel_management_project.Models.Enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("Quản trị viên"),
    MANAGER("Quản lý"),
    RECEPTION("Lễ tân");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public static Role fromLabel(String label) {
        for (Role s : values()) {
            if (s.label.equalsIgnoreCase(label)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy vai trò: " + label);
    }
}
