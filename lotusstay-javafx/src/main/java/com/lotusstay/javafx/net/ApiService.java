package com.lotusstay.javafx.net;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.lotusstay.javafx.model.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ApiService kết nối trực tiếp tới Backend Spring Boot qua ApiClient (REST
 * HTTP).
 * Toàn bộ dữ liệu mẫu (mock seed data) đã được xóa bỏ hoàn toàn.
 */
public class ApiService {

    private static final DateTimeFormatter DATE_VN = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ============== AUTH (ĐĂNG NHẬP) ==============
    public static LoginResponse login(String username, String password) {
        Map<String, Object> req = new HashMap<>();
        req.put("account", username);
        req.put("password", password);
        req.put("rememberMe", false);

        JsonNode root = ApiClient.post("/api/auth/login", req, new TypeReference<JsonNode>() {
        });
        if (root == null) {
            throw new ApiClient.ApiException("Không nhận được phản hồi từ máy chủ.");
        }

        JsonNode resultNode = root.get("result");
        if (resultNode == null || resultNode.isNull()) {
            String msg = root.has("message") ? root.get("message").asText() : "Đăng nhập thất bại.";
            throw new ApiClient.ApiException(msg);
        }

        String token = resultNode.has("token") ? resultNode.get("token").asText() : null;
        JsonNode userNode = resultNode.get("user");

        LoginResponse res = new LoginResponse();
        res.token = token;

        if (userNode != null && !userNode.isNull()) {
            res.userId = userNode.has("id") ? userNode.get("id").asLong() : null;
            res.username = userNode.has("username") ? userNode.get("username").asText() : username;
            res.fullName = userNode.has("fullName") ? userNode.get("fullName").asText() : "";

            if (userNode.has("role")) {
                JsonNode roleNode = userNode.get("role");
                if (roleNode.isObject() && roleNode.has("name")) {
                    res.role = roleNode.get("name").asText();
                } else {
                    res.role = roleNode.asText();
                }
            }
        }

        // Lưu phiên đăng nhập
        Session.set(res.token, res.userId, res.username, res.fullName, res.role);
        return res;
    }

    // ============== DASHBOARD ==============
    public static DashboardStats dashboard() {
        DashboardStats s = new DashboardStats();
        List<Room> roomList = rooms();

        s.todayRevenue = BigDecimal.ZERO;
        s.revenueGrowthPercent = 0.0;
        s.todayInvoiceCount = 0;

        long occupied = roomList.stream().filter(r -> "DANG_O".equalsIgnoreCase(r.status)).count();
        long available = roomList.stream().filter(r -> "TRONG".equalsIgnoreCase(r.status)).count();
        long cleaning = roomList.stream().filter(r -> "DANG_DON".equalsIgnoreCase(r.status)).count();
        long maintenance = roomList.stream()
                .filter(r -> "BAO_TRI".equalsIgnoreCase(r.status) || "DANG_BAO_TRI".equalsIgnoreCase(r.status)).count();

        s.totalRooms = roomList.size();
        s.occupiedRooms = occupied;
        s.availableRooms = available;
        s.cleaningRooms = cleaning;
        s.maintenanceRooms = maintenance;
        s.occupancyRate = s.totalRooms > 0 ? (int) Math.round((occupied * 100.0) / s.totalRooms) : 0;
        s.readyToWelcome = (int) available;

        s.pendingReceivables = BigDecimal.ZERO;
        s.unpaidInvoicesCount = 0;
        s.upcomingCheckouts = 0;
        s.todayCheckouts = 0;
        s.tomorrowCheckouts = 0;

        s.revenue7DaysTotal = BigDecimal.ZERO;
        s.revenue7DaysAverage = BigDecimal.ZERO;
        s.dailyRevenues = Collections.emptyList();
        s.paymentBreakdowns = Collections.emptyList();
        s.roomTypeBreakdowns = Collections.emptyList();
        s.topServices = Collections.emptyList();
        s.upcomingEvents = Collections.emptyList();
        s.recentInvoices = Collections.emptyList();

        return s;
    }

    // ============== ROOM TYPES (LOẠI PHÒNG) ==============
    public static List<RoomType> roomTypes() {
        PageResponse<JsonNode> page = ApiClient.get("/api/room-type?pageNo=1&pageSize=1000",
                new TypeReference<PageResponse<JsonNode>>() {
                });
        if (page == null || page.getContent() == null)
            return new ArrayList<>();

        List<RoomType> list = new ArrayList<>();
        for (JsonNode n : page.getContent()) {
            RoomType rt = new RoomType();
            rt.id = n.has("id") ? n.get("id").asLong() : null;
            rt.code = rt.id != null ? String.format("LP%02d", rt.id) : "";
            rt.name = n.has("name") ? n.get("name").asText() : "";
            rt.basePrice = n.has("basePrice") ? new BigDecimal(n.get("basePrice").asText()) : BigDecimal.ZERO;
            rt.maxGuests = n.has("maxOccupancy") ? n.get("maxOccupancy").asInt() : 2;
            rt.imagePath = n.has("imagePath") ? n.get("imagePath").asText() : null;
            rt.active = !n.has("active") || n.get("active").asBoolean();
            list.add(rt);
        }
        return list;
    }

    public static RoomType createRoomType(String name, BigDecimal price, Integer maxGuests) {
        return createRoomType(null, name, price, maxGuests);
    }

    public static RoomType createRoomType(String code, String name, BigDecimal price, Integer maxGuests) {
        Map<String, Object> req = new HashMap<>();
        req.put("name", name);
        req.put("basePrice", price);
        req.put("maxOccupancy", maxGuests == null ? 2 : maxGuests);

        ApiResponse<JsonNode> resp = ApiClient.post("/api/room-type", req,
                new TypeReference<ApiResponse<JsonNode>>() {
                });
        RoomType rt = new RoomType();
        if (resp != null && resp.result != null) {
            JsonNode n = resp.result;
            rt.id = n.has("id") ? n.get("id").asLong() : null;
            rt.code = rt.id != null ? String.format("LP%02d", rt.id) : (code != null ? code : "LP01");
            rt.name = n.has("name") ? n.get("name").asText() : name;
            rt.basePrice = n.has("basePrice") ? new BigDecimal(n.get("basePrice").asText()) : price;
            rt.maxGuests = n.has("maxOccupancy") ? n.get("maxOccupancy").asInt() : maxGuests;
        }
        return rt;
    }

    public static RoomType updateRoomType(Long id, String code, String name, BigDecimal price, Integer maxGuests) {
        Map<String, Object> req = new HashMap<>();
        req.put("name", name);
        req.put("basePrice", price);
        req.put("maxOccupancy", maxGuests == null ? 2 : maxGuests);

        ApiResponse<JsonNode> resp = ApiClient.put("/api/room-type/" + id, req,
                new TypeReference<ApiResponse<JsonNode>>() {
                });
        RoomType rt = new RoomType();
        rt.id = id;
        rt.code = code != null && !code.isBlank() ? code : String.format("LP%02d", id);
        rt.name = name;
        rt.basePrice = price;
        rt.maxGuests = maxGuests;
        return rt;
    }

    public static void deleteRoomType(Long id) {
        ApiClient.delete("/api/room-type/" + id);
    }

    // ============== ROOMS (PHÒNG) ==============
    public static List<Room> rooms() {
        PageResponse<JsonNode> page = ApiClient.get("/api/rooms?pageNo=1&pageSize=1000",
                new TypeReference<PageResponse<JsonNode>>() {
                });
        if (page == null || page.getContent() == null)
            return new ArrayList<>();

        List<Room> list = new ArrayList<>();
        for (JsonNode n : page.getContent()) {
            Room r = new Room();
            r.id = n.has("id") ? n.get("id").asLong() : null;
            r.number = n.has("roomNumber") ? n.get("roomNumber").asText() : "";
            r.code = "P" + r.number;
            r.floor = n.has("floor") ? n.get("floor").asInt() : 1;
            r.status = mapBackendRoomStatus(n.has("status") ? n.get("status").asText() : "Trống");
            r.notes = n.has("note") ? n.get("note").asText() : "";
            r.imagePath = n.has("imagePath") ? n.get("imagePath").asText() : null;
            r.active = !n.has("active") || n.get("active").asBoolean();

            RoomType rt = new RoomType();
            rt.name = n.has("roomTypeName") ? n.get("roomTypeName").asText() : "Tiêu chuẩn";
            r.roomType = rt;

            list.add(r);
        }
        return list;
    }

