package com.lotusstay.javafx.net;

import com.lotusstay.javafx.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PHIÊN BẢN MOCK (dữ liệu mẫu trong bộ nhớ) — KHÔNG gọi backend thật.
 *
 * Mục đích: cho phép chạy và xem toàn bộ giao diện ngay, trước khi nối backend.
 * Khi bạn làm xong API thật (dự án lotusstay-backend đã gửi), hãy:
 *   1. Đổi "ApiService" thành dùng lại class thật gọi ApiClient.get/post/put/patch/delete
 *      (xem file ApiService.java ở lịch sử trước — đã viết sẵn đúng endpoint).
 *   2. Hoặc đơn giản hơn: sửa từng hàm bên dưới, thay phần thao tác danh sách trong bộ nhớ
 *      bằng một dòng gọi ApiClient tương ứng.
 *
 * Mọi method ném lỗi dùng chung ApiClient.ApiException để các màn hình (Util.runAsync)
 * không cần sửa gì khi bạn chuyển sang gọi API thật.
 */
public class ApiService {

    // ============== "BẢNG" TRONG BỘ NHỚ ==============
    private static final List<UserAccount> USERS = new ArrayList<>();
    private static final Map<String, String> PASSWORDS = new HashMap<>(); // username -> password (chỉ để demo)
    private static final List<RoomType> ROOM_TYPES = new ArrayList<>();
    private static final List<Room> ROOMS = new ArrayList<>();
    private static final List<Customer> CUSTOMERS = new ArrayList<>();
    private static final List<Booking> BOOKINGS = new ArrayList<>();
    private static final List<ServiceItem> SERVICES = new ArrayList<>();
    private static final List<Staff> STAFF = new ArrayList<>();
    private static final List<Invoice> INVOICES = new ArrayList<>();

    private static long nextUserId = 1, nextRoomTypeId = 1, nextRoomId = 1, nextCustomerId = 1,
            nextBookingId = 1, nextServiceId = 1, nextStaffId = 1, nextInvoiceId = 1;

    static { seed(); }

