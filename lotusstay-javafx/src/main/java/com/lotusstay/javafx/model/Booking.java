package com.lotusstay.javafx.model;

public class Booking {
    public Long id;
    public String code;               // DP001, DP002...
    public Customer customer;         // ManyToOne
    public Room room;                 // ManyToOne
    public String checkIn;            // checkInDate
    public String checkOutPlanned;     // expectedCheckOutDate
    public String actualCheckIn;      // actualCheckInTime
    public String actualCheckOut;     // actualCheckOutTime
    public Integer numberOfGuests = 1;// so_nguoi_o (TINYINT)
    public String status;             // DAT_TRUOC, DANG_O, DA_TRA, HUY
    public String notes;              // ghi_chu (TEXT)

    public String statusLabel() {
        return switch (status == null ? "" : status) {
            case "DAT_TRUOC", "CHO_XAC_NHAN", "DA_XAC_NHAN" -> "Đã đặt trước";
            case "DANG_O", "DANG_SU_DUNG" -> "Đang lưu trú";
            case "DA_TRA", "DA_TRA_PHONG" -> "Đã trả phòng";
            case "HUY", "DA_HUY" -> "Đã hủy";
            default -> status;
        };
    }

    public String badgeClass() {
        return switch (status == null ? "" : status) {
            case "DAT_TRUOC", "CHO_XAC_NHAN", "DA_XAC_NHAN" -> "badge-dat_truoc";
            case "DANG_O", "DANG_SU_DUNG" -> "badge-dang_o";
            case "DA_TRA", "DA_TRA_PHONG" -> "badge-trong";
            case "HUY", "DA_HUY" -> "badge-bao_tri";
            default -> "badge-neutral";
        };
    }
}
