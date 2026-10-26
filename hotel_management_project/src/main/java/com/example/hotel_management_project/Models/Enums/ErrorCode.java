package com.example.hotel_management_project.Models.Enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    USER_EXISTED(1001, "Người dùng đã tồn tại", HttpStatus.FOUND),
    USER_NOT_FOUND(1002, "Người dùng không tồn tại", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1003, "Mật khẩu không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_METHOD(1004, "HttpMethod không hợp lệ", HttpStatus.METHOD_NOT_ALLOWED),
    INVALID_KEY(9999, "Không xác định lỗi", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(2001, "Người dùng chưa xác thực", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2002, "Người dùng không có quyền truy cập", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(2003, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    INVALID_OTP(2004, "Mã OTP không hợp lệ", HttpStatus.BAD_REQUEST),
    EXPIRED_OTP(2005, "Mã OTP đã hết hạn", HttpStatus.BAD_REQUEST),

    // Staff
    STAFF_EXISTED(1011, "Nhân viên đã tồn tại", HttpStatus.BAD_REQUEST),
    STAFF_NOT_FOUND(1012, "Nhân viên không tồn tại", HttpStatus.BAD_REQUEST),

    // Customer
    CUSTOMER_EXISTED(1021, "Khách hàng đã tồn tại", HttpStatus.BAD_REQUEST),
    CUSTOMER_NOT_FOUND(1022, "Khách hàng không tồn tại", HttpStatus.BAD_REQUEST),

    // Room Type
    ROOM_TYPE_EXISTED(1031, "Loại phòng đã tồn tại", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_NOT_FOUND(1032, "Loại phòng không tồn tại", HttpStatus.BAD_REQUEST),

    // Work Shift
    WORK_SHIFT_EXISTED(1041, "Ca làm việc đã tồn tại", HttpStatus.BAD_REQUEST),
    WORK_SHIFT_NOT_FOUND(1042, "Ca làm việc không tồn tại", HttpStatus.BAD_REQUEST),
    WORK_SHIFT_TIME_OVERLAPPED(1043, "Thời gian ca làm việc bị trùng lặp với ca đã có", HttpStatus.BAD_REQUEST),
    INVALID_WORK_SHIFT_TIME(1044, "Giờ kết thúc phải sau giờ bắt đầu", HttpStatus.BAD_REQUEST),

    // Room
    ROOM_EXISTED(1051, "Phòng đã tồn tại", HttpStatus.BAD_REQUEST),
    ROOM_NOT_FOUND(1052, "Phòng không tồn tại", HttpStatus.BAD_REQUEST),

    // Booking
    BOOKING_EXISTED(1061, "Đơn đặt phòng đã tồn tại", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_FOUND(1062, "Đơn đặt phòng không tồn tại", HttpStatus.BAD_REQUEST),

    // Service
    SERVICE_EXISTED(1071, "Dịch vụ đã tồn tại", HttpStatus.BAD_REQUEST),
    SERVICE_NOT_FOUND(1072, "Dịch vụ không tồn tại", HttpStatus.BAD_REQUEST),

    // Shift Assignment
    SHIFT_ASSIGNMENT_EXISTED(1081, "Phân ca làm việc đã tồn tại", HttpStatus.BAD_REQUEST),
    SHIFT_ASSIGNMENT_NOT_FOUND(1082, "Phân ca làm việc không tồn tại", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}