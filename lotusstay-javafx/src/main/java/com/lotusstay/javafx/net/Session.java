package com.lotusstay.javafx.net;

/** Lưu thông tin phiên đăng nhập hiện tại (token JWT + người dùng) cho toàn ứng dụng. */
public class Session {
    private static String token;
    private static Long userId;
    private static String username;
    private static String fullName;
    private static String role; // ADMIN / MANAGER / RECEPTION

    public static void set(String token, Long userId, String username, String fullName, String role) {
        Session.token = token;
        Session.userId = userId;
        Session.username = username;
        Session.fullName = fullName;
        Session.role = role;
    }

    public static void clear() {
        token = null; userId = null; username = null; fullName = null; role = null;
    }

    public static boolean isLoggedIn() { return token != null; }
    public static String getToken() { return token; }
    public static String getFullName() { return fullName; }
    public static String getRole() { return role; }

    public static boolean isAdmin() { return "ADMIN".equals(role); }
    public static boolean isManagerOrAbove() { return isAdmin() || "MANAGER".equals(role); }
}
