package com.lotusstay.javafx.model;

public class PaymentMethodItem {
    public Long id;
    public String name;
    public String qrImage;
    public String bankName;
    public String accountNumber;
    public String accountHolderName;
    public Boolean active;

    public String statusDisplay() {
        return (active != null && active) ? "Đang sử dụng" : "Tạm dừng";
    }

    public String badgeClass() {
        return (active != null && active) ? "badge-trong" : "badge-bao_tri";
    }
}