    private static void seed() {
        // ---- Tài khoản ----
        UserAccount admin = user(nextUserId++, "admin", "Quản trị viên", "ADMIN");
        UserAccount manager = user(nextUserId++, "manager", "Nguyễn Thị Quản Lý", "MANAGER");
        UserAccount reception = user(nextUserId++, "reception", "Trần Văn Lễ Tân", "RECEPTION");
        USERS.addAll(List.of(admin, manager, reception));
        PASSWORDS.put("admin", "admin123");
        PASSWORDS.put("manager", "manager123");
        PASSWORDS.put("reception", "reception123");

        // ---- Loại phòng ----
        RoomType rtDon = roomType(nextRoomTypeId++, "Phòng Đơn", 350_000);
        RoomType rtDoi = roomType(nextRoomTypeId++, "Phòng Đôi", 500_000);
        RoomType rtVip = roomType(nextRoomTypeId++, "Phòng VIP", 900_000);
        RoomType rtGd = roomType(nextRoomTypeId++, "Phòng Gia Đình", 1_200_000);
        ROOM_TYPES.addAll(List.of(rtDon, rtDoi, rtVip, rtGd));

        // ---- Phòng ----
        ROOMS.add(room("101", rtDoi, 1, "TRONG"));
        ROOMS.add(room("102", rtDon, 1, "DANG_O"));
        ROOMS.add(room("103", rtDon, 1, "TRONG"));
        ROOMS.add(room("104", rtDoi, 1, "DANG_DON"));
        ROOMS.add(room("105", rtDoi, 1, "TRONG"));
        ROOMS.add(room("106", rtVip, 1, "DANG_O"));
        ROOMS.add(room("107", rtDoi, 1, "BAO_TRI"));
        ROOMS.add(room("108", rtDon, 1, "DANG_O"));
        ROOMS.add(room("201", rtVip, 2, "TRONG"));
        ROOMS.add(room("205", rtDoi, 2, "DANG_O"));
        ROOMS.add(room("301", rtVip, 3, "TRONG"));
        ROOMS.add(room("310", rtVip, 3, "DANG_DON"));
        ROOMS.add(room("401", rtGd, 4, "TRONG"));
        ROOMS.add(room("412", rtGd, 4, "DANG_O"));

        // ---- Khách hàng ----
        Customer a = customer(nextCustomerId++, "Nguyễn Văn A", "0901 234 501");
        Customer b = customer(nextCustomerId++, "Trần Thị B", "0901 234 502");
        Customer c = customer(nextCustomerId++, "Lê Văn C", "0901 234 503");
        Customer d = customer(nextCustomerId++, "Phạm Thị D", "0901 234 504");
        Customer e = customer(nextCustomerId++, "Hoàng Văn E", "0901 234 505");
        Customer f = customer(nextCustomerId++, "Vũ Thị F", "0901 234 506");
        CUSTOMERS.addAll(List.of(a, b, c, d, e, f));

        // ---- Đặt phòng đang diễn ra (khớp với phòng DANG_O ở trên) ----
        BOOKINGS.add(booking(b, findRoom("102"), LocalDate.now().minusDays(2), LocalDate.now().plusDays(1), "DANG_O"));
        BOOKINGS.add(booking(f, findRoom("412"), LocalDate.now().minusDays(1), LocalDate.now().plusDays(2), "DANG_O"));
        BOOKINGS.add(booking(e, findRoom("108"), LocalDate.now(), LocalDate.now().plusDays(1), "DANG_O"));

        // ---- Dịch vụ ----
        SERVICES.add(service(nextServiceId++, "Ăn sáng", 60_000));
        SERVICES.add(service(nextServiceId++, "Giặt ủi", 40_000));
        SERVICES.add(service(nextServiceId++, "Minibar", 30_000));
        SERVICES.add(service(nextServiceId++, "Đưa đón sân bay", 250_000));
        SERVICES.add(service(nextServiceId++, "Spa thư giãn", 350_000));

        // ---- Nhân viên ----
        STAFF.add(staff(manager, "Nguyễn Thị Quản Lý", "Quản lý", "Ca sáng (6h–14h)", "ACTIVE"));
        STAFF.add(staff(reception, "Trần Văn Lễ Tân", "Lễ tân", "Ca chiều (14h–22h)", "ACTIVE"));
        STAFF.add(staff(null, "Lê Thị Dọn Phòng", "Buồng phòng", "Ca sáng (6h–14h)", "ACTIVE"));
        STAFF.add(staff(null, "Phạm Văn Bảo Vệ", "Bảo vệ", "Ca đêm (22h–6h)", "INACTIVE"));

        // ---- Hóa đơn mẫu (để Dashboard/Báo cáo có số liệu) ----
        INVOICES.add(invoice(a, findRoom("101"), rtDoi, 500_000, 0, 50_000, LocalDate.now().minusDays(5)));
        INVOICES.add(invoice(b, findRoom("102"), rtDon, 350_000, 0, 0, LocalDate.now().minusDays(4)));
        INVOICES.add(invoice(c, findRoom("205"), rtDoi, 500_000, 0, 100_000, LocalDate.now().minusDays(3)));
        INVOICES.add(invoice(d, findRoom("310"), rtVip, 900_000, 0, 0, LocalDate.now().minusDays(2)));
        INVOICES.add(invoice(e, findRoom("108"), rtDon, 350_000, 0, 50_000, LocalDate.now().minusDays(1)));
        INVOICES.add(invoice(f, findRoom("412"), rtGd, 1_200_000, 0, 0, LocalDate.now()));
    }

