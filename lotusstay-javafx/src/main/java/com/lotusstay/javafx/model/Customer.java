package com.lotusstay.javafx.model;

public class Customer {
    public Long id;
    public String fullName;
    public String phone;
    public String email;
    public String idNumber;

    @Override public String toString() { return fullName + " — " + (phone == null ? "" : phone); }
}
