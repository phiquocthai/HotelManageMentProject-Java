package com.lotusstay.javafx.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Promotion {
    public Long id;
    public String code;
    public String name;
    public String discountType; // "PHAN_TRAM" or "SO_TIEN"
    public BigDecimal discountValue;
    public LocalDate startDate;
    public LocalDate endDate;
    public Boolean active;

    public String discountTypeDisplay() {
        if ("PHAN_TRAM".equalsIgnoreCase(discountType)) return "Phần trăm (%)";
        if ("SO_TIEN".equalsIgnoreCase(discountType)) return "Số tiền (VNĐ)";
        return discountType != null ? discountType : "—";
    }

    public String discountValueDisplay() {
        if (discountValue == null) return "0";
        if ("PHAN_TRAM".equalsIgnoreCase(discountType)) {
            return discountValue.stripTrailingZeros().toPlainString() + "%";
        }
        return String.format("%,d đ", discountValue.longValue());
    }

    public boolean isExpired() {
        if (endDate == null) return false;
        return endDate.isBefore(LocalDate.now());
    }

    public boolean isUpcoming() {
        if (startDate == null) return false;
        return startDate.isAfter(LocalDate.now());
    }

    public String statusDisplay() {
        if (active != null && !active) return "Tạm ngưng";
        if (isExpired()) return "Đã hết hạn";
        if (isUpcoming()) return "Sắp diễn ra";
        return "Đang áp dụng";
    }

    public String badgeClass() {
        if (active != null && !active) return "badge-bao_tri";
        if (isExpired()) return "badge-bao_tri";
        if (isUpcoming()) return "badge-dang_o";
        return "badge-trong";
    }
}
