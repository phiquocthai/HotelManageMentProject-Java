package com.lotusstay.javafx.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO chứa toàn bộ dữ liệu thống kê tổng quan (Dashboard),
 * cấu trúc chuẩn hóa sẵn sàng cho việc gọi REST API backend Spring Boot.
 */
public class DashboardStats {

    // ── 1. KPI Cards ────────────────────────────────────────────────────────
    public BigDecimal todayRevenue = BigDecimal.valueOf(1_260_000);
    public double revenueGrowthPercent = 152.0;
    public List<Double> todaySparkline = List.of(20.0, 35.0, 25.0, 48.0, 38.0, 75.0, 65.0);
    public long todayInvoiceCount = 1;

    public int occupancyRate = 31;
    public long occupiedRooms = 4;
    public long totalRooms = 14;
    public long availableRooms = 6;
    public long cleaningRooms = 3;
    public long maintenanceRooms = 1;

    public BigDecimal pendingReceivables = BigDecimal.valueOf(1_282_000);
    public int unpaidInvoicesCount = 2;

    public int upcomingCheckouts = 3;
    public int todayCheckouts = 0;
    public int tomorrowCheckouts = 3;

    // ── 2. AI Insight (Nhận định hôm nay) ──────────────────────────────────
    public String aiInsightTitle = "Nhận định hôm nay";
    public String aiInsightSub = "Trợ lý đọc dữ liệu phòng, hóa đơn và thanh toán để nêu việc cần chú ý";
    public String aiInsightContent = "Bấm \"Phân tích\" để AI tóm tắt việc cần xử lý, doanh thu, công nợ và cơ hội.";
    public List<String> aiInsightBulletPoints = new ArrayList<>(List.of(
            "Doanh thu hôm nay đạt 1.260.000 ₫ (+152% so với hôm qua) nhờ các giao dịch thanh toán đặt phòng và dịch vụ.",
            "Có 2 hóa đơn còn phải thu tổng 1.282.000 ₫, lễ tân cần chú ý đối soát khi khách thực hiện thủ tục trả phòng.",
            "Công suất phòng hiện tại đạt 31% (4/13 phòng khả dụng). Ngày mai có 3 lượt trả phòng cần điều phối dọn dẹp trước 12:00."
    ));

    // ── 3. Doanh thu 7 ngày gần nhất ────────────────────────────────────────
    public BigDecimal revenue7DaysTotal = BigDecimal.valueOf(5_542_000);
    public BigDecimal revenue7DaysAverage = BigDecimal.valueOf(791_714);
    public List<DailyRevenueItem> dailyRevenues = new ArrayList<>();

    // ── 4. Tình trạng phòng ─────────────────────────────────────────────────
    public int readyToWelcome = 6;

    // ── 5. Thu tiền theo phương thức ────────────────────────────────────────
    public List<PaymentMethodItem> paymentBreakdowns = new ArrayList<>();

    // ── 6. Tiền phòng theo loại phòng ───────────────────────────────────────
    public List<RoomTypeRevenueItem> roomTypeBreakdowns = new ArrayList<>();

    // ── 7. Dịch vụ bán chạy ────────────────────────────────────────────────
    public List<ServiceSalesItem> topServices = new ArrayList<>();

    // ── 8. Lịch hôm nay & ngày mai ─────────────────────────────────────────
    public List<ScheduleEventItem> upcomingEvents = new ArrayList<>();

    // ── 9. Hóa đơn gần đây ─────────────────────────────────────────────────
    public List<RecentInvoiceItem> recentInvoices = new ArrayList<>();

    // ═════════════════════════════════════════════════════════════════════════
    // Nested DTO Classes for Backend Serialization
    // ═════════════════════════════════════════════════════════════════════════

    public static class DailyRevenueItem {
        public String dayLabel; // e.g. "CN 18/10", "T2 19/10", "Hôm nay 24/10"
        public BigDecimal amount;
        public String labelTop; // e.g. "2.9 tr", "1.3 tr" or null
        public boolean isToday;

        public DailyRevenueItem() {}
        public DailyRevenueItem(String dayLabel, BigDecimal amount, String labelTop, boolean isToday) {
            this.dayLabel = dayLabel;
            this.amount = amount;
            this.labelTop = labelTop;
            this.isToday = isToday;
        }
    }

    public static class PaymentMethodItem {
        public String name;
        public String amountText;
        public int percent;
        public String colorHex;

        public PaymentMethodItem() {}
        public PaymentMethodItem(String name, String amountText, int percent, String colorHex) {
            this.name = name;
            this.amountText = amountText;
            this.percent = percent;
            this.colorHex = colorHex;
        }
    }

    public static class RoomTypeRevenueItem {
        public String name;
        public String amountText;
        public int percent;

        public RoomTypeRevenueItem() {}
        public RoomTypeRevenueItem(String name, String amountText, int percent) {
            this.name = name;
            this.amountText = amountText;
            this.percent = percent;
        }
    }

    public static class ServiceSalesItem {
        public String name;
        public String amountText;
        public int percent;

        public ServiceSalesItem() {}
        public ServiceSalesItem(String name, String amountText, int percent) {
            this.name = name;
            this.amountText = amountText;
            this.percent = percent;
        }
    }

    public static class ScheduleEventItem {
        public String type; // "NHAN_PHONG", "TRA_PHONG"
        public String typeLabel; // "NHẬN PHÒNG", "TRẢ PHÒNG"
        public String customerName;
        public String roomInfo; // e.g. "P106 · Phòng VIP"
        public String timeLabel; // e.g. "Ngày mai", "Hôm nay"

        public ScheduleEventItem() {}
        public ScheduleEventItem(String type, String typeLabel, String customerName, String roomInfo, String timeLabel) {
            this.type = type;
            this.typeLabel = typeLabel;
            this.customerName = customerName;
            this.roomInfo = roomInfo;
            this.timeLabel = timeLabel;
        }
    }

    public static class RecentInvoiceItem {
        public String customerName;
        public String roomCode;
        public String totalText;
        public String status; // "DA_THANH_TOAN", "CHUA_THANH_TOAN", "THANH_TOAN_1_PHAN"
        public String statusLabel;
        public String date;

        public RecentInvoiceItem() {}
        public RecentInvoiceItem(String customerName, String roomCode, String totalText, String status, String statusLabel, String date) {
            this.customerName = customerName;
            this.roomCode = roomCode;
            this.totalText = totalText;
            this.status = status;
            this.statusLabel = statusLabel;
            this.date = date;
        }
    }
}

