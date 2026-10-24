package com.lotusstay.javafx.model;

public class Customer {
    public Long id;
    public String code;      // KH001, KH002...
    public String fullName;  // Tối đa 30 ký tự
    public String citizenId; // so_CCCD: 12 số
    public String email;     // Tối đa 100 ký tự
    public String phone;     // SDT: 10 số
    public String imagePath;
    public boolean active = true;

    // Backward compatibility aliases
    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String val) { this.citizenId = val; }
    public String getIdNumber() { return citizenId; }
    public void setIdNumber(String val) { this.citizenId = val; }

    @Override
    public String toString() {
        return (code != null ? code + " - " : "") + fullName + " (" + (phone == null ? "" : phone) + ")";
    }
}
