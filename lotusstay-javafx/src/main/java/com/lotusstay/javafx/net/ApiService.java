package com.lotusstay.javafx.net;

import com.lotusstay.javafx.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PHIÊN BẢN MOCK (dữ liệu mẫu trong bộ nhớ) — KHÔNG gọi backend thật.
 * Phản ánh đầy đủ các Entity và các trường từ backend Spring Boot.
 */
public class ApiService {

    // ============== "BẢNG" TRONG BỘ NHỚ ==============
    private static final List<UserAccount> USERS = new ArrayList<>();
    private static final Map<String, String> PASSWORDS = new HashMap<>();
    private static final List<RoomType> ROOM_TYPES = new ArrayList<>();
    private static final List<Room> ROOMS = new ArrayList<>();
    private static final List<Customer> CUSTOMERS = new ArrayList<>();
    private static final List<Booking> BOOKINGS = new ArrayList<>();
    private static final List<ServiceItem> SERVICES = new ArrayList<>();
    private static final List<Staff> STAFF = new ArrayList<>();
    private static final List<Invoice> INVOICES = new ArrayList<>();
    private static final List<WorkShift> WORK_SHIFTS = new ArrayList<>();
    private static final List<ShiftAssignment> SHIFT_ASSIGNMENTS = new ArrayList<>();
    private static final List<Attendance> ATTENDANCES = new ArrayList<>();

    private static long nextUserId = 1, nextRoomTypeId = 1, nextRoomId = 1, nextCustomerId = 1,
            nextBookingId = 1, nextServiceId = 1, nextStaffId = 1, nextInvoiceId = 1,
            nextWorkShiftId = 1, nextShiftAssignmentId = 1, nextAttendanceId = 1;

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

        // ---- Loại phòng (Mã LP01, LP02...) ----
        RoomType rtDon = roomType("LP01", "Phòng Đơn Standard", 350_000, 1);
        RoomType rtDoi = roomType("LP02", "Phòng Đôi Superior", 550_000, 2);
        RoomType rtVip = roomType("LP03", "Phòng VIP Deluxe", 950_000, 2);
        RoomType rtGd = roomType("LP04", "Phòng Gia Đình Suite", 1_400_000, 4);
        ROOM_TYPES.addAll(List.of(rtDon, rtDoi, rtVip, rtGd));

        // ---- Phòng (Mã P001, P002...) ----
        ROOMS.add(room("P001", "101", rtDoi, 1, "TRONG", "Gần sảnh chính, cửa sổ thoáng"));
        ROOMS.add(room("P002", "102", rtDon, 1, "DANG_O", "Khách lưu trú 3 đêm"));
        ROOMS.add(room("P003", "103", rtDon, 1, "TRONG", "Phòng tầng trệt"));
        ROOMS.add(room("P004", "104", rtDoi, 1, "DANG_DON", "Khách vừa trả lúc 11h"));
        ROOMS.add(room("P005", "105", rtDoi, 1, "TRONG", "View sân vườn"));
        ROOMS.add(room("P006", "106", rtVip, 1, "DANG_O", "Khách VIP đoàn công tác"));
        ROOMS.add(room("P007", "107", rtDoi, 1, "BAO_TRI", "Bảo dưỡng máy lạnh"));
        ROOMS.add(room("P008", "108", rtDon, 1, "DANG_O", "Khách ở dài hạn"));
        ROOMS.add(room("P009", "201", rtVip, 2, "TRONG", "View hồ bơi"));
        ROOMS.add(room("P010", "205", rtDoi, 2, "DANG_O", "Đã thanh toán trước"));
        ROOMS.add(room("P011", "301", rtVip, 3, "TRONG", "Tầng cao yên tĩnh"));
        ROOMS.add(room("P012", "310", rtVip, 3, "DANG_DON", "Thay ga giường"));
        ROOMS.add(room("P013", "401", rtGd, 4, "TRONG", "Suite gia đình cao cấp"));
        ROOMS.add(room("P014", "412", rtGd, 4, "DANG_O", "Gia đình 4 người"));

        // ---- Khách hàng (Mã KH001, KH002...) ----
        Customer a = customer("KH001", "Nguyễn Văn An", "0901234501", "079201001234", "an.nguyen@gmail.com");
        Customer b = customer("KH002", "Trần Thị Bích", "0901234502", "079201001235", "bich.tran@gmail.com");
        Customer c = customer("KH003", "Lê Hoàng Cường", "0901234503", "079201001236", "cuong.le@gmail.com");
        Customer d = customer("KH004", "Phạm Thị Diễm", "0901234504", "079201001237", "diem.pham@gmail.com");
        Customer e = customer("KH005", "Hoàng Văn Em", "0901234505", "079201001238", "em.hoang@gmail.com");
        Customer f = customer("KH006", "Vũ Mai Phương", "0901234506", "079201001239", "phuong.vu@gmail.com");
        CUSTOMERS.addAll(List.of(a, b, c, d, e, f));