    // ============== HÀM TẠO ĐỐI TƯỢNG MẪU ==============
    private static UserAccount user(long id, String username, String fullName, String role) {
        UserAccount u = new UserAccount();
        u.id = id; u.username = username; u.fullName = fullName; u.role = role; u.status = "ACTIVE";
        return u;
    }
    private static RoomType roomType(long id, String name, long price) {
        RoomType rt = new RoomType(); rt.id = id; rt.name = name; rt.basePrice = BigDecimal.valueOf(price);
        return rt;
    }
    private static Room room(String number, RoomType type, int floor, String status) {
        Room r = new Room();
        r.id = nextRoomId++; r.number = number; r.roomType = type; r.floor = floor; r.status = status;
        return r;
    }
    private static Customer customer(long id, String name, String phone) {
        Customer c = new Customer(); c.id = id; c.fullName = name; c.phone = phone; c.email = ""; c.idNumber = "";
        return c;
    }
    private static Booking booking(Customer c, Room r, LocalDate in, LocalDate outPlanned, String status) {
        Booking b = new Booking();
        b.id = nextBookingId++; b.customer = c; b.room = r;
        b.checkIn = in.toString(); b.checkOutPlanned = outPlanned.toString(); b.status = status;
        return b;
    }
    private static ServiceItem service(long id, String name, long price) {
        ServiceItem s = new ServiceItem(); s.id = id; s.name = name; s.price = BigDecimal.valueOf(price); s.active = true;
        return s;
    }
    private static Staff staff(UserAccount u, String name, String job, String shift, String status) {
        Staff s = new Staff();
        s.id = nextStaffId++; s.user = u; s.fullName = name; s.jobTitle = job; s.shift = shift; s.status = status;
        return s;
    }
    private static Invoice invoice(Customer c, Room r, RoomType type, long roomFee, long svcFee, long penalty, LocalDate date) {
        Invoice i = new Invoice();
        i.id = nextInvoiceId++; i.customer = c; i.room = r;
        i.roomFee = BigDecimal.valueOf(roomFee);
        i.serviceFee = BigDecimal.valueOf(svcFee);
        i.penaltyFee = BigDecimal.valueOf(penalty);
        i.total = i.roomFee.add(i.serviceFee).add(i.penaltyFee);
        i.issuedDate = date.toString();
        return i;
    }
    private static Room findRoom(String number) {
        return ROOMS.stream().filter(r -> r.number.equals(number)).findFirst().orElse(null);
    }

    // ============== AUTH ==============
    public static synchronized LoginResponse login(String username, String password) {
        sleep();
        if (!PASSWORDS.containsKey(username) || !PASSWORDS.get(username).equals(password)) {
            throw new ApiClient.ApiException("Sai tên đăng nhập hoặc mật khẩu.");
        }
        UserAccount u = USERS.stream().filter(x -> x.username.equals(username)).findFirst().orElseThrow();
        if ("INACTIVE".equals(u.status)) throw new ApiClient.ApiException("Tài khoản đã bị khóa.");
        LoginResponse res = new LoginResponse();
        res.token = "mock-token"; res.userId = u.id; res.username = u.username;
        res.fullName = u.fullName; res.role = u.role;
        return res;
    }

    // ============== DASHBOARD ==============
    public static synchronized DashboardStats dashboard() {
        sleep();
        String today = LocalDate.now().toString();
        DashboardStats s = new DashboardStats();
        List<Invoice> todayInv = INVOICES.stream().filter(i -> today.equals(i.issuedDate)).collect(Collectors.toList());
        s.todayRevenue = todayInv.stream().map(i -> i.total).reduce(BigDecimal.ZERO, BigDecimal::add);
        s.todayInvoiceCount = todayInv.size();
        s.totalRooms = ROOMS.size();
        s.occupiedRooms = ROOMS.stream().filter(r -> "DANG_O".equals(r.status)).count();
        s.availableRooms = ROOMS.stream().filter(r -> "TRONG".equals(r.status)).count();
        s.upcomingCheckouts = BOOKINGS.stream().filter(b -> "DANG_O".equals(b.status)
                && b.checkOutPlanned != null && b.checkOutPlanned.compareTo(LocalDate.now().plusDays(1).toString()) <= 0).count();
        return s;
    }

    // ============== ROOM TYPES ==============
    public static synchronized List<RoomType> roomTypes() { sleep(); return new ArrayList<>(ROOM_TYPES); }

    public static synchronized RoomType createRoomType(String name, BigDecimal price, Integer maxGuests) {
        sleep();
        RoomType rt = roomType(nextRoomTypeId++, name, price.longValue());
        rt.maxGuests = maxGuests;
        ROOM_TYPES.add(rt);
        return rt;
    }