    public static Room createRoom(String number, Long roomTypeId, Integer floor, String notes) {
        return createRoom(null, number, roomTypeId, floor, "TRONG", notes);
    }

    public static Room createRoom(String code, String number, Long roomTypeId, Integer floor, String status,
            String notes) {
        Map<String, Object> req = new HashMap<>();
        req.put("roomNumber", number);
        req.put("roomTypeId", roomTypeId);
        req.put("floor", floor == null ? 1 : floor);
        req.put("status", mapFeRoomStatusToVietnamese(status));
        req.put("note", notes != null ? notes : "");

        ApiResponse<JsonNode> resp = ApiClient.post("/api/rooms", req,
                new TypeReference<ApiResponse<JsonNode>>() {
                });

        Room r = new Room();
        r.number = number;
        r.code = code != null && !code.isBlank() ? code : "P" + number;
        r.floor = floor != null ? floor : 1;
        r.status = status != null ? status : "TRONG";
        r.notes = notes;
        if (resp != null && resp.result != null && resp.result.has("id")) {
            r.id = resp.result.get("id").asLong();
        }
        return r;
    }

    public static Room updateRoom(Long id, String code, String number, Long roomTypeId, Integer floor, String status,
            String notes) {
        Map<String, Object> req = new HashMap<>();
        req.put("roomNumber", number);
        req.put("roomTypeId", roomTypeId);
        req.put("floor", floor == null ? 1 : floor);
        req.put("status", mapFeRoomStatusToVietnamese(status));
        req.put("note", notes != null ? notes : "");

        ApiClient.put("/api/rooms/" + id, req, new TypeReference<ApiResponse<JsonNode>>() {
        });

        Room r = new Room();
        r.id = id;
        r.number = number;
        r.code = code != null && !code.isBlank() ? code : "P" + number;
        r.floor = floor != null ? floor : 1;
        r.status = status;
        r.notes = notes;
        return r;
    }

    public static Room updateRoomStatus(Long roomId, String status) {
        // Lấy thông tin phòng hiện tại
        ApiResponse<JsonNode> current = ApiClient.get("/api/rooms/" + roomId,
                new TypeReference<ApiResponse<JsonNode>>() {
                });
        if (current != null && current.result != null) {
            JsonNode n = current.result;
            Map<String, Object> req = new HashMap<>();
            req.put("roomNumber", n.has("roomNumber") ? n.get("roomNumber").asText() : "");
            // Lưu ý: nếu cần roomTypeId từ RoomType, ta có thể giữ nguyên hoặc tìm
            req.put("floor", n.has("floor") ? n.get("floor").asInt() : 1);
            req.put("status", mapFeRoomStatusToVietnamese(status));
            req.put("note", n.has("note") ? n.get("note").asText() : "");
            ApiClient.put("/api/rooms/" + roomId, req, new TypeReference<ApiResponse<JsonNode>>() {
            });
        }
        Room r = new Room();
        r.id = roomId;
        r.status = status;
        return r;
    }

    public static void deleteRoom(Long id) {
        ApiClient.delete("/api/rooms/" + id);
    }

    // ============== CUSTOMERS (KHÁCH HÀNG) ==============
    public static PageResponse<Customer> customerPage(int pageNo, int pageSize, String query) {
        String path;
        int safePage = Math.max(1, pageNo);
        int safeSize = Math.max(1, pageSize);
        if (query == null || query.isBlank()) {
            path = "/api/customer?pageNo=" + safePage + "&pageSize=" + safeSize + "&sortBy=id&sortDir=asc";
        } else {
            String q = query.trim();
            String enc = java.net.URLEncoder.encode(q, java.nio.charset.StandardCharsets.UTF_8);
            if (q.contains("@")) {
                path = "/api/customer/search?pageNo=" + safePage + "&pageSize=" + safeSize
                        + "&sortBy=id&sortDir=asc&email=" + enc;
            } else if (q.matches("\\d{10,12}")) {
                if (q.length() == 12) {
                    path = "/api/customer/search?pageNo=" + safePage + "&pageSize=" + safeSize
                            + "&sortBy=id&sortDir=asc&citizenId=" + enc;
                } else {
                    path = "/api/customer/search?pageNo=" + safePage + "&pageSize=" + safeSize
                            + "&sortBy=id&sortDir=asc&phoneNumber=" + enc;
                }
            } else {
                path = "/api/customer/search?pageNo=" + safePage + "&pageSize=" + safeSize
                        + "&sortBy=id&sortDir=asc&fullName=" + enc;
            }
        }
        PageResponse<JsonNode> page = ApiClient.get(path, new TypeReference<PageResponse<JsonNode>>() {
        });
        PageResponse<Customer> res = new PageResponse<>();
        if (page == null) {
            res.pageNo = safePage;
            res.pageSize = safeSize;
            res.totalElements = 0;
            res.totalPages = 1;
            res.last = true;
            res.content = new ArrayList<>();
            return res;
        }

        res.pageNo = page.pageNo;
        res.pageSize = page.pageSize;
        res.totalElements = page.totalElements;
        res.totalPages = Math.max(1, page.totalPages);
        res.last = page.last;

        List<Customer> list = new ArrayList<>();
        if (page.getContent() != null) {
            for (JsonNode n : page.getContent()) {
                Customer c = new Customer();
                c.id = n.has("id") ? n.get("id").asLong() : null;
                c.fullName = n.has("fullName") ? n.get("fullName").asText() : "";
                c.citizenId = n.has("citizenId") ? n.get("citizenId").asText() : "";
                c.email = n.has("email") ? n.get("email").asText() : "";
                c.phone = n.has("phoneNumber") ? n.get("phoneNumber").asText() : "";
                c.imagePath = n.has("imagePath") ? n.get("imagePath").asText() : null;
                c.active = !n.has("active") || n.get("active").asBoolean();
                list.add(c);
            }
        }
        res.content = list;
        return res;
    }

