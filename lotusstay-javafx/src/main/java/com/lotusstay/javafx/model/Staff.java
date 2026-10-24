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
}