        // ---- Đặt phòng đang diễn ra (Mã DP001, DP002...) ----
        BOOKINGS.add(booking("DP001", b, findRoom("102"), LocalDate.now().minusDays(2), LocalDate.now().plusDays(1), "DANG_O", "Nhận phòng sớm"));
        BOOKINGS.add(booking("DP002", f, findRoom("412"), LocalDate.now().minusDays(1), LocalDate.now().plusDays(2), "DANG_O", "Yêu cầu cũi trẻ em"));
        BOOKINGS.add(booking("DP003", e, findRoom("108"), LocalDate.now(), LocalDate.now().plusDays(1), "DANG_O", "Xuất hóa đơn đỏ"));

        // ---- Dịch vụ (Mã DV001, DV002...) ----
        SERVICES.add(service("DV001", "Buffet Ăn sáng", 80_000));
        SERVICES.add(service("DV002", "Giặt ủi quần áo", 45_000));
        SERVICES.add(service("DV003", "Đồ uống Minibar", 35_000));
        SERVICES.add(service("DV004", "Đưa đón sân bay", 280_000));
        SERVICES.add(service("DV005", "Spa & Massage thư giãn", 400_000));

        // ---- Nhân viên & Tài khoản (làm chung) ----
        Staff stAdmin = staff("TK001", "admin01", "admin123", "Phi Quốc Thái (Admin)", "admin@hotel.vn", "0901000001", "Quản trị viên", "ADMIN", "Toàn thời gian", "ACTIVE", "01/01/2026");
        Staff ql1 = staff("TK002", "quanly01", "quanly123", "Nguyễn Thị Quản Lý", "quanly01@hotel.vn", "0901000002", "Quản lý", "MANAGER", "Ca sáng (6h–14h)", "ACTIVE", "05/01/2026");
        Staff lt1 = staff("TK003", "letan01", "letan123", "Trần Văn Lễ Tân", "letan01@hotel.vn", "0901000003", "Lễ tân", "RECEPTION", "Ca chiều (14h–22h)", "ACTIVE", "10/01/2026");
        Staff lt2 = staff("TK004", "letan02", "letan123", "Lê Hoàng Lễ Tân", "letan02@hotel.vn", "0901000004", "Lễ tân", "RECEPTION", "Ca đêm (22h–6h)", "INACTIVE", "12/02/2026");
        Staff lt3 = staff("TK005", "letan03", "letan123", "Phạm Minh Đức", "letan03@hotel.vn", "0901000005", "Lễ tân", "RECEPTION", "Ca sáng (6h–14h)", "ACTIVE", "20/03/2026");
        Staff ql2 = staff("TK006", "quanly02", "quanly123", "Võ Thị Ánh Tuyết", "anhtuyet@hotel.vn", "0901000006", "Quản lý", "MANAGER", "Ca chiều (14h–22h)", "ACTIVE", "02/04/2026");
        STAFF.addAll(List.of(stAdmin, ql1, lt1, lt2, lt3, ql2));

        for (Staff st : STAFF) {
            PASSWORDS.put(st.username, st.password);
        }

        // ---- Ca làm việc ----
        WorkShift caSang = workShift(nextWorkShiftId++, "Ca sáng", "06:00", "14:00");
        WorkShift caChieu = workShift(nextWorkShiftId++, "Ca chiều", "14:00", "22:00");
        WorkShift caDem = workShift(nextWorkShiftId++, "Ca đêm", "22:00", "06:00");
        WorkShift caSangSom = workShift(nextWorkShiftId++, "Ca sáng sớm", "19:00", "23:00");
        WORK_SHIFTS.addAll(List.of(caSang, caChieu, caDem, caSangSom));

        // ---- Phân ca (ngày 10/10/2026 & 09/10/2026) ----
        ShiftAssignment sa1 = shiftAssignment(nextShiftAssignmentId++, ql1, caSang, "10/10/2026");
        ShiftAssignment sa2 = shiftAssignment(nextShiftAssignmentId++, lt1, caChieu, "10/10/2026");
        ShiftAssignment sa3 = shiftAssignment(nextShiftAssignmentId++, lt2, caSang, "10/10/2026");
        ShiftAssignment sa4 = shiftAssignment(nextShiftAssignmentId++, lt3, caDem, "10/10/2026");
        ShiftAssignment sa5 = shiftAssignment(nextShiftAssignmentId++, ql1, caSang, "09/10/2026");
        ShiftAssignment sa6 = shiftAssignment(nextShiftAssignmentId++, lt1, caChieu, "09/10/2026");
        ShiftAssignment sa7 = shiftAssignment(nextShiftAssignmentId++, lt2, caSang, "09/10/2026");
        SHIFT_ASSIGNMENTS.addAll(List.of(sa1, sa2, sa3, sa4, sa5, sa6, sa7));