    public static List<Customer> customers(String query) {
        PageResponse<Customer> page = customerPage(1, 1000, query);
        List<Customer> list = page.getContent();
        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase();
            return list.stream()
                    .filter(c -> (c.fullName != null && c.fullName.toLowerCase().contains(q))
                            || (c.code != null && c.code.toLowerCase().contains(q))
                            || (c.citizenId != null && c.citizenId.toLowerCase().contains(q))
                            || (c.email != null && c.email.toLowerCase().contains(q))
                            || (c.phone != null && c.phone.contains(q)))
                    .collect(Collectors.toList());
        }
        return list;
    }

    public static Customer createCustomer(String fullName, String phone, String email, String idNumber) {
        return createCustomer(null, fullName, phone, email, idNumber);
    }

    public static Customer createCustomer(String code, String fullName, String phone, String email, String idNumber) {
        Map<String, Object> req = new HashMap<>();
        req.put("fullName", fullName);
        req.put("citizenId", idNumber != null && !idNumber.isBlank() ? idNumber : "000000000000");
        req.put("email", email != null && !email.isBlank() ? email : "khachhang@hotel.vn");
        req.put("phoneNumber", phone != null && !phone.isBlank() ? phone : "0900000000");

        ApiResponse<JsonNode> resp = ApiClient.post("/api/customer", req,
                new TypeReference<ApiResponse<JsonNode>>() {
                });

        Customer c = new Customer();
        c.fullName = fullName;
        c.phone = phone;
        c.email = email;
        c.citizenId = idNumber;
        if (resp != null && resp.result != null && resp.result.has("id")) {
            c.id = resp.result.get("id").asLong();
            c.code = String.format("KH%03d", c.id);
        }
        return c;
    }

    public static Customer updateCustomer(Long id, String code, String fullName, String phone, String email,
            String idNumber) {
        Map<String, Object> req = new HashMap<>();
        req.put("fullName", fullName);
        req.put("citizenId", idNumber != null && !idNumber.isBlank() ? idNumber : "000000000000");
        req.put("email", email != null && !email.isBlank() ? email : "khachhang@hotel.vn");
        req.put("phoneNumber", phone != null && !phone.isBlank() ? phone : "0900000000");

        ApiClient.put("/api/customer/" + id, req, new TypeReference<ApiResponse<JsonNode>>() {
        });

        Customer c = new Customer();
        c.id = id;
        c.code = code != null && !code.isBlank() ? code : String.format("KH%03d", id);
        c.fullName = fullName;
        c.phone = phone;
        c.email = email;
        c.citizenId = idNumber;
        return c;
    }

    public static void deleteCustomer(Long id) {
        ApiClient.delete("/api/customer/" + id);
    }

    // ============== SERVICES (DỊCH VỤ) ==============
    public static List<ServiceItem> services() {
        PageResponse<JsonNode> page = ApiClient.get("/api/services?pageNo=1&pageSize=1000",
                new TypeReference<PageResponse<JsonNode>>() {
                });
        if (page == null || page.getContent() == null)
            return new ArrayList<>();

        List<ServiceItem> list = new ArrayList<>();
        for (JsonNode n : page.getContent()) {
            ServiceItem s = new ServiceItem();
            s.id = n.has("id") ? n.get("id").asLong() : null;
            s.code = s.id != null ? String.format("DV%03d", s.id) : "";
            s.name = n.has("name") ? n.get("name").asText() : "";
            s.price = n.has("price") ? new BigDecimal(n.get("price").asText()) : BigDecimal.ZERO;
            s.imagePath = n.has("imagePath") ? n.get("imagePath").asText() : null;
            s.active = !n.has("active") || n.get("active").asBoolean();
            list.add(s);
        }
        return list;
    }

    public static ServiceItem createService(String name, BigDecimal price) {
        return createService(null, name, price);
    }

    public static ServiceItem createService(String code, String name, BigDecimal price) {
        Map<String, Object> req = new HashMap<>();
        req.put("name", name);
        req.put("price", price);
        req.put("active", true);

        ApiResponse<JsonNode> resp = ApiClient.post("/api/services", req,
                new TypeReference<ApiResponse<JsonNode>>() {
                });

        ServiceItem s = new ServiceItem();
        s.name = name;
        s.price = price;
        if (resp != null && resp.result != null && resp.result.has("id")) {
            s.id = resp.result.get("id").asLong();
            s.code = String.format("DV%03d", s.id);
        }
        return s;
    }

    public static ServiceItem updateService(Long id, String code, String name, BigDecimal price) {
        Map<String, Object> req = new HashMap<>();
        req.put("name", name);
        req.put("price", price);
        req.put("active", true);

        ApiClient.put("/api/services/" + id, req, new TypeReference<ApiResponse<JsonNode>>() {
        });

        ServiceItem s = new ServiceItem();
        s.id = id;
        s.code = code != null && !code.isBlank() ? code : String.format("DV%03d", id);
        s.name = name;
        s.price = price;
        return s;
    }

    public static void deleteService(Long id) {
        ApiClient.delete("/api/services/" + id);
    }

    // ============== PROMOTIONS (KHUYẾN MÃI) ==============
    public static List<Promotion> promotions() {
        PageResponse<JsonNode> page = ApiClient.get("/api/promotions?pageNo=1&pageSize=1000&sortBy=id&sortDir=asc",
                new TypeReference<PageResponse<JsonNode>>() {
                });
        if (page == null || page.getContent() == null)
            return new ArrayList<>();

        List<Promotion> list = new ArrayList<>();
        for (JsonNode n : page.getContent()) {
            Promotion p = new Promotion();
            p.id = n.has("id") && !n.get("id").isNull() ? n.get("id").asLong() : null;
            p.code = n.has("code") && !n.get("code").isNull() ? n.get("code").asText() : "";
            p.name = n.has("name") && !n.get("name").isNull() ? n.get("name").asText() : "";
            p.discountType = n.has("discountType") && !n.get("discountType").isNull() ? n.get("discountType").asText()
                    : "PHAN_TRAM";
            p.discountValue = n.has("discountValue") && !n.get("discountValue").isNull()
                    ? new BigDecimal(n.get("discountValue").asText())
                    : BigDecimal.ZERO;
            if (n.has("startDate") && !n.get("startDate").isNull()) {
                try {
                    p.startDate = LocalDate.parse(n.get("startDate").asText());
                } catch (Exception ignored) {
                }
            }
            if (n.has("endDate") && !n.get("endDate").isNull()) {
                try {
                    p.endDate = LocalDate.parse(n.get("endDate").asText());
                } catch (Exception ignored) {
                }
            }
            p.active = !n.has("active") || n.get("active").asBoolean();
            list.add(p);
        }
        return list;
    }

    public static Promotion createPromotion(String code, String name, String discountType, BigDecimal discountValue,
            LocalDate startDate, LocalDate endDate, Boolean active) {
        Map<String, Object> req = new HashMap<>();
        req.put("code", code);
        req.put("name", name);
        req.put("discountType", discountType != null ? discountType : "PHAN_TRAM");
        req.put("discountValue", discountValue != null ? discountValue : BigDecimal.ZERO);
        req.put("startDate", startDate != null ? startDate.toString() : LocalDate.now().toString());
        req.put("endDate", endDate != null ? endDate.toString() : LocalDate.now().plusMonths(1).toString());
        req.put("active", active != null ? active : true);

        ApiResponse<JsonNode> resp = ApiClient.post("/api/promotions", req,
                new TypeReference<ApiResponse<JsonNode>>() {
                });

        Promotion p = new Promotion();
        p.code = code;
        p.name = name;
        p.discountType = discountType;
        p.discountValue = discountValue;
        p.startDate = startDate;
        p.endDate = endDate;
        p.active = active;
        if (resp != null && resp.result != null) {
            JsonNode rNode = resp.result.has("promotion") ? resp.result.get("promotion") : resp.result;
            if (rNode.has("id")) {
                p.id = rNode.get("id").asLong();
            }
        }
        return p;
    }

    public static Promotion updatePromotion(Long id, String code, String name, String discountType,
            BigDecimal discountValue,
            LocalDate startDate, LocalDate endDate, Boolean active) {
        Map<String, Object> req = new HashMap<>();
        req.put("code", code);
        req.put("name", name);
        req.put("discountType", discountType != null ? discountType : "PHAN_TRAM");
        req.put("discountValue", discountValue != null ? discountValue : BigDecimal.ZERO);
        req.put("startDate", startDate != null ? startDate.toString() : LocalDate.now().toString());
        req.put("endDate", endDate != null ? endDate.toString() : LocalDate.now().plusMonths(1).toString());
        req.put("active", active != null ? active : true);

        ApiClient.put("/api/promotions/" + id, req, new TypeReference<ApiResponse<JsonNode>>() {
        });

        Promotion p = new Promotion();
        p.id = id;
        p.code = code;
        p.name = name;
        p.discountType = discountType;
        p.discountValue = discountValue;
        p.startDate = startDate;
        p.endDate = endDate;
        p.active = active;
        return p;
    }

    public static void deletePromotion(Long id) {
        ApiClient.delete("/api/promotions/" + id);
    }

    // ============== PAYMENT METHODS (PHƯƠNG THỨC THANH TOÁN) ==============
    public static List<PaymentMethodItem> paymentMethods() {
        PageResponse<JsonNode> page = ApiClient.get("/api/payment-methods?pageNo=1&pageSize=1000&sortBy=id&sortDir=asc",
                new TypeReference<PageResponse<JsonNode>>() {
                });
        if (page == null || page.getContent() == null)
            return new ArrayList<>();

        List<PaymentMethodItem> list = new ArrayList<>();
        for (JsonNode n : page.getContent()) {
            PaymentMethodItem pm = new PaymentMethodItem();
            pm.id = n.has("id") && !n.get("id").isNull() ? n.get("id").asLong() : null;
            pm.name = n.has("name") && !n.get("name").isNull() ? n.get("name").asText() : "";
            pm.qrImage = n.has("qrImage") && !n.get("qrImage").isNull() ? n.get("qrImage").asText() : null;
            pm.bankName = n.has("bankName") && !n.get("bankName").isNull() ? n.get("bankName").asText() : null;
            pm.accountNumber = n.has("accountNumber") && !n.get("accountNumber").isNull()
                    ? n.get("accountNumber").asText()
                    : null;
            pm.accountHolderName = n.has("accountHolderName") && !n.get("accountHolderName").isNull()
                    ? n.get("accountHolderName").asText()
                    : null;
            pm.active = !n.has("active") || n.get("active").asBoolean();
            list.add(pm);
        }
        return list;
    }

    public static PaymentMethodItem createPaymentMethod(String name, String bankName, String accountNumber,
            String accountHolderName, String qrImage, Boolean active) {
        Map<String, Object> req = new HashMap<>();
        req.put("name", name);
        req.put("bankName", bankName != null ? bankName : "");
        req.put("accountNumber", accountNumber != null ? accountNumber : "");
        req.put("accountHolderName", accountHolderName != null ? accountHolderName : "");
        req.put("qrImage", qrImage != null ? qrImage : "");
        req.put("active", active != null ? active : true);

        ApiResponse<JsonNode> resp = ApiClient.post("/api/payment-methods", req,
                new TypeReference<ApiResponse<JsonNode>>() {
                });

        PaymentMethodItem pm = new PaymentMethodItem();
        pm.name = name;
        pm.bankName = bankName;
        pm.accountNumber = accountNumber;
        pm.accountHolderName = accountHolderName;
        pm.qrImage = qrImage;
        pm.active = active;
        if (resp != null && resp.result != null) {
            JsonNode rNode = resp.result.has("paymentMethod") ? resp.result.get("paymentMethod") : resp.result;
            if (rNode.has("id")) {
                pm.id = rNode.get("id").asLong();
            }
        }
        return pm;
    }

    public static PaymentMethodItem updatePaymentMethod(Long id, String name, String bankName, String accountNumber,
            String accountHolderName, String qrImage, Boolean active) {
        Map<String, Object> req = new HashMap<>();
        req.put("name", name);
        req.put("bankName", bankName != null ? bankName : "");
        req.put("accountNumber", accountNumber != null ? accountNumber : "");
        req.put("accountHolderName", accountHolderName != null ? accountHolderName : "");
        req.put("qrImage", qrImage != null ? qrImage : "");
        req.put("active", active != null ? active : true);

        ApiClient.put("/api/payment-methods/" + id, req, new TypeReference<ApiResponse<JsonNode>>() {
        });

        PaymentMethodItem pm = new PaymentMethodItem();
        pm.id = id;
        pm.name = name;
        pm.bankName = bankName;
        pm.accountNumber = accountNumber;
        pm.accountHolderName = accountHolderName;
        pm.qrImage = qrImage;
        pm.active = active;
        return pm;
    }

    public static void deletePaymentMethod(Long id) {
        ApiClient.delete("/api/payment-methods/" + id);
    }

    // ============== STAFF (NHÂN SỰ) ==============
    public static List<Staff> staff() {
        PageResponse<JsonNode> page = ApiClient.get("/api/staff?pageNo=1&pageSize=1000",
                new TypeReference<PageResponse<JsonNode>>() {
                });
        if (page == null || page.getContent() == null)
            return new ArrayList<>();

        List<Staff> list = new ArrayList<>();
        for (JsonNode n : page.getContent()) {
            Staff s = new Staff();
            s.id = n.hasNonNull("id") ? n.get("id").asLong() : null;
            s.code = s.id != null ? String.format("TK%03d", s.id) : "";
            s.username = n.hasNonNull("username") ? n.get("username").asText() : "";
            s.fullName = n.hasNonNull("fullName") ? n.get("fullName").asText() : "";
            s.email = n.hasNonNull("email") ? n.get("email").asText() : "";
            s.phone = n.hasNonNull("phoneNumber") ? n.get("phoneNumber").asText() : "";
            s.jobTitle = n.hasNonNull("jobTitle") ? n.get("jobTitle").asText() : "";

            if (n.hasNonNull("role")) {
                JsonNode rNode = n.get("role");
                String rText = rNode.isObject() && rNode.hasNonNull("name") ? rNode.get("name").asText() : rNode.asText();
                s.role = (rText != null && !rText.isBlank() && !"null".equalsIgnoreCase(rText)) ? rText : null;
            }
            s.role = Staff.resolveRole(s.role, s.username);

            boolean active = !n.has("active") || n.get("active").asBoolean();
            s.status = active ? "ACTIVE" : "INACTIVE";
            s.shift = null;
            s.createdAt = n.has("createdAt")
                    ? n.get("createdAt").asText().substring(0, Math.min(10, n.get("createdAt").asText().length()))
                    : "";

            UserAccount u = new UserAccount();
            u.id = s.id;
            u.username = s.username;
            u.fullName = s.fullName;
            u.email = s.email;
            u.phone = s.phone;
            u.role = s.role;
            u.status = s.status;
            s.user = u;

            list.add(s);
        }
        return list;
    }

    public static Staff createStaff(String username, String password, String fullName,
            String email, String phone, String jobTitle,
            String role, String shift) {
        Map<String, Object> req = new HashMap<>();
        req.put("username", username);
        req.put("password", password != null && !password.isBlank() ? password : "password123");
        req.put("fullName", fullName);
        req.put("email", email != null && !email.isBlank() ? email : username + "@hotel.vn");
        req.put("phoneNumber", phone != null && !phone.isBlank() ? phone : "0900000000");
        req.put("jobTitle", jobTitle != null && !jobTitle.isBlank() ? jobTitle : "Nhân viên");
        req.put("role", role != null ? role.toUpperCase() : "RECEPTION");
        req.put("active", true);

        ApiResponse<JsonNode> resp = ApiClient.post("/api/staff", req,
                new TypeReference<ApiResponse<JsonNode>>() {
                });

        Staff s = new Staff();
        s.username = username;
        s.fullName = fullName;
        s.email = email;
        s.phone = phone;
        s.jobTitle = jobTitle;
        s.role = role;
        s.shift = shift;
        s.status = "ACTIVE";
        if (resp != null && resp.result != null && resp.result.has("id")) {
            s.id = resp.result.get("id").asLong();
            s.code = String.format("TK%03d", s.id);
        }
        return s;
    }

    public static Staff updateStaff(Long id, String username, String fullName,
            String email, String phone, String jobTitle,
            String role, String shift) {
        Map<String, Object> req = new HashMap<>();
        req.put("username", username);
        req.put("fullName", fullName);
        req.put("email", email != null && !email.isBlank() ? email : username + "@hotel.vn");
        req.put("phoneNumber", phone != null && !phone.isBlank() ? phone : "0900000000");
        req.put("jobTitle", jobTitle != null && !jobTitle.isBlank() ? jobTitle : "Nhân viên");
        req.put("role", role != null ? role.toUpperCase() : "RECEPTION");
        req.put("active", true);

        ApiClient.put("/api/staff/" + id, req, new TypeReference<ApiResponse<JsonNode>>() {
        });

        Staff s = new Staff();
        s.id = id;
        s.code = String.format("TK%03d", id);
        s.username = username;
        s.fullName = fullName;
        s.email = email;
        s.phone = phone;
        s.jobTitle = jobTitle;
        s.role = role;
        s.shift = shift;
        s.status = "ACTIVE";
        return s;
    }

    public static Staff toggleStaffStatus(Long id) {
        ApiResponse<JsonNode> current = ApiClient.get("/api/staff/" + id, new TypeReference<ApiResponse<JsonNode>>() {
        });
        if (current != null && current.result != null) {
            JsonNode n = current.result;
            boolean active = !n.has("active") || n.get("active").asBoolean();
            Map<String, Object> req = new HashMap<>();
            req.put("username", n.has("username") ? n.get("username").asText() : "");
            req.put("fullName", n.has("fullName") ? n.get("fullName").asText() : "");
            req.put("email", n.has("email") ? n.get("email").asText() : "");
            req.put("phoneNumber", n.has("phoneNumber") ? n.get("phoneNumber").asText() : "0900000000");
            req.put("jobTitle", n.has("jobTitle") ? n.get("jobTitle").asText() : "Nhân viên");
            req.put("role",
                    n.has("role")
                            ? (n.get("role").isObject() ? n.get("role").get("name").asText() : n.get("role").asText())
                            : "RECEPTION");
            req.put("active", !active);

            ApiClient.put("/api/staff/" + id, req, new TypeReference<ApiResponse<JsonNode>>() {
            });

            Staff s = new Staff();
            s.id = id;
            s.status = !active ? "ACTIVE" : "INACTIVE";
            return s;
        }
        return null;
    }

    public static void deleteStaff(Long id) {
        ApiClient.delete("/api/staff/" + id);
    }

    public static Staff createStaff(String fullName, String jobTitle, String shift, String phone, Long userId) {
        return createStaff("user" + System.currentTimeMillis() % 10000, "123456", fullName, "", phone, jobTitle,
                "RECEPTION", shift);
    }

    // ============== WORK SHIFTS (CA LÀM VIỆC) ==============
    public static List<WorkShift> workShifts() {
        PageResponse<JsonNode> page = ApiClient.get("/api/workshift?pageNo=1&pageSize=1000",
                new TypeReference<PageResponse<JsonNode>>() {
                });
        if (page == null || page.getContent() == null)
            return new ArrayList<>();

        List<WorkShift> list = new ArrayList<>();
        for (JsonNode n : page.getContent()) {
            WorkShift ws = new WorkShift();
            ws.id = n.has("id") ? n.get("id").asLong() : null;

            if (n.has("name") && !n.get("name").isNull() && !n.get("name").asText().trim().isEmpty()) {
                ws.shiftName = n.get("name").asText().trim();
            } else if (n.has("shiftName") && !n.get("shiftName").isNull()
                    && !n.get("shiftName").asText().trim().isEmpty()) {
                ws.shiftName = n.get("shiftName").asText().trim();
            } else if (n.has("tenCa") && !n.get("tenCa").isNull()) {
                ws.shiftName = n.get("tenCa").asText().trim();
            } else {
                ws.shiftName = "";
            }

            ws.startTime = n.has("startTime") ? formatTimeNode(n.get("startTime")) : "06:00";
            ws.endTime = n.has("endTime") ? formatTimeNode(n.get("endTime")) : "14:00";
            ws.active = !n.has("active") || n.get("active").asBoolean();
            list.add(ws);
        }
        return list;
    }

    public static WorkShift createWorkShift(String shiftName, String startTime, String endTime) {
        Map<String, Object> req = new HashMap<>();
        req.put("name", shiftName);
        req.put("shiftName", shiftName);
        req.put("startTime", ensureTimeSeconds(startTime));
        req.put("endTime", ensureTimeSeconds(endTime));

        ApiResponse<JsonNode> resp = ApiClient.post("/api/workshift", req,
                new TypeReference<ApiResponse<JsonNode>>() {
                });

        WorkShift ws = new WorkShift();
        ws.shiftName = shiftName;
        ws.startTime = startTime;
        ws.endTime = endTime;
        ws.active = true;
        if (resp != null && resp.result != null) {
            JsonNode r = resp.result;
            if (r.has("Ca làm việc:")) {
                r = r.get("Ca làm việc:");
            }
            if (r.has("id")) {
                ws.id = r.get("id").asLong();
            }
        }
        return ws;
    }

    public static WorkShift updateWorkShift(Long id, String shiftName, String startTime, String endTime) {
        Map<String, Object> req = new HashMap<>();
        req.put("name", shiftName);
        req.put("shiftName", shiftName);
        req.put("startTime", ensureTimeSeconds(startTime));
        req.put("endTime", ensureTimeSeconds(endTime));

        ApiClient.put("/api/workshift/" + id, req, new TypeReference<ApiResponse<JsonNode>>() {
        });

        WorkShift ws = new WorkShift();
        ws.id = id;
        ws.shiftName = shiftName;
        ws.startTime = startTime;
        ws.endTime = endTime;
        ws.active = true;
        return ws;
    }

    public static void deleteWorkShift(Long id) {
        ApiClient.delete("/api/workshift/" + id);
    }

    // ============== SHIFT ASSIGNMENTS (PHÂN CA) ==============
    public static PageResponse<ShiftAssignment> shiftAssignmentPage(int pageNo, int pageSize, String staffName,
            Long workShiftId, String workDate) {
        int safePage = Math.max(1, pageNo);
        int safeSize = Math.max(1, pageSize);
        boolean hasFilter = (staffName != null && !staffName.isBlank())
                || workShiftId != null
                || (workDate != null && !workDate.isBlank());

        String basePath = hasFilter ? "/api/shift-assignments/search" : "/api/shift-assignments";
        StringBuilder sb = new StringBuilder(basePath).append("?pageNo=").append(safePage)
                .append("&pageSize=").append(safeSize)
                .append("&sortBy=id&sortDir=asc");

        if (staffName != null && !staffName.isBlank()) {
            sb.append("&staffName=")
                    .append(java.net.URLEncoder.encode(staffName.trim(), java.nio.charset.StandardCharsets.UTF_8));
        }
        if (workShiftId != null) {
            sb.append("&workShiftId=").append(workShiftId);
        }
        if (workDate != null && !workDate.isBlank()) {
            sb.append("&workDate=").append(formatToIsoDate(workDate.trim()));
        }

        PageResponse<JsonNode> page = ApiClient.get(sb.toString(),
                new TypeReference<PageResponse<JsonNode>>() {
                });

        PageResponse<ShiftAssignment> res = new PageResponse<>();
        if (page == null) {
            res.pageNo = safePage;
            res.pageSize = safeSize;
            res.totalElements = 0;
            res.totalPages = 1;
            res.last = true;
            res.content = new ArrayList<>();
            return res;
        }

        res.pageNo = page.pageNo;
        res.pageSize = page.pageSize;
        res.totalElements = page.totalElements;
        res.totalPages = Math.max(1, page.totalPages);
        res.last = page.last;

        List<Attendance> attendanceList = attendances();
        List<ShiftAssignment> list = new ArrayList<>();
        if (page.getContent() != null) {
            for (JsonNode n : page.getContent()) {
                ShiftAssignment sa = new ShiftAssignment();
                sa.id = n.has("id") ? n.get("id").asLong() : null;

                Staff s = new Staff();
                if (n.has("staffId") && !n.get("staffId").isNull()) {
                    s.id = n.get("staffId").asLong();
                }
                s.fullName = n.has("staffName") ? n.get("staffName").asText() : "";
                sa.staff = s;

                WorkShift ws = new WorkShift();
                if (n.has("workShiftId") && !n.get("workShiftId").isNull()) {
                    ws.id = n.get("workShiftId").asLong();
                }
                ws.shiftName = n.has("shiftName") ? n.get("shiftName").asText() : "";
                ws.startTime = n.has("startTime") ? formatTimeNode(n.get("startTime")) : "";
                ws.endTime = n.has("endTime") ? formatTimeNode(n.get("endTime")) : "";
                sa.workShift = ws;

                String dateStr = n.has("workDate") ? n.get("workDate").asText() : "";
                sa.workDate = formatToVnDate(dateStr);

                sa.attendance = attendanceList.stream()
                        .filter(a -> a.shiftAssignment != null && Objects.equals(a.shiftAssignment.id, sa.id))
                        .findFirst()
                        .orElse(null);

                list.add(sa);
            }
        }
        res.content = list;
        return res;
    }

    public static List<ShiftAssignment> shiftAssignments() {
        PageResponse<ShiftAssignment> p = shiftAssignmentPage(1, 1000, null, null, null);
        return p != null && p.getContent() != null ? p.getContent() : new ArrayList<>();
    }

    public static ShiftAssignment createShiftAssignment(Long staffId, Long workShiftId, String workDate) {
        Map<String, Object> req = new HashMap<>();
        req.put("staffId", staffId);
        req.put("workShiftId", workShiftId);
        req.put("workDate", formatToIsoDate(workDate));

        ApiResponse<JsonNode> resp = ApiClient.post("/api/shift-assignments", req,
                new TypeReference<ApiResponse<JsonNode>>() {
                });

        ShiftAssignment sa = new ShiftAssignment();
        sa.workDate = workDate;
        if (resp != null && resp.result != null) {
            JsonNode n = resp.result.has("shiftAssignment") ? resp.result.get("shiftAssignment") : resp.result;
            if (n.has("id"))
                sa.id = n.get("id").asLong();
        }
        return sa;
    }

    public static ShiftAssignment updateShiftAssignment(Long id, Long staffId, Long workShiftId, String workDate) {
        Map<String, Object> req = new HashMap<>();
        req.put("staffId", staffId);
        req.put("workShiftId", workShiftId);
        req.put("workDate", formatToIsoDate(workDate));

        ApiResponse<JsonNode> resp = ApiClient.put("/api/shift-assignments/" + id, req,
                new TypeReference<ApiResponse<JsonNode>>() {
                });

        ShiftAssignment sa = new ShiftAssignment();
        sa.id = id;
        sa.workDate = workDate;
        if (resp != null && resp.result != null) {
            JsonNode n = resp.result.has("shiftAssignment") ? resp.result.get("shiftAssignment") : resp.result;
            if (n.has("id"))
                sa.id = n.get("id").asLong();
        }
        return sa;
    }

    public static void deleteShiftAssignment(Long id) {
        ApiClient.delete("/api/shift-assignments/" + id);
    }

    // ============== ATTENDANCES (CHẤM CÔNG) ==============
    public static List<Attendance> attendances() {
        PageResponse<JsonNode> page = ApiClient.get("/api/attendances?pageNo=1&pageSize=1000",
                new TypeReference<PageResponse<JsonNode>>() {
                });
        if (page == null || page.getContent() == null)
            return new ArrayList<>();

        List<Attendance> list = new ArrayList<>();
        for (JsonNode n : page.getContent()) {
            list.add(parseAttendanceNode(n));
        }
        return list;
    }

    public static PageResponse<Attendance> attendancePage(
            int pageNo, int pageSize, String staffName, Long workShiftId, String workDate, String status) {
        StringBuilder url = new StringBuilder(
                "/api/attendances/search?pageNo=" + pageNo + "&pageSize=" + pageSize + "&sortBy=id&sortDir=asc");
        if (staffName != null && !staffName.isBlank()) {
            url.append("&staffName=").append(URLEncoder.encode(staffName.trim(), StandardCharsets.UTF_8));
        }
        if (workShiftId != null) {
            url.append("&workShiftId=").append(workShiftId);
        }
        if (workDate != null && !workDate.isBlank()) {
            url.append("&workDate=").append(workDate.trim());
        }
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            url.append("&status=")
                    .append(URLEncoder.encode(mapFeAttendanceStatusToVietnamese(status), StandardCharsets.UTF_8));
        }

        PageResponse<JsonNode> page = ApiClient.get(url.toString(), new TypeReference<PageResponse<JsonNode>>() {
        });
        if (page == null)
            return new PageResponse<>();

        PageResponse<Attendance> res = new PageResponse<>();
        res.pageNo = page.pageNo;
        res.pageSize = page.pageSize;
        res.totalElements = page.totalElements;
        res.totalPages = page.totalPages;
        res.last = page.last;

        List<Attendance> list = new ArrayList<>();
        if (page.getContent() != null) {
            for (JsonNode n : page.getContent()) {
                list.add(parseAttendanceNode(n));
            }
        }
        res.content = list;
        return res;
    }

    private static Attendance parseAttendanceNode(JsonNode n) {
        Attendance a = new Attendance();
        a.id = n.has("id") && !n.get("id").isNull() ? n.get("id").asLong() : null;
        a.checkInTime = n.has("checkInTime") && !n.get("checkInTime").isNull()
                ? formatTime(n.get("checkInTime").asText())
                : null;
        a.checkOutTime = n.has("checkOutTime") && !n.get("checkOutTime").isNull()
                ? formatTime(n.get("checkOutTime").asText())
                : null;
        a.status = n.has("status") && !n.get("status").isNull() ? mapBackendAttendanceStatus(n.get("status").asText())
                : "DU_CONG";
        a.note = n.has("note") && !n.get("note").isNull() ? n.get("note").asText() : "";

        ShiftAssignment sa = new ShiftAssignment();
        if (n.has("shiftAssignmentId") && !n.get("shiftAssignmentId").isNull()) {
            sa.id = n.get("shiftAssignmentId").asLong();
        }
        Staff st = new Staff();
        st.fullName = n.has("staffName") && !n.get("staffName").isNull() ? n.get("staffName").asText() : "";
        sa.staff = st;

        WorkShift ws = new WorkShift();
        if (n.has("workShiftName") && !n.get("workShiftName").isNull()) {
            ws.shiftName = n.get("workShiftName").asText();
        } else if (n.has("shiftName") && !n.get("shiftName").isNull()) {
            ws.shiftName = n.get("shiftName").asText();
        }
        sa.workShift = ws;

        if (n.has("workDate") && !n.get("workDate").isNull()) {
            sa.workDate = n.get("workDate").asText();
        }
        a.shiftAssignment = sa;
        return a;
    }

    public static Attendance createAttendance(Long shiftAssignmentId, String checkIn, String checkOut, String status,
            String note) {
        Map<String, Object> req = new HashMap<>();
        req.put("shiftAssignmentId", shiftAssignmentId);
        if (checkIn != null && !checkIn.isBlank())
            req.put("checkInTime", ensureTimeSeconds(checkIn));
        if (checkOut != null && !checkOut.isBlank())
            req.put("checkOutTime", ensureTimeSeconds(checkOut));
        req.put("status", mapFeAttendanceStatusToVietnamese(status));
        req.put("note", note != null ? note : "");

        ApiResponse<JsonNode> resp = ApiClient.post("/api/attendances", req,
                new TypeReference<ApiResponse<JsonNode>>() {
                });

        Attendance a = new Attendance();
        a.checkInTime = checkIn;
        a.checkOutTime = checkOut;
        a.status = status;
        a.note = note;
        if (resp != null && resp.result != null) {
            JsonNode n = resp.result.has("attendance") ? resp.result.get("attendance") : resp.result;
            if (n.has("id"))
                a.id = n.get("id").asLong();
        }
        return a;
    }

    public static Attendance updateAttendance(Long id, String checkIn, String checkOut, String status, String note) {
        Map<String, Object> req = new HashMap<>();
        if (checkIn != null && !checkIn.isBlank())
            req.put("checkInTime", ensureTimeSeconds(checkIn));
        if (checkOut != null && !checkOut.isBlank())
            req.put("checkOutTime", ensureTimeSeconds(checkOut));
        req.put("status", mapFeAttendanceStatusToVietnamese(status));
        req.put("note", note != null ? note : "");

        ApiClient.put("/api/attendances/" + id, req, new TypeReference<ApiResponse<JsonNode>>() {
        });

        Attendance a = new Attendance();
        a.id = id;
        a.checkInTime = checkIn;
        a.checkOutTime = checkOut;
        a.status = status;
        a.note = note;
        return a;
    }

    public static void deleteAttendance(Long id) {
        ApiClient.delete("/api/attendances/" + id);
    }

    // ============== USERS (TÀI KHOẢN HỆ THỐNG) ==============
    public static List<UserAccount> users() {
        return staff().stream().map(s -> s.user).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static UserAccount createUser(String username, String password, String email, String phone, String role) {
        Staff s = createStaff(username, password, username, email, phone, "Nhân viên", role, "Toàn thời gian");
        return s.user;
    }

    public static UserAccount toggleUserStatus(Long id) {
        Staff s = toggleStaffStatus(id);
        return s != null ? s.user : null;
    }

    // ============== BOOKINGS (ĐẶT PHÒNG & LƯU TRÚ) ==============
    public static List<Booking> bookings() {
        try {
            PageResponse<JsonNode> page = ApiClient.get("/api/booking?pageNo=1&pageSize=1000&sortBy=id&sortDir=desc",
                    new TypeReference<PageResponse<JsonNode>>() {
                    });
            if (page == null || page.getContent() == null)
                return new ArrayList<>();

            List<Booking> list = new ArrayList<>();
            for (JsonNode n : page.getContent()) {
                Booking b = new Booking();
                b.id = n.hasNonNull("id") ? n.get("id").asLong() : null;
                b.code = b.id != null ? String.format("DP%03d", b.id) : "DP000";

                Customer c = new Customer();
                c.id = n.hasNonNull("customerId") ? n.get("customerId").asLong() : null;
                c.fullName = n.hasNonNull("customerName") ? n.get("customerName").asText() : "";
                c.phone = n.hasNonNull("customerPhone") ? n.get("customerPhone").asText() : "";
                c.code = c.id != null ? String.format("KH%03d", c.id) : "";
                b.customer = c;

                Room r = new Room();
                r.id = n.hasNonNull("roomId") ? n.get("roomId").asLong() : null;
                r.number = n.hasNonNull("roomNumber") ? n.get("roomNumber").asText() : "";
                r.code = "P" + r.number;
                RoomType rt = new RoomType();
                rt.name = n.hasNonNull("roomTypeName") ? n.get("roomTypeName").asText() : "Tiêu chuẩn";
                r.roomType = rt;
                b.room = r;

                b.checkIn = n.hasNonNull("checkInDate") ? formatToVnDate(n.get("checkInDate").asText()) : "—";
                b.checkOutPlanned = n.hasNonNull("expectedCheckOutDate")
                        ? formatToVnDate(n.get("expectedCheckOutDate").asText())
                        : "—";
                b.actualCheckIn = n.hasNonNull("actualCheckInTime") ? formatDateTimeNode(n.get("actualCheckInTime"))
                        : null;
                b.actualCheckOut = n.hasNonNull("actualCheckOutTime") ? formatDateTimeNode(n.get("actualCheckOutTime"))
                        : null;
                b.numberOfGuests = n.hasNonNull("numberOfGuests") ? n.get("numberOfGuests").asInt() : 1;
                b.status = n.hasNonNull("status") ? n.get("status").asText() : "DAT_TRUOC";
                b.notes = n.hasNonNull("note") ? n.get("note").asText() : "";

                list.add(b);
            }
            return list;
        } catch (Exception ex) {
            System.err.println("Lỗi khi tải danh sách đặt phòng: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public static Booking createBooking(Long customerId, String newCustomerName, String newCustomerPhone,
            Long roomId, String checkIn, String checkOutPlanned, boolean checkInNow) {
        return createBooking(customerId, newCustomerName, newCustomerPhone, null, roomId, checkIn, checkOutPlanned, 1,
                "", checkInNow);
    }

    public static Booking createBooking(Long customerId, String newCustomerName, String newCustomerPhone,
            String newCustomerCitizenId,
            Long roomId, String checkIn, String checkOutPlanned, Integer guests, String notes, boolean checkInNow) {
        Long effectiveCustomerId = customerId;
        if (effectiveCustomerId == null && newCustomerName != null && !newCustomerName.isBlank()) {
            Customer c = createCustomer(newCustomerName.trim(),
                    newCustomerPhone != null ? newCustomerPhone.trim() : "",
                    "guest." + System.currentTimeMillis() % 100000 + "@hotel.vn",
                    newCustomerCitizenId != null && !newCustomerCitizenId.isBlank() ? newCustomerCitizenId.trim()
                            : "000000000000");
            effectiveCustomerId = c.id;
        }

        if (effectiveCustomerId == null) {
            throw new ApiClient.ApiException("Vui lòng chọn hoặc nhập thông tin khách hàng.");
        }

        Map<String, Object> req = new HashMap<>();
        req.put("customerId", effectiveCustomerId);
        req.put("roomId", roomId);
        req.put("checkInDate", formatToIsoDate(checkIn));
        req.put("expectedCheckOutDate", formatToIsoDate(checkOutPlanned));
        req.put("numberOfGuests", guests != null && guests > 0 ? guests : 1);
        req.put("note", notes != null ? notes : "");
        req.put("status", checkInNow ? "DANG_O" : "DAT_TRUOC");

        ApiResponse<JsonNode> resp = ApiClient.post("/api/booking", req,
                new TypeReference<ApiResponse<JsonNode>>() {
                });

        Booking b = new Booking();
        if (resp != null && resp.result != null) {
            JsonNode n = resp.result;
            b.id = n.hasNonNull("id") ? n.get("id").asLong() : null;
            b.code = b.id != null ? String.format("DP%03d", b.id) : "DP000";
            b.status = n.hasNonNull("status") ? n.get("status").asText() : (checkInNow ? "DANG_O" : "DAT_TRUOC");
        }
        return b;
    }

    public static Booking checkIn(Long bookingId) {
        ApiResponse<JsonNode> resp = ApiClient.post("/api/booking/" + bookingId + "/check-in", Collections.emptyMap(),
                new TypeReference<ApiResponse<JsonNode>>() {
                });
        Booking b = new Booking();
        b.id = bookingId;
        b.status = "DANG_O";
        return b;
    }

    public static Booking checkOutBooking(Long bookingId) {
        ApiResponse<JsonNode> resp = ApiClient.post("/api/booking/" + bookingId + "/check-out", Collections.emptyMap(),
                new TypeReference<ApiResponse<JsonNode>>() {
                });
        Booking b = new Booking();
        b.id = bookingId;
        b.status = "DA_TRA";
        return b;
    }

    public static Booking cancelBooking(Long bookingId) {
        ApiResponse<JsonNode> resp = ApiClient.post("/api/booking/" + bookingId + "/cancel", Collections.emptyMap(),
                new TypeReference<ApiResponse<JsonNode>>() {
                });
        Booking b = new Booking();
        b.id = bookingId;
        b.status = "DA_HUY";
        return b;
    }

    public static void deleteBooking(Long bookingId) {
        ApiClient.delete("/api/booking/" + bookingId);
    }

    // ============== INVOICES (HÓA ĐƠN) ==============
    public static List<Invoice> invoices() {
        return new ArrayList<>();
    }

    public static Invoice checkout(Long bookingId, Long roomId, Long customerId, String checkOutDate,
            BigDecimal roomFee, BigDecimal penaltyFee, List<Map<String, Object>> services) {
        if (roomId != null) {
            updateRoomStatus(roomId, "DANG_DON");
        }
        Invoice inv = new Invoice();
        inv.code = "HD" + System.currentTimeMillis() % 1000;
        inv.paymentStatus = "DA_THANH_TOAN";
        inv.total = (roomFee != null ? roomFee : BigDecimal.ZERO)
                .add(penaltyFee != null ? penaltyFee : BigDecimal.ZERO);
        return inv;
    }

    public static void deleteInvoice(Long id) {
        // Endpoint xóa hóa đơn nếu có
    }

    // ============== HELPERS CHUYỂN ĐỔI DỮ LIỆU ==============
    private static String mapBackendRoomStatus(String status) {
        if (status == null)
            return "TRONG";
        return switch (status.trim().toLowerCase()) {
            case "đang ở", "dang_o" -> "DANG_O";
            case "đang dọn", "dang_don" -> "DANG_DON";
            case "đang bảo trì", "bảo trì", "bao_tri", "dang_bao_tri" -> "BAO_TRI";
            default -> "TRONG";
        };
    }

    private static String mapFeRoomStatusToVietnamese(String feStatus) {
        if (feStatus == null)
            return "Trống";
        return switch (feStatus.toUpperCase()) {
            case "DANG_O" -> "Đang ở";
            case "DANG_DON" -> "Đang dọn";
            case "BAO_TRI", "DANG_BAO_TRI" -> "Đang bảo trì";
            default -> "Trống";
        };
    }

    private static String mapBackendAttendanceStatus(String status) {
        if (status == null)
            return "DU_CONG";
        return switch (status.trim().toLowerCase()) {
            case "đi trễ", "di_tre" -> "DI_TRE";
            case "về sớm", "ve_som" -> "VE_SOM";
            case "vắng mặt", "vang_mat" -> "VANG_MAT";
            default -> "DU_CONG";
        };
    }

    private static String mapFeAttendanceStatusToVietnamese(String feStatus) {
        if (feStatus == null)
            return "Đủ công";
        return switch (feStatus.toUpperCase()) {
            case "DI_TRE" -> "Đi trễ";
            case "VE_SOM" -> "Về sớm";
            case "VANG_MAT" -> "Vắng mặt";
            default -> "Đủ công";
        };
    }

    private static String formatDateTimeNode(JsonNode node) {
        if (node == null || node.isNull())
            return null;
        if (node.isArray() && node.size() >= 5) {
            int y = node.get(0).asInt();
            int m = node.get(1).asInt();
            int d = node.get(2).asInt();
            int h = node.get(3).asInt();
            int min = node.get(4).asInt();
            return String.format("%02d:%02d %02d/%02d/%04d", h, min, d, m, y);
        }
        String txt = node.asText();
        if (txt == null || txt.isBlank())
            return null;
        if (txt.contains("T")) {
            String[] parts = txt.split("T");
            String datePart = formatToVnDate(parts[0]);
            String timePart = parts.length > 1 && parts[1].length() >= 5 ? parts[1].substring(0, 5) : "";
            return timePart.isEmpty() ? datePart : timePart + " " + datePart;
        }
        return txt;
    }

    private static String formatTimeNode(JsonNode node) {
        if (node == null || node.isNull())
            return "";
        if (node.isArray() && node.size() >= 2) {
            int h = node.get(0).asInt();
            int m = node.get(1).asInt();
            return String.format("%02d:%02d", h, m);
        }
        return formatTime(node.asText());
    }

    private static String formatTime(String time) {
        if (time == null || time.isBlank())
            return "";
        String t = time.trim();
        if (t.contains("T")) {
            String[] parts = t.split("T");
            if (parts.length > 1)
                t = parts[1];
        }
        if (t.length() >= 5)
            return t.substring(0, 5);
        return t;
    }

    private static String ensureTimeSeconds(String time) {
        if (time == null || time.isBlank())
            return "00:00:00";
        String t = time.trim();
        if (t.length() == 5)
            return t + ":00";
        return t;
    }

    private static String formatToVnDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank())
            return "";
        try {
            if (dateStr.contains("-")) {
                LocalDate d = LocalDate.parse(dateStr, DATE_ISO);
                return d.format(DATE_VN);
            }
        } catch (Exception ignored) {
        }
        return dateStr;
    }

    private static String formatToIsoDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank())
            return LocalDate.now().toString();
        try {
            if (dateStr.contains("/")) {
                LocalDate d = LocalDate.parse(dateStr, DATE_VN);
                return d.format(DATE_ISO);
            }
        } catch (Exception ignored) {
        }
        return dateStr;
    }

    // ============== BOOKING SERVICE DETAILS (CHI TIẾT DỊCH VỤ PHÒNG) ==============
    public static PageResponse<BookingServiceDetail> bookingServiceDetailsPage(int pageNo, int pageSize, Long bookingId) {
        int safePage = Math.max(1, pageNo);
        int safeSize = Math.max(1, pageSize);
        String path = "/api/booking-service-details?pageNo=" + safePage + "&pageSize=" + safeSize + "&sortBy=id&sortDir=desc";
        if (bookingId != null) {
            path += "&bookingId=" + bookingId;
        }

        try {
            PageResponse<JsonNode> page = ApiClient.get(path, new TypeReference<PageResponse<JsonNode>>() {
            });

            PageResponse<BookingServiceDetail> res = new PageResponse<>();
            if (page == null) {
                res.pageNo = safePage;
                res.pageSize = safeSize;
                res.totalElements = 0;
                res.totalPages = 1;
                res.last = true;
                res.content = new ArrayList<>();
                return res;
            }

            res.pageNo = page.pageNo;
            res.pageSize = page.pageSize;
            res.totalElements = page.totalElements;
            res.totalPages = Math.max(1, page.totalPages);
            res.last = page.last;

            List<BookingServiceDetail> list = new ArrayList<>();
            if (page.getContent() != null) {
                for (JsonNode n : page.getContent()) {
                    list.add(mapBookingServiceDetail(n));
                }
            }
            res.content = list;
            return res;
        } catch (ApiClient.ApiException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("404")) {
                PageResponse<BookingServiceDetail> empty = new PageResponse<>();
                empty.pageNo = safePage;
                empty.pageSize = safeSize;
                empty.totalElements = 0;
                empty.totalPages = 1;
                empty.last = true;
                empty.content = new ArrayList<>();
                return empty;
            }
            throw ex;
        }
    }

    public static List<BookingServiceDetail> bookingServiceDetailsByBooking(Long bookingId) {
        if (bookingId == null) return new ArrayList<>();
        try {
            ApiResponse<JsonNode> resp = ApiClient.get("/api/booking-service-details/booking/" + bookingId,
                    new TypeReference<ApiResponse<JsonNode>>() {
                    });
            List<BookingServiceDetail> list = new ArrayList<>();
            if (resp != null && resp.result != null && resp.result.has("details")) {
                JsonNode arr = resp.result.get("details");
                if (arr.isArray()) {
                    for (JsonNode n : arr) {
                        list.add(mapBookingServiceDetail(n));
                    }
                }
            }
            return list;
        } catch (ApiClient.ApiException ex) {
            return new ArrayList<>();
        }
    }

    public static BookingServiceDetail createBookingServiceDetail(Long bookingId, Long serviceId, int quantity, BigDecimal unitPrice) {
        Map<String, Object> req = new HashMap<>();
        req.put("bookingId", bookingId);
        req.put("serviceId", serviceId);
        req.put("quantity", quantity);
        req.put("unitPrice", unitPrice);

        try {
            ApiResponse<JsonNode> resp = ApiClient.post("/api/booking-service-details", req,
                    new TypeReference<ApiResponse<JsonNode>>() {
                    });

            if (resp != null && resp.result != null && resp.result.has("detail")) {
                return mapBookingServiceDetail(resp.result.get("detail"));
            }
            return null;
        } catch (ApiClient.ApiException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("404")) {
                throw new ApiClient.ApiException("API dịch vụ phòng chưa được kích hoạt trên server. Vui lòng Rerun/Khởi động lại Backend (HotelManagementProjectApplication) trong IntelliJ!");
            }
            throw ex;
        }
    }

    public static BookingServiceDetail updateBookingServiceDetail(Long id, Long bookingId, Long serviceId, int quantity, BigDecimal unitPrice) {
        Map<String, Object> req = new HashMap<>();
        req.put("bookingId", bookingId);
        req.put("serviceId", serviceId);
        req.put("quantity", quantity);
        req.put("unitPrice", unitPrice);

        try {
            ApiResponse<JsonNode> resp = ApiClient.put("/api/booking-service-details/" + id, req,
                    new TypeReference<ApiResponse<JsonNode>>() {
                    });

            if (resp != null && resp.result != null && resp.result.has("detail")) {
                return mapBookingServiceDetail(resp.result.get("detail"));
            }
            return null;
        } catch (ApiClient.ApiException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("404")) {
                throw new ApiClient.ApiException("API dịch vụ phòng chưa được kích hoạt trên server. Vui lòng Rerun/Khởi động lại Backend (HotelManagementProjectApplication) trong IntelliJ!");
            }
            throw ex;
        }
    }

    public static void deleteBookingServiceDetail(Long id) {
        try {
            ApiClient.delete("/api/booking-service-details/" + id);
        } catch (ApiClient.ApiException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("404")) {
                throw new ApiClient.ApiException("API dịch vụ phòng chưa được kích hoạt trên server. Vui lòng Rerun/Khởi động lại Backend (HotelManagementProjectApplication) trong IntelliJ!");
            }
            throw ex;
        }
    }

    private static BookingServiceDetail mapBookingServiceDetail(JsonNode n) {
        BookingServiceDetail d = new BookingServiceDetail();
        d.id = n.has("id") && !n.get("id").isNull() ? n.get("id").asLong() : null;
        d.bookingId = n.has("bookingId") && !n.get("bookingId").isNull() ? n.get("bookingId").asLong() : null;
        d.roomNumber = n.has("roomNumber") && !n.get("roomNumber").isNull() ? n.get("roomNumber").asText() : "";
        d.customerName = n.has("customerName") && !n.get("customerName").isNull() ? n.get("customerName").asText() : "";
        d.serviceId = n.has("serviceId") && !n.get("serviceId").isNull() ? n.get("serviceId").asLong() : null;
        d.serviceName = n.has("serviceName") && !n.get("serviceName").isNull() ? n.get("serviceName").asText() : "";
        d.quantity = n.has("quantity") && !n.get("quantity").isNull() ? n.get("quantity").asInt() : 1;
        d.unitPrice = n.has("unitPrice") && !n.get("unitPrice").isNull()
                ? new BigDecimal(n.get("unitPrice").asText()) : BigDecimal.ZERO;
        d.totalPrice = n.has("totalPrice") && !n.get("totalPrice").isNull()
                ? new BigDecimal(n.get("totalPrice").asText()) : d.unitPrice.multiply(BigDecimal.valueOf(d.quantity));
        d.usedAt = n.has("usedAt") ? formatDateTimeNode(n.get("usedAt")) : "";
        return d;
    }
}
