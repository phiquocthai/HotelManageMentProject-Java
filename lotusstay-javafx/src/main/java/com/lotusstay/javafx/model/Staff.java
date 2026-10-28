package com.lotusstay.javafx.model;

public class Staff {
    public Long id;
    public String code;         // Mã TK / Mã NV: TK001, TK002...
    public String username;     // Tên đăng nhập: admin01, quanly01, letan01...
    public String password;     // Mật khẩu
    public String fullName;     // Họ và tên
    public String email;        // Email
    public String phone;        // Số điện thoại
    public String jobTitle;     // Chức danh: Quản trị viên, Quản lý, Lễ tân...
    public String role;         // ADMIN, MANAGER, RECEPTION
    public String shift;        // Ca làm việc
    public String status;       // ACTIVE (Hoạt động) / INACTIVE (Đã khóa)
    public String createdAt;    // Ngày tạo: dd/MM/yyyy

    // Tương thích ngược nếu có chỗ gọi .user
    public UserAccount user;

    public Staff() {}

    public Staff(Long id, String code, String username, String password, String fullName,
                 String email, String phone, String jobTitle, String role, String shift,
                 String status, String createdAt) {
        this.id = id;
        this.code = code;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.jobTitle = jobTitle;
        this.role = role;
        this.shift = shift;
        this.status = status;
        this.createdAt = createdAt;

        UserAccount u = new UserAccount();
        u.id = id;
        u.username = username;
        u.fullName = fullName;
        u.email = email;
        u.phone = phone;
        u.role = role;
        u.status = status;
        this.user = u;
    }

    public String getRoleDisplay() {
        String r = resolveRole(this.role, this.username);
        return switch (r) {
            case "ADMIN" -> "Quản trị viên";
            case "MANAGER" -> "Quản lý";
            default -> "Lễ tân";
        };
    }

    public static String resolveRole(String role, String username) {
        if (role != null && !role.isBlank() && !"null".equalsIgnoreCase(role)) {
            String clean = role.trim().toUpperCase();
            if (clean.contains("ADMIN")) return "ADMIN";
            if (clean.contains("MGR") || clean.contains("MANAGER")) return "MANAGER";
            if (clean.contains("REC") || clean.contains("RECEPTION")) return "RECEPTION";
            return clean;
        }
        if (username != null) {
            String u = username.toLowerCase();
            if (u.contains("admin")) return "ADMIN";
            if (u.contains("mgr") || u.contains("manager") || u.contains("quanly")) return "MANAGER";
            if (u.contains("rec") || u.contains("letan")) return "RECEPTION";
        }
        return "RECEPTION";
    }
}