    public static synchronized void deleteRoomType(Long id) {
        sleep();
        boolean inUse = ROOMS.stream().anyMatch(r -> r.roomType != null && r.roomType.id.equals(id));
        if (inUse) throw new ApiClient.ApiException("Không thể xóa — vẫn còn phòng thuộc loại này.");
        ROOM_TYPES.removeIf(rt -> rt.id.equals(id));
    }

    // ============== ROOMS ==============
    public static synchronized List<Room> rooms() { sleep(); return new ArrayList<>(ROOMS); }

    public static synchronized Room createRoom(String number, Long roomTypeId, Integer floor, String notes) {
        sleep();
        RoomType type = ROOM_TYPES.stream().filter(t -> t.id.equals(roomTypeId)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy loại phòng."));
        Room r = room(number, type, floor == null ? 1 : floor, "TRONG");
        r.notes = notes;
        ROOMS.add(r);
        return r;
    }

    public static synchronized Room updateRoomStatus(Long roomId, String status) {
        sleep();
        Room r = ROOMS.stream().filter(x -> x.id.equals(roomId)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy phòng."));
        r.status = status;
        return r;
    }

    // ============== CUSTOMERS ==============
    public static synchronized List<Customer> customers(String query) {
        sleep();
        if (query == null || query.isBlank()) return new ArrayList<>(CUSTOMERS);
        String q = query.toLowerCase();
        return CUSTOMERS.stream()
                .filter(c -> c.fullName.toLowerCase().contains(q) || (c.phone != null && c.phone.contains(q)))
                .collect(Collectors.toList());
    }

    public static synchronized Customer createCustomer(String fullName, String phone, String email, String idNumber) {
        sleep();
        Customer c = customer(nextCustomerId++, fullName, phone);
        c.email = email; c.idNumber = idNumber;
        CUSTOMERS.add(c);
        return c;
    }

    // ============== BOOKINGS ==============
    public static synchronized List<Booking> bookings() { sleep(); return new ArrayList<>(BOOKINGS); }

    public static synchronized Booking createBooking(Long customerId, String newCustomerName, String newCustomerPhone,
                                                       Long roomId, String checkIn, String checkOutPlanned, boolean checkInNow) {
        sleep();
        Room room = ROOMS.stream().filter(r -> r.id.equals(roomId)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy phòng."));
        if (!"TRONG".equals(room.status)) throw new ApiClient.ApiException("Phòng hiện không trống.");

        Customer customer;
        if (customerId != null) {
            customer = CUSTOMERS.stream().filter(c -> c.id.equals(customerId)).findFirst()
                    .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy khách hàng."));
        } else {
            if (newCustomerName == null || newCustomerName.isBlank())
                throw new ApiClient.ApiException("Thiếu thông tin khách hàng.");
            customer = customer(nextCustomerId++, newCustomerName.trim(), newCustomerPhone);
            CUSTOMERS.add(customer);
        }

        Booking b = new Booking();
        b.id = nextBookingId++; b.customer = customer; b.room = room;
        b.checkIn = checkIn; b.checkOutPlanned = checkOutPlanned;
        b.status = checkInNow ? "DANG_O" : "DAT_TRUOC";
        if (checkInNow) room.status = "DANG_O";
        BOOKINGS.add(b);
        return b;
    }

    public static synchronized Booking checkIn(Long bookingId) {
        sleep();
        Booking b = BOOKINGS.stream().filter(x -> x.id.equals(bookingId)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy lượt đặt phòng."));
        b.status = "DANG_O";
        b.room.status = "DANG_O";
        return b;
    }

    public static synchronized Booking cancelBooking(Long bookingId) {
        sleep();
        Booking b = BOOKINGS.stream().filter(x -> x.id.equals(bookingId)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy lượt đặt phòng."));
        b.status = "HUY";
        return b;
    }

    // ============== INVOICES ==============
    public static synchronized List<Invoice> invoices() { sleep(); return new ArrayList<>(INVOICES); }

    public static synchronized Invoice checkout(Long bookingId, Long roomId, Long customerId, String checkOutDate,
                                                 BigDecimal roomFee, BigDecimal penaltyFee, List<Map<String, Object>> services) {
        sleep();
        Booking booking = null;
        Room room = null;
        Customer customer = null;

        if (bookingId != null) {
            booking = BOOKINGS.stream().filter(b -> b.id.equals(bookingId)).findFirst().orElse(null);
            if (booking != null) { room = booking.room; customer = booking.customer; }
        } else {
            if (roomId != null) room = ROOMS.stream().filter(r -> r.id.equals(roomId)).findFirst().orElse(null);
            if (customerId != null) customer = CUSTOMERS.stream().filter(c -> c.id.equals(customerId)).findFirst().orElse(null);
        }

        BigDecimal serviceFee = BigDecimal.ZERO; // mock: chưa cộng chi tiết dịch vụ, chỉ dùng roomFee/penaltyFee gửi lên
        Invoice inv = new Invoice();
        inv.id = nextInvoiceId++; inv.booking = booking; inv.room = room; inv.customer = customer;
        inv.roomFee = roomFee == null ? BigDecimal.ZERO : roomFee;
        inv.serviceFee = serviceFee;
        inv.penaltyFee = penaltyFee == null ? BigDecimal.ZERO : penaltyFee;
        inv.total = inv.roomFee.add(inv.serviceFee).add(inv.penaltyFee);
        inv.issuedDate = checkOutDate == null ? LocalDate.now().toString() : checkOutDate;
        INVOICES.add(inv);

        if (booking != null) booking.status = "DA_TRA";
        if (room != null) room.status = "DANG_DON";
        return inv;
    }

    // ============== SERVICES (danh mục) ==============
    public static synchronized List<ServiceItem> services() { sleep(); return new ArrayList<>(SERVICES); }

    public static synchronized ServiceItem createService(String name, BigDecimal price) {
        sleep();
        ServiceItem s = service(nextServiceId++, name, price.longValue());
        SERVICES.add(s);
        return s;
    }

    public static synchronized void deleteService(Long id) {
        sleep();
        SERVICES.removeIf(s -> s.id.equals(id));
    }

    // ============== STAFF ==============
    public static synchronized List<Staff> staff() { sleep(); return new ArrayList<>(STAFF); }

    public static synchronized Staff createStaff(String fullName, String jobTitle, String shift, String phone, Long userId) {
        sleep();
        UserAccount u = userId == null ? null :
                USERS.stream().filter(x -> x.id.equals(userId)).findFirst().orElse(null);
        Staff s = staff(u, fullName, jobTitle, shift, "ACTIVE");
        s.phone = phone;
        STAFF.add(s);
        return s;
    }

    public static synchronized void deleteStaff(Long id) {
        sleep();
        STAFF.removeIf(s -> s.id.equals(id));
    }

    // ============== USERS (tài khoản hệ thống) ==============
    public static synchronized List<UserAccount> users() { sleep(); return new ArrayList<>(USERS); }

    public static synchronized UserAccount createUser(String username, String password, String email, String phone, String role) {
        sleep();
        if (USERS.stream().anyMatch(u -> u.username.equals(username)))
            throw new ApiClient.ApiException("Tên đăng nhập đã tồn tại.");
        UserAccount u = user(nextUserId++, username, "", role); // No fullName passed in the updated view yet, use "" or wait, I changed UsersView to not prompt for fullname. Let's just pass empty or we can add it back later if needed.
        u.email = email;
        u.phone = phone;
        USERS.add(u);
        PASSWORDS.put(username, (password == null || password.isBlank()) ? "123456" : password);
        return u;
    }

    public static synchronized UserAccount toggleUserStatus(Long id) {
        sleep();
        UserAccount u = USERS.stream().filter(x -> x.id.equals(id)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy tài khoản."));
        u.status = "ACTIVE".equals(u.status) ? "INACTIVE" : "ACTIVE";
        return u;
    }

    /** Giả lập độ trễ mạng nhẹ để trải nghiệm UI (loading…) giống gọi API thật. */
    private static void sleep() {
        try { Thread.sleep(150); } catch (InterruptedException ignored) {}
    }
}
