package com.lotusstay.javafx.model;

public class UserAccount {
    public Long id;
    public String username;
    public String email;
    public String phone;
    public String fullName;
    public String role;    // ADMIN / MANAGER / RECEPTION
    public String status;  // ACTIVE / INACTIVE

    @Override public String toString() { return "@" + username + " (" + email + ")"; }
}