        // ---- Chấm công ----
        Attendance att1 = attendance(nextAttendanceId++, sa3, "06:00", null, "DU_CONG", "");
        Attendance att2 = attendance(nextAttendanceId++, sa1, "06:02", null, "DU_CONG", "");
        Attendance att3 = attendance(nextAttendanceId++, sa7, "06:00", "13:30", "VE_SOM", "Xin về sớm");
        Attendance att4 = attendance(nextAttendanceId++, sa6, "14:20", "22:00", "DI_TRE", "Kẹt xe");
        Attendance att5 = attendance(nextAttendanceId++, sa5, "05:58", "14:02", "DU_CONG", "");
        ATTENDANCES.addAll(List.of(att1, att2, att3, att4, att5));
        sa3.attendance = att1; sa1.attendance = att2;
        sa7.attendance = att3; sa6.attendance = att4; sa5.attendance = att5;

        // ---- Hóa đơn mẫu (Mã HD001, HD002...) ----
        INVOICES.add(invoice("HD001", "DP000", a, findRoom("101"), 550_000, 0, 0, 0, LocalDate.now().minusDays(5), "DA_THANH_TOAN"));
        INVOICES.add(invoice("HD002", "DP001", b, findRoom("102"), 350_000, 80_000, 0, 0, LocalDate.now().minusDays(4), "DA_THANH_TOAN"));
        INVOICES.add(invoice("HD003", "DP000", c, findRoom("205"), 550_000, 45_000, 50_000, 0, LocalDate.now().minusDays(3), "DA_THANH_TOAN"));
        INVOICES.add(invoice("HD004", "DP000", d, findRoom("310"), 950_000, 280_000, 0, 50_000, LocalDate.now().minusDays(2), "DA_THANH_TOAN"));
        INVOICES.add(invoice("HD005", "DP003", e, findRoom("108"), 350_000, 0, 0, 0, LocalDate.now().minusDays(1), "DA_THANH_TOAN"));
        INVOICES.add(invoice("HD006", "DP002", f, findRoom("412"), 1_400_000, 400_000, 0, 100_000, LocalDate.now(), "CHUA_THANH_TOAN"));
    }

    // ============== HÀM TẠO ĐỐI TƯỢNG MẪU ==============
    private static UserAccount user(long id, String username, String fullName, String role) {
        UserAccount u = new UserAccount();
        u.id = id; u.username = username; u.fullName = fullName; u.role = role; u.status = "ACTIVE";
        return u;
    }

    private static RoomType roomType(String code, String name, long price, int maxGuests) {
        RoomType rt = new RoomType();
        rt.id = nextRoomTypeId++;
        rt.code = code;
        rt.name = name;
        rt.basePrice = BigDecimal.valueOf(price);
        rt.maxGuests = maxGuests;
        rt.active = true;
        return rt;
    }

    private static Room room(String code, String number, RoomType type, int floor, String status, String notes) {
        Room r = new Room();
        r.id = nextRoomId++;
        r.code = code;
        r.number = number;
        r.roomType = type;
        r.floor = floor;
        r.status = status;
        r.notes = notes;
        r.active = true;
        return r;
    }

    private static Customer customer(String code, String name, String phone, String citizenId, String email) {
        Customer c = new Customer();
        c.id = nextCustomerId++;
        c.code = code;
        c.fullName = name;
        c.phone = phone;
        c.citizenId = citizenId;
        c.email = email;
        c.active = true;
        return c;
    }

    private static Booking booking(String code, Customer c, Room r, LocalDate in, LocalDate outPlanned, String status, String notes) {
        Booking b = new Booking();
        b.id = nextBookingId++;
        b.code = code;
        b.customer = c;
        b.room = r;
        b.checkIn = in.toString();
        b.checkOutPlanned = outPlanned.toString();
        b.actualCheckIn = in.toString() + " 14:00";
        b.status = status;
        b.notes = notes;
        return b;
    }

    private static ServiceItem service(String code, String name, long price) {
        ServiceItem s = new ServiceItem();
        s.id = nextServiceId++;
        s.code = code;
        s.name = name;
        s.price = BigDecimal.valueOf(price);
        s.active = true;
        return s;
    }

    private static Staff staff(String code, String username, String password, String name, String email, String phone, String job, String role, String shift, String status, String date) {
        long id = nextStaffId++;
        return new Staff(id, code, username, password, name, email, phone, job, role, shift, status, date);
    }

    private static Invoice invoice(String code, String bookingCode, Customer c, Room r, long roomFee, long svcFee, long penalty, long discount, LocalDate date, String paymentStatus) {
        Invoice i = new Invoice();
        i.id = nextInvoiceId++;
        i.code = code;
        i.customer = c;
        i.room = r;
        i.roomFee = BigDecimal.valueOf(roomFee);
        i.serviceFee = BigDecimal.valueOf(svcFee);
        i.penaltyFee = BigDecimal.valueOf(penalty);
        i.discountFee = BigDecimal.valueOf(discount);
        i.total = i.roomFee.add(i.serviceFee).add(i.penaltyFee).subtract(i.discountFee);
        i.issuedDate = date.toString();
        i.paymentStatus = paymentStatus;
        return i;
    }

    private static WorkShift workShift(long id, String name, String start, String end) {
        WorkShift w = new WorkShift();
        w.id = id; w.shiftName = name; w.startTime = start; w.endTime = end; w.active = true;
        return w;
    }

    private static ShiftAssignment shiftAssignment(long id, Staff s, WorkShift w, String date) {
        ShiftAssignment sa = new ShiftAssignment();
        sa.id = id; sa.staff = s; sa.workShift = w; sa.workDate = date;
        return sa;
    }

    private static Attendance attendance(long id, ShiftAssignment sa, String in, String out, String status, String note) {
        Attendance a = new Attendance();
        a.id = id; a.shiftAssignment = sa; a.checkInTime = in; a.checkOutTime = out; a.status = status; a.note = note;
        return a;
    }

    private static Room findRoom(String number) {
        return ROOMS.stream().filter(r -> r.number.equals(number)).findFirst().orElse(null);
    }

    // ============== AUTH ==============
    public static synchronized LoginResponse login(String username, String password) {
        sleep();
        Staff s = STAFF.stream()
                .filter(x -> x.username != null && x.username.equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
        if (s != null) {
            if (!s.password.equals(password)) {
                throw new ApiClient.ApiException("Sai tên đăng nhập hoặc mật khẩu.");
            }
            if ("INACTIVE".equalsIgnoreCase(s.status)) {
                throw new ApiClient.ApiException("Tài khoản nhân viên này đã bị khóa.");
            }
            LoginResponse res = new LoginResponse();
            res.token = "mock-token-" + s.id;
            res.userId = s.id;
            res.username = s.username;
            res.fullName = s.fullName;
            res.role = s.role;
            return res;
        }

        if (PASSWORDS.containsKey(username) && PASSWORDS.get(username).equals(password)) {
            UserAccount u = USERS.stream().filter(x -> x.username.equals(username)).findFirst().orElse(null);
            if (u != null) {
                if ("INACTIVE".equals(u.status)) throw new ApiClient.ApiException("Tài khoản đã bị khóa.");
                LoginResponse res = new LoginResponse();
                res.token = "mock-token-" + u.id;
                res.userId = u.id;
                res.username = u.username;
                res.fullName = u.fullName;
                res.role = u.role;
                return res;
            }
        }
        throw new ApiClient.ApiException("Sai tên đăng nhập hoặc mật khẩu.");
    }

    // ============== DASHBOARD ==============
    public static synchronized DashboardStats dashboard() {
        sleep();
        DashboardStats s = new DashboardStats();

        // 1. KPI cards calculation
        s.todayRevenue = BigDecimal.valueOf(1_260_000);
        s.revenueGrowthPercent = 152.0;
        s.todayInvoiceCount = 1;

        long occupied = ROOMS.stream().filter(r -> "DANG_O".equals(r.status)).count();
        long available = ROOMS.stream().filter(r -> "TRONG".equals(r.status)).count();
        long cleaning = ROOMS.stream().filter(r -> "DANG_DON".equals(r.status)).count();
        long maintenance = ROOMS.stream().filter(r -> "BAO_TRI".equals(r.status)).count();

        s.totalRooms = ROOMS.size() > 0 ? ROOMS.size() : 14;
        s.occupiedRooms = occupied > 0 ? occupied : 4;
        s.availableRooms = available > 0 ? available : 6;
        s.cleaningRooms = cleaning > 0 ? cleaning : 3;
        s.maintenanceRooms = maintenance > 0 ? maintenance : 1;
        s.occupancyRate = 31;
        s.readyToWelcome = (int) s.availableRooms;

        s.pendingReceivables = BigDecimal.valueOf(1_282_000);
        s.unpaidInvoicesCount = 2;
        s.upcomingCheckouts = 3;
        s.todayCheckouts = 0;
        s.tomorrowCheckouts = 3;

        // 2. Doanh thu 7 ngày gần nhất
        s.revenue7DaysTotal = BigDecimal.valueOf(5_542_000);
        s.revenue7DaysAverage = BigDecimal.valueOf(791_714);
        s.dailyRevenues = List.of(
                new DashboardStats.DailyRevenueItem("CN 18/10", BigDecimal.ZERO, null, false),
                new DashboardStats.DailyRevenueItem("T2 19/10", BigDecimal.valueOf(550_000), null, false),
                new DashboardStats.DailyRevenueItem("T3 20/10", BigDecimal.valueOf(350_000), null, false),
                new DashboardStats.DailyRevenueItem("T4 21/10", BigDecimal.ZERO, null, false),
                new DashboardStats.DailyRevenueItem("T5 22/10", BigDecimal.valueOf(2_882_000), "2.9 tr", false),
                new DashboardStats.DailyRevenueItem("T6 23/10", BigDecimal.valueOf(500_000), null, false),
                new DashboardStats.DailyRevenueItem("Hôm nay 24/10", BigDecimal.valueOf(1_260_000), "1.3 tr", true)
        );

        // 3. Thu tiền theo phương thức
        s.paymentBreakdowns = List.of(
                new DashboardStats.PaymentMethodItem("Tiền mặt", "1.1 tr", 27, "#38BDF8"),
                new DashboardStats.PaymentMethodItem("MoMo", "850k", 20, "#FB923C"),
                new DashboardStats.PaymentMethodItem("Chuyển khoản", "2.3 tr", 53, "#10B981")
        );

        // 4. Tiền phòng theo loại phòng
        s.roomTypeBreakdowns = List.of(
                new DashboardStats.RoomTypeRevenueItem("Phòng VIP", "2.7 tr", 51),
                new DashboardStats.RoomTypeRevenueItem("Phòng Gia Đình", "1.2 tr", 23),
                new DashboardStats.RoomTypeRevenueItem("Phòng Đôi", "1 tr", 19),
                new DashboardStats.RoomTypeRevenueItem("Phòng Đơn", "350k", 7)
        );

        // 5. Dịch vụ bán chạy
        s.topServices = List.of(
                new DashboardStats.ServiceSalesItem("Ăn sáng", "360k", 34),
                new DashboardStats.ServiceSalesItem("Spa thư giãn", "350k", 33),
                new DashboardStats.ServiceSalesItem("Đưa đón sân bay", "250k", 24),
                new DashboardStats.ServiceSalesItem("Minibar", "60k", 6),
                new DashboardStats.ServiceSalesItem("Giặt ủi", "40k", 4)
        );

        // 6. Lịch hôm nay & ngày mai
        s.upcomingEvents = List.of(
                new DashboardStats.ScheduleEventItem("NHAN_PHONG", "NHẬN PHÒNG", "Phạm Thị Dung", "P106 · Phòng VIP", "Ngày mai"),
                new DashboardStats.ScheduleEventItem("TRA_PHONG", "TRẢ PHÒNG", "Trần Thị Bích", "P102 · Phòng Đơn", "Ngày mai"),
                new DashboardStats.ScheduleEventItem("TRA_PHONG", "TRẢ PHÒNG", "Hoàng Văn Em", "P108 · Phòng Đơn", "Ngày mai"),
                new DashboardStats.ScheduleEventItem("TRA_PHONG", "TRẢ PHÒNG", "Lê Văn Cường", "P205 · Phòng Đôi", "Ngày mai")
        );

        // 7. Hóa đơn gần đây
        s.recentInvoices = List.of(
                new DashboardStats.RecentInvoiceItem("Hoàng Văn Em", "P401", "1.260.000 ₫", "DA_THANH_TOAN", "ĐÃ THANH TOÁN", "24/10/2026"),
                new DashboardStats.RecentInvoiceItem("Nguyễn Văn An", "P105", "500.000 ₫", "CHUA_THANH_TOAN", "CHƯA THANH TOÁN", "23/10/2026"),
                new DashboardStats.RecentInvoiceItem("Phạm Thị Dung", "P310", "1.100.000 ₫", "DA_THANH_TOAN", "ĐÃ THANH TOÁN", "22/10/2026"),
                new DashboardStats.RecentInvoiceItem("Lê Văn Cường", "P201", "1.782.000 ₫", "THANH_TOAN_1_PHAN", "THANH TOÁN 1 PHẦN", "22/10/2026"),
                new DashboardStats.RecentInvoiceItem("Trần Thị Bích", "P103", "350.000 ₫", "DA_THANH_TOAN", "ĐÃ THANH TOÁN", "20/10/2026"),
                new DashboardStats.RecentInvoiceItem("Nguyễn Văn An", "P101", "550.000 ₫", "DA_THANH_TOAN", "ĐÃ THANH TOÁN", "19/10/2026")
        );

        return s;
    }


    // ============== ROOM TYPES ==============
    public static synchronized List<RoomType> roomTypes() { sleep(); return new ArrayList<>(ROOM_TYPES); }

    public static synchronized RoomType createRoomType(String name, BigDecimal price, Integer maxGuests) {
        return createRoomType(null, name, price, maxGuests);
    }

    public static synchronized RoomType createRoomType(String code, String name, BigDecimal price, Integer maxGuests) {
        sleep();
        if (code == null || code.isBlank()) {
            code = String.format("LP%02d", nextRoomTypeId);
        }
        RoomType rt = roomType(code, name, price.longValue(), maxGuests == null ? 2 : maxGuests);
        ROOM_TYPES.add(rt);
        return rt;
    }

    public static synchronized RoomType updateRoomType(Long id, String code, String name, BigDecimal price, Integer maxGuests) {
        sleep();
        RoomType rt = ROOM_TYPES.stream().filter(x -> x.id.equals(id)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy loại phòng."));
        if (code != null && !code.isBlank()) rt.code = code;
        rt.name = name;
        rt.basePrice = price;
        rt.maxGuests = maxGuests;
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
        return createRoom(null, number, roomTypeId, floor, "TRONG", notes);
    }

    public static synchronized Room createRoom(String code, String number, Long roomTypeId, Integer floor, String status, String notes) {
        sleep();
        RoomType type = ROOM_TYPES.stream().filter(t -> t.id.equals(roomTypeId)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy loại phòng."));
        if (code == null || code.isBlank()) {
            code = String.format("P%03d", nextRoomId);
        }
        Room r = room(code, number, type, floor == null ? 1 : floor, status == null ? "TRONG" : status, notes);
        ROOMS.add(r);
        return r;
    }

    public static synchronized Room updateRoom(Long id, String code, String number, Long roomTypeId, Integer floor, String status, String notes) {
        sleep();
        Room r = ROOMS.stream().filter(x -> x.id.equals(id)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy phòng."));
        if (code != null && !code.isBlank()) r.code = code;
        r.number = number;
        if (roomTypeId != null) {
            r.roomType = ROOM_TYPES.stream().filter(t -> t.id.equals(roomTypeId)).findFirst().orElse(r.roomType);
        }
        if (floor != null) r.floor = floor;
        if (status != null) r.status = status;
        r.notes = notes;
        return r;
    }

    public static synchronized Room updateRoomStatus(Long roomId, String status) {
        sleep();
        Room r = ROOMS.stream().filter(x -> x.id.equals(roomId)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy phòng."));
        r.status = status;
        return r;
    }

    public static synchronized void deleteRoom(Long id) {
        sleep();
        Room r = ROOMS.stream().filter(x -> x.id.equals(id)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy phòng."));
        if ("DANG_O".equalsIgnoreCase(r.status)) {
            throw new ApiClient.ApiException("Không thể xóa phòng đang có khách ở.");
        }
        ROOMS.removeIf(x -> x.id.equals(id));
    }

    // ============== CUSTOMERS ==============
    public static synchronized List<Customer> customers(String query) {
        sleep();
        if (query == null || query.isBlank()) return new ArrayList<>(CUSTOMERS);
        String q = query.toLowerCase();
        return CUSTOMERS.stream()
                .filter(c -> (c.fullName != null && c.fullName.toLowerCase().contains(q))
                        || (c.code != null && c.code.toLowerCase().contains(q))
                        || (c.citizenId != null && c.citizenId.toLowerCase().contains(q))
                        || (c.email != null && c.email.toLowerCase().contains(q))
                        || (c.phone != null && c.phone.contains(q)))
                .collect(Collectors.toList());
    }

    public static synchronized Customer createCustomer(String fullName, String phone, String email, String idNumber) {
        return createCustomer(null, fullName, phone, email, idNumber);
    }

    public static synchronized Customer createCustomer(String code, String fullName, String phone, String email, String idNumber) {
        sleep();
        if (code == null || code.isBlank()) {
            code = String.format("KH%03d", nextCustomerId);
        }
        Customer c = customer(code, fullName, phone, idNumber, email);
        CUSTOMERS.add(c);
        return c;
    }

    public static synchronized Customer updateCustomer(Long id, String code, String fullName, String phone, String email, String idNumber) {
        sleep();
        Customer c = CUSTOMERS.stream().filter(x -> x.id.equals(id)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy khách hàng."));
        if (code != null && !code.isBlank()) c.code = code;
        c.fullName = fullName;
        c.phone = phone;
        c.email = email;
        c.citizenId = idNumber;
        return c;
    }

    public static synchronized void deleteCustomer(Long id) {
        sleep();
        CUSTOMERS.removeIf(c -> c.id.equals(id));
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
            customer = customer(String.format("KH%03d", nextCustomerId), newCustomerName.trim(), newCustomerPhone, "", "");
            CUSTOMERS.add(customer);
        }

        String code = String.format("DP%03d", nextBookingId);
        Booking b = booking(code, customer, room, LocalDate.parse(checkIn), LocalDate.parse(checkOutPlanned),
                checkInNow ? "DANG_O" : "DAT_TRUOC", "");
        if (checkInNow) room.status = "DANG_O";
        BOOKINGS.add(b);
        return b;
    }

    public static synchronized Booking checkIn(Long bookingId) {
        sleep();
        Booking b = BOOKINGS.stream().filter(x -> x.id.equals(bookingId)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy lượt đặt phòng."));
        b.status = "DANG_O";
        b.actualCheckIn = LocalDate.now().toString() + " " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        if (b.room != null) b.room.status = "DANG_O";
        return b;
    }

    public static synchronized Booking cancelBooking(Long bookingId) {
        sleep();
        Booking b = BOOKINGS.stream().filter(x -> x.id.equals(bookingId)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy lượt đặt phòng."));
        b.status = "HUY";
        if (b.room != null && "DANG_O".equals(b.room.status)) {
            b.room.status = "TRONG";
        }
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

        String invCode = String.format("HD%03d", nextInvoiceId);
        String bookCode = booking != null ? booking.code : "DP000";
        BigDecimal serviceFee = BigDecimal.ZERO;
        Invoice inv = invoice(invCode, bookCode, customer, room,
                roomFee == null ? 0 : roomFee.longValue(),
                serviceFee.longValue(),
                penaltyFee == null ? 0 : penaltyFee.longValue(),
                0,
                checkOutDate == null ? LocalDate.now() : LocalDate.parse(checkOutDate),
                "DA_THANH_TOAN");
        INVOICES.add(inv);

        if (booking != null) {
            booking.status = "DA_TRA";
            booking.actualCheckOut = LocalDate.now().toString() + " " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        }
        if (room != null) room.status = "DANG_DON";
        return inv;
    }

    public static synchronized void deleteInvoice(Long id) {
        sleep();
        INVOICES.removeIf(i -> i.id.equals(id));
    }

    // ============== SERVICES (danh mục) ==============
    public static synchronized List<ServiceItem> services() { sleep(); return new ArrayList<>(SERVICES); }

    public static synchronized ServiceItem createService(String name, BigDecimal price) {
        return createService(null, name, price);
    }

    public static synchronized ServiceItem createService(String code, String name, BigDecimal price) {
        sleep();
        if (code == null || code.isBlank()) {
            code = String.format("DV%03d", nextServiceId);
        }
        ServiceItem s = service(code, name, price.longValue());
        SERVICES.add(s);
        return s;
    }

    public static synchronized ServiceItem updateService(Long id, String code, String name, BigDecimal price) {
        sleep();
        ServiceItem s = SERVICES.stream().filter(x -> x.id.equals(id)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy dịch vụ."));
        if (code != null && !code.isBlank()) s.code = code;
        s.name = name;
        s.price = price;
        return s;
    }

    public static synchronized void deleteService(Long id) {
        sleep();
        SERVICES.removeIf(s -> s.id.equals(id));
    }

    // ============== STAFF (nhân sự & tài khoản làm chung) ==============
    public static synchronized List<Staff> staff() { sleep(); return new ArrayList<>(STAFF); }

    public static synchronized Staff createStaff(String username, String password, String fullName,
                                                 String email, String phone, String jobTitle,
                                                 String role, String shift) {
        sleep();
        if (username != null && !username.isBlank() && STAFF.stream().anyMatch(s -> username.equalsIgnoreCase(s.username))) {
            throw new ApiClient.ApiException("Tên đăng nhập @" + username + " đã tồn tại.");
        }
        long id = nextStaffId++;
        String code = String.format("TK%03d", id);
        String pwd = (password == null || password.isBlank()) ? "123456" : password;
        String dateStr = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Staff s = new Staff(id, code, username, pwd, fullName, email, phone, jobTitle, role, shift, "ACTIVE", dateStr);
        STAFF.add(s);
        PASSWORDS.put(username, pwd);
        return s;
    }

    public static synchronized Staff updateStaff(Long id, String username, String fullName,
                                                 String email, String phone, String jobTitle,
                                                 String role, String shift) {
        sleep();
        Staff s = STAFF.stream().filter(x -> x.id.equals(id)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy nhân viên."));
        if (username != null && !username.isBlank() && !username.equalsIgnoreCase(s.username)
                && STAFF.stream().anyMatch(x -> username.equalsIgnoreCase(x.username))) {
            throw new ApiClient.ApiException("Tên đăng nhập @" + username + " đã tồn tại.");
        }
        s.username = username;
        s.fullName = fullName;
        s.email = email;
        s.phone = phone;
        s.jobTitle = jobTitle;
        s.role = role;
        s.shift = shift;
        if (s.user != null) {
            s.user.username = username;
            s.user.fullName = fullName;
            s.user.email = email;
            s.user.phone = phone;
            s.user.role = role;
        }
        return s;
    }

    public static synchronized Staff toggleStaffStatus(Long id) {
        sleep();
        Staff s = STAFF.stream().filter(x -> x.id.equals(id)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy nhân viên."));
        s.status = "ACTIVE".equalsIgnoreCase(s.status) ? "INACTIVE" : "ACTIVE";
        if (s.user != null) s.user.status = s.status;
        return s;
    }

    public static synchronized void deleteStaff(Long id) {
        sleep();
        STAFF.removeIf(s -> s.id.equals(id));
    }

    public static synchronized Staff createStaff(String fullName, String jobTitle, String shift, String phone, Long userId) {
        return createStaff("user" + nextStaffId, "123456", fullName, "", phone, jobTitle, "RECEPTION", shift);
    }

    // ============== WORK SHIFTS (ca làm việc) ==============
    public static synchronized List<WorkShift> workShifts() { sleep(); return new ArrayList<>(WORK_SHIFTS); }

    public static synchronized WorkShift createWorkShift(String shiftName, String startTime, String endTime) {
        sleep();
        if (WORK_SHIFTS.stream().anyMatch(w -> w.shiftName.equalsIgnoreCase(shiftName)))
            throw new ApiClient.ApiException("Tên ca \"" + shiftName + "\" đã tồn tại.");
        WorkShift w = workShift(nextWorkShiftId++, shiftName, startTime, endTime);
        WORK_SHIFTS.add(w);
        return w;
    }

    public static synchronized WorkShift updateWorkShift(Long id, String shiftName, String startTime, String endTime) {
        sleep();
        WorkShift w = WORK_SHIFTS.stream().filter(x -> x.id.equals(id)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy ca làm việc."));
        w.shiftName = shiftName;
        w.startTime = startTime;
        w.endTime = endTime;
        return w;
    }

    public static synchronized void deleteWorkShift(Long id) {
        sleep();
        boolean inUse = SHIFT_ASSIGNMENTS.stream().anyMatch(sa -> sa.workShift != null && sa.workShift.id.equals(id));
        if (inUse) throw new ApiClient.ApiException("Không thể xóa — ca này đang được phân công.");
        WORK_SHIFTS.removeIf(w -> w.id.equals(id));
    }

    // ============== SHIFT ASSIGNMENTS (phân ca) ==============
    public static synchronized List<ShiftAssignment> shiftAssignments() { sleep(); return new ArrayList<>(SHIFT_ASSIGNMENTS); }

    public static synchronized ShiftAssignment createShiftAssignment(Long staffId, Long workShiftId, String workDate) {
        sleep();
        Staff s = STAFF.stream().filter(x -> x.id.equals(staffId)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy nhân viên."));
        WorkShift w = WORK_SHIFTS.stream().filter(x -> x.id.equals(workShiftId)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy ca làm việc."));
        ShiftAssignment sa = shiftAssignment(nextShiftAssignmentId++, s, w, workDate);
        SHIFT_ASSIGNMENTS.add(sa);
        return sa;
    }

    public static synchronized void deleteShiftAssignment(Long id) {
        sleep();
        SHIFT_ASSIGNMENTS.removeIf(sa -> sa.id.equals(id));
    }

    // ============== ATTENDANCES (chấm công) ==============
    public static synchronized List<Attendance> attendances() { sleep(); return new ArrayList<>(ATTENDANCES); }

    public static synchronized Attendance createAttendance(Long shiftAssignmentId, String checkIn, String checkOut, String status, String note) {
        sleep();
        ShiftAssignment sa = SHIFT_ASSIGNMENTS.stream().filter(x -> x.id.equals(shiftAssignmentId)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy phân ca."));
        Attendance att = attendance(nextAttendanceId++, sa, checkIn, checkOut, status, note);
        ATTENDANCES.add(att);
        sa.attendance = att;
        return att;
    }

    public static synchronized Attendance updateAttendance(Long id, String checkIn, String checkOut, String status, String note) {
        sleep();
        Attendance att = ATTENDANCES.stream().filter(x -> x.id.equals(id)).findFirst()
                .orElseThrow(() -> new ApiClient.ApiException("Không tìm thấy bản ghi chấm công."));
        att.checkInTime = checkIn;
        att.checkOutTime = checkOut;
        att.status = status;
        att.note = note;
        return att;
    }

    // ============== USERS (tài khoản hệ thống) ==============
    public static synchronized List<UserAccount> users() { sleep(); return new ArrayList<>(USERS); }

    public static synchronized UserAccount createUser(String username, String password, String email, String phone, String role) {
        sleep();
        if (USERS.stream().anyMatch(u -> u.username.equals(username)))
            throw new ApiClient.ApiException("Tên đăng nhập đã tồn tại.");
        UserAccount u = user(nextUserId++, username, "", role);
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
