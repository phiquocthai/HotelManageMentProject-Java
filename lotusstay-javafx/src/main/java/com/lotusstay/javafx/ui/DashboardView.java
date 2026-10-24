package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.DashboardStats;
import com.lotusstay.javafx.net.ApiService;
import com.lotusstay.javafx.net.Session;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * DashboardView — Giao diện Tổng quan chuẩn Luxury Dark Theme.
 * Thiết kế chi tiết theo đúng thiết kế tham chiếu:
 *  - 4 Thẻ KPI đa tầng (Doanh thu có Sparkline, Công suất có Progress đa phân đoạn, Còn phải thu, Trả phòng)
 *  - Trợ lý AI Insight ("Nhận định hôm nay") kèm nút Phân tích thông minh
 *  - Biểu đồ cột Doanh thu 7 ngày gần nhất & Biểu đồ Donut Tình trạng phòng
 *  - 3 Thẻ phân tích thanh toán, loại phòng và dịch vụ bán chạy
 *  - Lịch nhận/trả phòng hôm nay & ngày mai kèm Bảng hóa đơn gần đây
 */
public class DashboardView {

    private static MainShell appShell;

    public static ScrollPane build() {
        return build(null);
    }

    public static ScrollPane build(MainShell shell) {
        appShell = shell;

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20, 24, 32, 24));
        contentBox.setStyle("-fx-background-color: #0A0D14;");

        // ── 1. Hero Header ────────────────────────────────────────────────────
        contentBox.getChildren().add(buildHeroHeader());

        // ── Placeholder Container for Async Data ──────────────────────────────
        VBox dynamicContainer = new VBox(20);
        Label loadingLabel = new Label("Đang tải dữ liệu tổng quan theo thời gian thực…");
        loadingLabel.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 13px; -fx-font-family: 'Inter', sans-serif; -fx-padding: 16 0;");
        dynamicContainer.getChildren().add(loadingLabel);
        contentBox.getChildren().add(dynamicContainer);

        // Load data asynchronously
        Util.runAsync(ApiService::dashboard, stats -> {
            dynamicContainer.getChildren().clear();

            // 2. 4 KPI Cards Grid (Equal 25% width, no text truncation)
            dynamicContainer.getChildren().add(buildKpiCardsRow(stats));

            // 3. AI Insight Banner
            dynamicContainer.getChildren().add(buildAiInsightCard(stats));

            // 4. Middle Analytics Row (7-day Revenue & Room Status Donut)
            dynamicContainer.getChildren().add(buildMiddleChartsRow(stats));

            // 5. Unified 3-Column Grid (Breakdowns Row & Bottom Row aligned strictly)
            dynamicContainer.getChildren().add(buildBreakdownAndBottomGrid(stats));
        });

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: #0A0D14; -fx-background: #0A0D14; -fx-border-color: transparent;");
        return scroll;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 1. HERO HEADER
    // ═════════════════════════════════════════════════════════════════════════
    private static HBox buildHeroHeader() {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        String greeting = "Chào buổi tối";
        int hour = LocalTime.now().getHour();
        if (hour >= 5 && hour < 12) greeting = "Chào buổi sáng";
        else if (hour >= 12 && hour < 18) greeting = "Chào buổi chiều";

        String fullName = Session.getFullName();
        String displayName = (fullName != null && !fullName.isBlank()) ? fullName : "Quản Trị Hệ Thống";

        VBox titleCol = new VBox(4);
        Label title = new Label(greeting + ", " + displayName);
        title.setStyle("-fx-text-fill: #FFFFFF; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 22px; -fx-font-weight: 800;");

        String todayFormatted = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Label subtitle = new Label("Hôm nay " + todayFormatted + " · tình hình hoạt động của LotusStay");
        subtitle.setStyle("-fx-text-fill: #8C9CB2; -fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px;");
        titleCol.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action Buttons matching Screenshot
        Button btnViewRooms = new Button("Xem sơ đồ phòng");
        btnViewRooms.setStyle("-fx-background-color: #121824; -fx-border-color: #2D3A4F; -fx-border-radius: 8; -fx-background-radius: 8; " +
                "-fx-text-fill: #FFFFFF; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 12.5px; -fx-font-weight: 700; " +
                "-fx-padding: 8 16; -fx-cursor: hand;");
        btnViewRooms.setOnMouseEntered(e -> btnViewRooms.setStyle("-fx-background-color: #1A2332; -fx-border-color: #3B4B64; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #FFFFFF; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 12.5px; -fx-font-weight: 700; -fx-padding: 8 16; -fx-cursor: hand;"));
        btnViewRooms.setOnMouseExited(e -> btnViewRooms.setStyle("-fx-background-color: #121824; -fx-border-color: #2D3A4F; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #FFFFFF; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 12.5px; -fx-font-weight: 700; -fx-padding: 8 16; -fx-cursor: hand;"));
        btnViewRooms.setOnAction(e -> {
            if (appShell != null) appShell.goTo("rooms");
        });

        Button btnNewBooking = new Button("+ Đặt phòng mới");
        btnNewBooking.setStyle("-fx-background-color: #F59E0B; -fx-border-radius: 8; -fx-background-radius: 8; " +
                "-fx-text-fill: #0A0D14; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 12.5px; -fx-font-weight: 800; " +
                "-fx-padding: 8 18; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.4), 12, 0, 0, 2);");
        btnNewBooking.setOnMouseEntered(e -> btnNewBooking.setStyle("-fx-background-color: #FBBF24; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #0A0D14; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 12.5px; -fx-font-weight: 800; -fx-padding: 8 18; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.6), 16, 0, 0, 3);"));
        btnNewBooking.setOnMouseExited(e -> btnNewBooking.setStyle("-fx-background-color: #F59E0B; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #0A0D14; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 12.5px; -fx-font-weight: 800; -fx-padding: 8 18; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.4), 12, 0, 0, 2);"));
        btnNewBooking.setOnAction(e -> {
            if (appShell != null) appShell.goTo("bookings");
        });

        HBox actions = new HBox(10, btnViewRooms, btnNewBooking);
        actions.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(titleCol, spacer, actions);
        return row;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 2. 4 KPI CARDS GRID (Strict 25% per card, refined typography, no ellipsis)
    // ═════════════════════════════════════════════════════════════════════════
    private static GridPane buildKpiCardsRow(DashboardStats stats) {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25.0);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }

        // Card 1: Doanh thu hôm nay
        VBox card1 = createBaseKpiCard();
        HBox top1 = createKpiTop("DOANH THU HÔM NAY", IconUtils.icon("coin", 13, Color.web("#F59E0B")), "rgba(245, 158, 11, 0.15)");
        Label val1 = createKpiValue(Util.money(stats.todayRevenue));

        HBox sub1 = new HBox(5);
        sub1.setAlignment(Pos.CENTER_LEFT);
        Label growth = new Label("▲ " + (long) stats.revenueGrowthPercent + "%");
        growth.setStyle("-fx-text-fill: #10B981; -fx-font-size: 11px; -fx-font-weight: 700; -fx-font-family: 'Inter', sans-serif;");
        growth.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        Label vsYesterday = new Label("so với hôm qua");
        vsYesterday.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 10.5px; -fx-font-family: 'Inter', sans-serif;");
        vsYesterday.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        sub1.getChildren().addAll(growth, vsYesterday);

        Node sparkline = createSparkline(stats.todaySparkline);
        card1.getChildren().addAll(top1, val1, sub1, sparkline);

        // Card 2: Công suất phòng
        VBox card2 = createBaseKpiCard();
        HBox top2 = createKpiTop("CÔNG SUẤT PHÒNG", IconUtils.icon("bed", 13, Color.web("#EAB308")), "rgba(234, 179, 8, 0.15)");
        Label val2 = createKpiValue(stats.occupancyRate + "%");
        long usable = stats.totalRooms - stats.maintenanceRooms;
        Label sub2 = createKpiSub(stats.occupiedRooms + " đang ở / " + usable + " phòng khả dụng");
        sub2.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 10.5px; -fx-font-family: 'Inter', sans-serif;");
        Node multiSegment = createMultiSegmentBar(stats.occupiedRooms, stats.availableRooms, stats.cleaningRooms, stats.maintenanceRooms);
        card2.getChildren().addAll(top2, val2, sub2, multiSegment);

        // Card 3: Còn phải thu
        VBox card3 = createBaseKpiCard();
        HBox top3 = createKpiTop("CÒN PHẢI THU", IconUtils.icon("wallet", 13, Color.web("#F59E0B")), "rgba(245, 158, 11, 0.12)");
        Label val3 = createKpiValue(Util.money(stats.pendingReceivables));
        Label sub3 = createKpiSub(stats.unpaidInvoicesCount + " hóa đơn chưa thu đủ");
        sub3.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 11px; -fx-font-family: 'Inter', sans-serif;");
        Region dummy3 = new Region();
        dummy3.setPrefHeight(20);
        card3.getChildren().addAll(top3, val3, sub3, dummy3);

        // Card 4: Trả phòng sắp tới
        VBox card4 = createBaseKpiCard();
        HBox top4 = createKpiTop("TRẢ PHÒNG SẮP TỚI", IconUtils.icon("door-open", 13, Color.web("#38BDF8")), "rgba(56, 189, 248, 0.12)");
        Label val4 = createKpiValue(String.valueOf(stats.upcomingCheckouts));
        Label sub4 = createKpiSub(stats.todayCheckouts + " hôm nay · " + stats.tomorrowCheckouts + " ngày mai");
        sub4.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 10.5px; -fx-font-family: 'Inter', sans-serif;");
        Region dummy4 = new Region();
        dummy4.setPrefHeight(20);
        card4.getChildren().addAll(top4, val4, sub4, dummy4);

        grid.add(card1, 0, 0);
        grid.add(card2, 1, 0);
        grid.add(card3, 2, 0);
        grid.add(card4, 3, 0);

        return grid;
    }

    private static VBox createBaseKpiCard() {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 14, 12, 14));
        card.setStyle("-fx-background-color: #121824; -fx-border-color: #1E293B; -fx-border-radius: 12; -fx-background-radius: 12;");
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private static HBox createKpiTop(String title, Node icon, String badgeBg) {
        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(title);
        l.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 10.5px; -fx-font-weight: 700; -fx-font-family: 'Inter', sans-serif; -fx-letter-spacing: 0.2px;");
        l.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        StackPane badge = new StackPane(icon);
        badge.setPrefSize(26, 26);
        badge.setMinSize(26, 26);
        badge.setMaxSize(26, 26);
        badge.setStyle("-fx-background-color: " + badgeBg + "; -fx-background-radius: 6;");

        top.getChildren().addAll(l, sp, badge);
        return top;
    }

    private static Label createKpiValue(String val) {
        Label l = new Label(val);
        l.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 20px; -fx-font-weight: 800; -fx-font-family: 'Montserrat', sans-serif;");
        l.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        return l;
    }

    private static Label createKpiSub(String sub) {
        Label l = new Label(sub);
        l.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 11px; -fx-font-family: 'Inter', sans-serif;");
        l.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        return l;
    }

    private static Node createSparkline(List<Double> points) {
        Canvas canvas = new Canvas(140, 24);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (points == null || points.size() < 2) return canvas;

        double max = points.stream().mapToDouble(Double::doubleValue).max().orElse(100);
        double min = points.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double range = Math.max(1, max - min);

        double w = canvas.getWidth();
        double h = canvas.getHeight();
        double padY = 3;
        double usableH = h - padY * 2;
        double stepX = (w - 10) / (points.size() - 1);

        gc.setStroke(Color.web("#F59E0B"));
        gc.setLineWidth(2.2);

        double prevX = 0, prevY = 0;
        for (int i = 0; i < points.size(); i++) {
            double px = 4 + i * stepX;
            double py = h - padY - (((points.get(i) - min) / range) * usableH);
            if (i > 0) gc.strokeLine(prevX, prevY, px, py);
            prevX = px;
            prevY = py;
        }

        gc.setFill(Color.web("#FBBF24"));
        gc.fillOval(prevX - 3.5, prevY - 3.5, 7, 7);
        return canvas;
    }

    private static Node createMultiSegmentBar(long occupied, long available, long cleaning, long maintenance) {
        HBox bar = new HBox(3);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPrefHeight(6);
        bar.setMaxHeight(6);

        long total = occupied + available + cleaning + maintenance;
        if (total == 0) total = 14;

        Region s1 = new Region();
        s1.setStyle("-fx-background-color: #EAB308; -fx-background-radius: 3;");
        HBox.setHgrow(s1, Priority.ALWAYS);
        s1.prefWidthProperty().bind(bar.widthProperty().multiply((double) occupied / total));

        Region s2 = new Region();
        s2.setStyle("-fx-background-color: #10B981; -fx-background-radius: 3;");
        HBox.setHgrow(s2, Priority.ALWAYS);
        s2.prefWidthProperty().bind(bar.widthProperty().multiply((double) available / total));

        Region s3 = new Region();
        s3.setStyle("-fx-background-color: #38BDF8; -fx-background-radius: 3;");
        HBox.setHgrow(s3, Priority.ALWAYS);
        s3.prefWidthProperty().bind(bar.widthProperty().multiply((double) cleaning / total));

        Region s4 = new Region();
        s4.setStyle("-fx-background-color: #F43F5E; -fx-background-radius: 3;");
        HBox.setHgrow(s4, Priority.ALWAYS);
        s4.prefWidthProperty().bind(bar.widthProperty().multiply((double) maintenance / total));

        bar.getChildren().addAll(s1, s2, s3, s4);
        return bar;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 3. AI INSIGHT BANNER (NHẬN ĐỊNH HÔM NAY)
    // ═════════════════════════════════════════════════════════════════════════
    private static VBox buildAiInsightCard(DashboardStats stats) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle("-fx-background-color: #121824; -fx-border-color: rgba(245, 158, 11, 0.35); -fx-border-radius: 12; -fx-background-radius: 12;");

        // Top Row
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        Label aiBadge = new Label("AI");
        aiBadge.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: #0A0D14; -fx-font-family: 'Montserrat', sans-serif; " +
                "-fx-font-size: 10.5px; -fx-font-weight: 800; -fx-background-radius: 5; -fx-padding: 2 6;");

        Label title = new Label(stats.aiInsightTitle);
        title.setStyle("-fx-text-fill: #FFFFFF; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 14.5px; -fx-font-weight: 800;");

        VBox titleCol = new VBox(2, new HBox(8, aiBadge, title), new Label(stats.aiInsightSub) {{
            setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 12px; -fx-font-family: 'Inter', sans-serif;");
        }});

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnAnalyze = new Button("Phân tích");
        btnAnalyze.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: #0A0D14; -fx-font-family: 'Montserrat', sans-serif; " +
                "-fx-font-size: 12px; -fx-font-weight: 800; -fx-background-radius: 6; -fx-padding: 6 16; -fx-cursor: hand;");

        top.getChildren().addAll(titleCol, sp, btnAnalyze);

        // Body
        VBox body = new VBox(8);
        Label defaultPrompt = new Label(stats.aiInsightContent);
        defaultPrompt.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12.5px; -fx-font-family: 'Inter', sans-serif;");
        body.getChildren().add(defaultPrompt);

        // Interactive AI analysis
        btnAnalyze.setOnAction(e -> {
            btnAnalyze.setDisable(true);
            btnAnalyze.setText("Đang phân tích…");
            body.getChildren().clear();
            Label loadingAi = new Label("Trợ lý AI đang đọc số liệu doanh thu, tình trạng phòng và công nợ…");
            loadingAi.setStyle("-fx-text-fill: #FBBF24; -fx-font-size: 12.5px; -fx-font-family: 'Inter', sans-serif;");
            body.getChildren().add(loadingAi);

            Timeline t = new Timeline(new KeyFrame(Duration.millis(500), evt -> {
                body.getChildren().clear();
                for (String bullet : stats.aiInsightBulletPoints) {
                    HBox bulletRow = new HBox(8);
                    bulletRow.setAlignment(Pos.CENTER_LEFT);
                    Label dot = new Label("•");
                    dot.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 16px; -fx-font-weight: 900;");
                    Label txt = new Label(bullet);
                    txt.setStyle("-fx-text-fill: #E2E8F0; -fx-font-size: 12.5px; -fx-font-family: 'Inter', sans-serif;");
                    txt.setWrapText(true);
                    HBox.setHgrow(txt, Priority.ALWAYS);
                    bulletRow.getChildren().addAll(dot, txt);
                    body.getChildren().add(bulletRow);
                }
                btnAnalyze.setText("Cập nhật lại");
                btnAnalyze.setDisable(false);
            }));
            t.play();
        });

        card.getChildren().addAll(top, body);
        return card;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 4. MIDDLE CHARTS ROW (DOANH THU 7 NGÀY & TÌNH TRẠNG PHÒNG)
    // ═════════════════════════════════════════════════════════════════════════
    private static HBox buildMiddleChartsRow(DashboardStats stats) {
        HBox row = new HBox(16);

        // ── 4.1 Doanh thu 7 ngày gần nhất ────────────────────────────────────
        VBox revCard = new VBox(14);
        revCard.setPadding(new Insets(18, 20, 16, 20));
        revCard.setStyle("-fx-background-color: #121824; -fx-border-color: #1E293B; -fx-border-radius: 12; -fx-background-radius: 12;");
        HBox.setHgrow(revCard, Priority.ALWAYS);

        Label revTitle = new Label("Doanh thu 7 ngày gần nhất");
        revTitle.setStyle("-fx-text-fill: #FFFFFF; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 15px; -fx-font-weight: 800;");

        Label revSub = new Label("Tổng " + Util.money(stats.revenue7DaysTotal) + " · trung bình " + Util.money(stats.revenue7DaysAverage) + "/ngày");
        revSub.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 12px; -fx-font-family: 'Inter', sans-serif;");

        VBox revHeader = new VBox(3, revTitle, revSub);
        Node revChart = createRevenue7DaysChart(stats.dailyRevenues);
        revCard.getChildren().addAll(revHeader, revChart);

        // ── 4.2 Tình trạng phòng (Donut Chart) ────────────────────────────────
        VBox roomCard = new VBox(14);
        roomCard.setPrefWidth(340);
        roomCard.setMinWidth(320);
        roomCard.setPadding(new Insets(18, 20, 16, 20));
        roomCard.setStyle("-fx-background-color: #121824; -fx-border-color: #1E293B; -fx-border-radius: 12; -fx-background-radius: 12;");

        Label roomTitle = new Label("Tình trạng phòng");
        roomTitle.setStyle("-fx-text-fill: #FFFFFF; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 15px; -fx-font-weight: 800;");

        Label roomSub = new Label(stats.totalRooms + " phòng · " + stats.readyToWelcome + " sẵn sàng đón khách");
        roomSub.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 12px; -fx-font-family: 'Inter', sans-serif;");

        VBox roomHeader = new VBox(3, roomTitle, roomSub);

        Node donut = createDonutChart(stats.occupancyRate, (int) stats.occupiedRooms, (int) stats.availableRooms, (int) stats.cleaningRooms, (int) stats.maintenanceRooms);

        VBox legendBox = new VBox(6,
                createLegendItem("#EAB308", "Đang ở", stats.occupiedRooms),
                createLegendItem("#10B981", "Trống", stats.availableRooms),
                createLegendItem("#38BDF8", "Đang dọn", stats.cleaningRooms),
                createLegendItem("#F43F5E", "Bảo trì", stats.maintenanceRooms)
        );
        legendBox.setPadding(new Insets(6, 12, 0, 12));

        roomCard.getChildren().addAll(roomHeader, donut, legendBox);

        row.getChildren().addAll(revCard, roomCard);
        return row;
    }

    private static Node createRevenue7DaysChart(List<DashboardStats.DailyRevenueItem> dailyRevenues) {
        VBox container = new VBox(4);

        HBox chartBox = new HBox(10);
        chartBox.setAlignment(Pos.BOTTOM_CENTER);
        chartBox.setPrefHeight(165);

        VBox yAxis = new VBox();
        yAxis.setAlignment(Pos.TOP_RIGHT);
        yAxis.setPrefHeight(135);
        yAxis.setPadding(new Insets(0, 4, 18, 0));

        Region sp1 = new Region(); VBox.setVgrow(sp1, Priority.ALWAYS);
        Region sp2 = new Region(); VBox.setVgrow(sp2, Priority.ALWAYS);
        Region sp3 = new Region(); VBox.setVgrow(sp3, Priority.ALWAYS);

        yAxis.getChildren().addAll(
                createAxisLabel("3 tr"),
                sp1,
                createAxisLabel("2 tr"),
                sp2,
                createAxisLabel("1 tr"),
                sp3,
                createAxisLabel("0")
        );

        HBox barsRow = new HBox(16);
        barsRow.setAlignment(Pos.BOTTOM_CENTER);
        HBox.setHgrow(barsRow, Priority.ALWAYS);

        double maxVal = 3_000_000.0;
        double maxBarHeight = 115.0;

        for (var item : dailyRevenues) {
            VBox col = new VBox(4);
            col.setAlignment(Pos.BOTTOM_CENTER);
            HBox.setHgrow(col, Priority.ALWAYS);

            Label topLbl = new Label(item.labelTop != null ? item.labelTop : " ");
            topLbl.setStyle("-fx-text-fill: " + (item.isToday ? "#FBBF24" : "#F59E0B") + "; -fx-font-size: 11px; -fx-font-weight: 700; -fx-font-family: 'Inter', sans-serif;");

            double val = item.amount != null ? item.amount.doubleValue() : 0;
            double barH = Math.max(3.0, (val / maxVal) * maxBarHeight);
            Region bar = new Region();
            bar.setPrefWidth(38);
            bar.setMinWidth(26);
            bar.setMaxWidth(46);
            bar.setPrefHeight(barH);
            bar.setMinHeight(barH);
            bar.setMaxHeight(barH);

            String barBg = item.isToday
                    ? "linear-gradient(to top, #D97706, #FBBF24)"
                    : (val > 0 ? "linear-gradient(to top, #926315, #D97706)" : "#1C2433");
            bar.setStyle("-fx-background-color: " + barBg + "; -fx-background-radius: 4 4 0 0; -fx-cursor: hand;");
            Tooltip.install(bar, new Tooltip(item.dayLabel + ": " + (val > 0 ? Util.money(item.amount) : "0 ₫")));

            Label dayLbl = new Label(item.dayLabel);
            dayLbl.setStyle("-fx-text-fill: " + (item.isToday ? "#FFFFFF" : "#8C9CB2") + "; -fx-font-size: 11px; -fx-font-weight: " + (item.isToday ? "700" : "500") + "; -fx-alignment: center;");

            col.getChildren().addAll(topLbl, bar, dayLbl);
            barsRow.getChildren().add(col);
        }

        chartBox.getChildren().addAll(yAxis, barsRow);
        container.getChildren().add(chartBox);
        return container;
    }

    private static Label createAxisLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #64748B; -fx-font-size: 10.5px; -fx-font-family: 'Inter', sans-serif;");
        return l;
    }

    private static Node createDonutChart(int occupancyRate, int occupied, int available, int cleaning, int maintenance) {
        Canvas canvas = new Canvas(140, 140);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double cx = 70, cy = 70;
        double r = 54;
        double thickness = 13;

        double total = occupied + available + cleaning + maintenance;
        if (total == 0) total = 14;

        double aOccupied = (occupied / total) * 360.0;
        double aAvailable = (available / total) * 360.0;
        double aCleaning = (cleaning / total) * 360.0;
        double aMaintenance = 360.0 - (aOccupied + aAvailable + aCleaning);

        gc.setLineWidth(thickness);
        double currentAngle = 90;

        // Yellow (Đang ở)
        gc.setStroke(Color.web("#EAB308"));
        gc.strokeArc(cx - r + thickness/2, cy - r + thickness/2, (r - thickness/2) * 2, (r - thickness/2) * 2, currentAngle, -aOccupied, ArcType.OPEN);
        currentAngle -= aOccupied;

        // Green (Trống)
        gc.setStroke(Color.web("#10B981"));
        gc.strokeArc(cx - r + thickness/2, cy - r + thickness/2, (r - thickness/2) * 2, (r - thickness/2) * 2, currentAngle, -aAvailable, ArcType.OPEN);
        currentAngle -= aAvailable;

        // Blue (Đang dọn)
        gc.setStroke(Color.web("#38BDF8"));
        gc.strokeArc(cx - r + thickness/2, cy - r + thickness/2, (r - thickness/2) * 2, (r - thickness/2) * 2, currentAngle, -aCleaning, ArcType.OPEN);
        currentAngle -= aCleaning;

        // Red (Bảo trì)
        gc.setStroke(Color.web("#F43F5E"));
        gc.strokeArc(cx - r + thickness/2, cy - r + thickness/2, (r - thickness/2) * 2, (r - thickness/2) * 2, currentAngle, -aMaintenance, ArcType.OPEN);

        Label percentLbl = new Label(occupancyRate + "%");
        percentLbl.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");

        Label subLbl = new Label("CÔNG SUẤT");
        subLbl.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 9.5px; -fx-font-weight: 700; -fx-text-fill: #8C9CB2;");

        VBox centerBox = new VBox(0, percentLbl, subLbl);
        centerBox.setAlignment(Pos.CENTER);

        StackPane stack = new StackPane(canvas, centerBox);
        stack.setAlignment(Pos.CENTER);
        return stack;
    }

    private static HBox createLegendItem(String colorHex, String label, long count) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        Region dot = new Region();
        dot.setPrefSize(9, 9);
        dot.setMinSize(9, 9);
        dot.setMaxSize(9, 9);
        dot.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 2;");

        Label name = new Label(label);
        name.setStyle("-fx-text-fill: #E2E8F0; -fx-font-size: 12.5px; -fx-font-family: 'Inter', sans-serif;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label val = new Label(String.valueOf(count));
        val.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 13px; -fx-font-weight: 700; -fx-font-family: 'Montserrat', sans-serif;");

        row.getChildren().addAll(dot, name, sp, val);
        return row;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 5. UNIFIED 3-COLUMN GRID: BREAKDOWNS + SCHEDULE & INVOICES
    // ═════════════════════════════════════════════════════════════════════════
    private static GridPane buildBreakdownAndBottomGrid(DashboardStats stats) {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setPercentWidth(33.3333);
        col0.setHgrow(Priority.ALWAYS);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(33.3333);
        col1.setHgrow(Priority.ALWAYS);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(33.3334);
        col2.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(col0, col1, col2);

        // ── Row 0: 3 Breakdown Cards (33.33% each) ───────────────────────────
        VBox cardPayment = createBreakdownCard("Thu tiền theo phương thức", "Tổng các khoản đã thanh toán");
        for (var item : stats.paymentBreakdowns) {
            cardPayment.getChildren().add(createProgressItem(item.name, item.amountText, item.percent, item.colorHex));
        }

        VBox cardRoomType = createBreakdownCard("Tiền phòng theo loại phòng", "Từ các hóa đơn đã lập");
        for (var item : stats.roomTypeBreakdowns) {
            cardRoomType.getChildren().add(createProgressItem(item.name, item.amountText, item.percent, "#F59E0B"));
        }

        VBox cardServices = createBreakdownCard("Dịch vụ bán chạy", "Theo doanh thu");
        for (var item : stats.topServices) {
            cardServices.getChildren().add(createProgressItem(item.name, item.amountText, item.percent, "#F59E0B"));
        }

        grid.add(cardPayment, 0, 0);
        grid.add(cardRoomType, 1, 0);
        grid.add(cardServices, 2, 0);

        // ── Row 1: Schedule Events (Col 0) & Recent Invoices (Col 1+2) ───────
        // ScheduleCard has EXACT same 33.33% width as cardPayment!
        VBox scheduleCard = new VBox(12);
        scheduleCard.setPadding(new Insets(16, 18, 16, 18));
        scheduleCard.setStyle("-fx-background-color: #121824; -fx-border-color: #1E293B; -fx-border-radius: 12; -fx-background-radius: 12;");
        scheduleCard.setMaxWidth(Double.MAX_VALUE);

        Label schTitle = new Label("Lịch hôm nay & ngày mai");
        schTitle.setStyle("-fx-text-fill: #FFFFFF; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 14.5px; -fx-font-weight: 800;");

        Label schSub = new Label("Nhận phòng và trả phòng sắp tới");
        schSub.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 12px; -fx-font-family: 'Inter', sans-serif;");

        VBox schList = new VBox(10);
        for (var ev : stats.upcomingEvents) {
            schList.getChildren().add(createScheduleRow(ev));
        }
        scheduleCard.getChildren().addAll(new VBox(3, schTitle, schSub), schList);

        // InvoicesCard spans 2 columns (66.67%), matching cardRoomType + cardServices combined!
        VBox invCard = new VBox(12);
        invCard.setPadding(new Insets(16, 20, 16, 20));
        invCard.setStyle("-fx-background-color: #121824; -fx-border-color: #1E293B; -fx-border-radius: 12; -fx-background-radius: 12;");
        invCard.setMaxWidth(Double.MAX_VALUE);

        HBox invHeader = new HBox();
        invHeader.setAlignment(Pos.CENTER_LEFT);

        Label invTitle = new Label("Hóa đơn gần đây");
        invTitle.setStyle("-fx-text-fill: #FFFFFF; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 14.5px; -fx-font-weight: 800;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnViewAll = new Button("Xem tất cả");
        btnViewAll.setStyle("-fx-background-color: transparent; -fx-border-color: #2D3A4F; -fx-border-radius: 6; -fx-background-radius: 6; " +
                "-fx-text-fill: #CBD5E1; -fx-font-size: 11.5px; -fx-font-weight: 600; -fx-padding: 4 12; -fx-cursor: hand;");
        btnViewAll.setOnMouseEntered(e -> btnViewAll.setStyle("-fx-background-color: rgba(255, 255, 255, 0.06); -fx-border-color: #475569; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #FFFFFF; -fx-font-size: 11.5px; -fx-font-weight: 600; -fx-padding: 4 12; -fx-cursor: hand;"));
        btnViewAll.setOnMouseExited(e -> btnViewAll.setStyle("-fx-background-color: transparent; -fx-border-color: #2D3A4F; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #CBD5E1; -fx-font-size: 11.5px; -fx-font-weight: 600; -fx-padding: 4 12; -fx-cursor: hand;"));
        btnViewAll.setOnAction(e -> {
            if (appShell != null) appShell.goTo("invoices");
        });

        invHeader.getChildren().addAll(invTitle, sp, btnViewAll);

        // Table Header
        HBox theader = new HBox(8);
        theader.setPadding(new Insets(6, 0, 8, 0));
        theader.setStyle("-fx-border-color: transparent transparent #1E293B transparent; -fx-border-width: 0 0 1 0;");

        Label thCust = createColHeader("KHÁCH HÀNG", 0);
        HBox.setHgrow(thCust, Priority.ALWAYS);
        thCust.setMinWidth(110);

        theader.getChildren().addAll(
                thCust,
                createColHeader("PHÒNG", 65),
                createColHeader("TỔNG TIỀN", 110),
                createColHeader("THANH TOÁN", 135),
                createColHeader("NGÀY LẬP", 85)
        );

        VBox invList = new VBox(0);
        invList.getChildren().add(theader);

        for (var inv : stats.recentInvoices) {
            invList.getChildren().add(createInvoiceRow(inv));
        }

        invCard.getChildren().addAll(invHeader, invList);

        grid.add(scheduleCard, 0, 1);
        grid.add(invCard, 1, 1, 2, 1);

        return grid;
    }

    private static VBox createBreakdownCard(String title, String subtitle) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16, 18, 16, 18));
        card.setStyle("-fx-background-color: #121824; -fx-border-color: #1E293B; -fx-border-radius: 12; -fx-background-radius: 12;");
        card.setMaxWidth(Double.MAX_VALUE);

        Label t = new Label(title);
        t.setStyle("-fx-text-fill: #FFFFFF; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 14.5px; -fx-font-weight: 800;");

        Label s = new Label(subtitle);
        s.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 12px; -fx-font-family: 'Inter', sans-serif;");

        card.getChildren().addAll(new VBox(3, t, s));
        return card;
    }

    private static VBox createProgressItem(String name, String amountText, int percent, String barColor) {
        VBox box = new VBox(5);

        HBox textRow = new HBox();
        textRow.setAlignment(Pos.CENTER_LEFT);

        Region dot = new Region();
        dot.setPrefSize(8, 8);
        dot.setStyle("-fx-background-color: " + barColor + "; -fx-background-radius: 2;");

        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-text-fill: #E2E8F0; -fx-font-size: 12.5px; -fx-font-family: 'Inter', sans-serif;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label amtLbl = new Label(amountText);
        amtLbl.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 12.5px; -fx-font-weight: 700; -fx-font-family: 'Inter', sans-serif;");

        Label pctLbl = new Label(" " + percent + "%");
        pctLbl.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 11.5px; -fx-font-family: 'Inter', sans-serif;");

        HBox rightVal = new HBox(amtLbl, pctLbl);
        rightVal.setAlignment(Pos.CENTER_RIGHT);

        textRow.getChildren().addAll(new HBox(6, dot, nameLbl), sp, rightVal);

        // Progress Bar
        StackPane barTrack = new StackPane();
        barTrack.setPrefHeight(6);
        barTrack.setStyle("-fx-background-color: #1E2738; -fx-background-radius: 3;");
        barTrack.setAlignment(Pos.CENTER_LEFT);

        Region barFill = new Region();
        barFill.setPrefHeight(6);
        barFill.setStyle("-fx-background-color: " + barColor + "; -fx-background-radius: 3;");
        barFill.prefWidthProperty().bind(barTrack.widthProperty().multiply(percent / 100.0));

        barTrack.getChildren().add(barFill);

        box.getChildren().addAll(textRow, barTrack);
        return box;
    }

    private static HBox createScheduleRow(DashboardStats.ScheduleEventItem ev) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));
        row.setStyle("-fx-border-color: transparent transparent #1A2230 transparent; -fx-border-width: 0 0 1 0;");

        // Badge
        Label badge = new Label(ev.typeLabel);
        if ("NHAN_PHONG".equals(ev.type)) {
            badge.setStyle("-fx-background-color: rgba(56, 189, 248, 0.15); -fx-text-fill: #38BDF8; -fx-font-size: 10px; -fx-font-weight: 800; -fx-padding: 3 8; -fx-background-radius: 4;");
        } else {
            badge.setStyle("-fx-background-color: rgba(245, 158, 11, 0.15); -fx-text-fill: #F59E0B; -fx-font-size: 10px; -fx-font-weight: 800; -fx-padding: 3 8; -fx-background-radius: 4;");
        }
        badge.setPrefWidth(90);
        badge.setAlignment(Pos.CENTER);

        VBox textCol = new VBox(2);
        Label name = new Label(ev.customerName);
        name.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 13px; -fx-font-weight: 700; -fx-font-family: 'Inter', sans-serif;");

        Label info = new Label(ev.roomInfo);
        info.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 11px; -fx-font-family: 'Inter', sans-serif;");
        textCol.getChildren().addAll(name, info);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label time = new Label(ev.timeLabel);
        time.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 11.5px; -fx-font-family: 'Inter', sans-serif;");

        row.getChildren().addAll(badge, textCol, sp, time);
        return row;
    }

    private static Label createColHeader(String text, double width) {
        Label l = new Label(text);
        if (width > 0) l.setPrefWidth(width);
        l.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px; -fx-font-weight: 700; -fx-font-family: 'Inter', sans-serif; -fx-letter-spacing: 0.5px;");
        return l;
    }

    private static HBox createInvoiceRow(DashboardStats.RecentInvoiceItem inv) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(9, 0, 9, 0));
        row.setStyle("-fx-border-color: transparent transparent #1A2230 transparent; -fx-border-width: 0 0 1 0;");

        Label cName = new Label(inv.customerName);
        cName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 12.5px; -fx-font-weight: 600; -fx-font-family: 'Inter', sans-serif;");
        HBox.setHgrow(cName, Priority.ALWAYS);
        cName.setMinWidth(110);

        Label cRoom = new Label(inv.roomCode);
        cRoom.setPrefWidth(65);
        cRoom.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 12px; -fx-font-family: 'Inter', sans-serif;");

        Label cTotal = new Label(inv.totalText);
        cTotal.setPrefWidth(110);
        cTotal.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 12.5px; -fx-font-weight: 700; -fx-font-family: 'Montserrat', sans-serif;");

        // Status badge
        Label cStatus = new Label(inv.statusLabel);
        if ("DA_THANH_TOAN".equals(inv.status)) {
            cStatus.setStyle("-fx-background-color: rgba(16, 185, 129, 0.15); -fx-text-fill: #34D399; -fx-font-size: 10px; -fx-font-weight: 700; -fx-padding: 3 8; -fx-background-radius: 4;");
        } else if ("CHUA_THANH_TOAN".equals(inv.status)) {
            cStatus.setStyle("-fx-background-color: rgba(239, 68, 68, 0.15); -fx-text-fill: #F87171; -fx-font-size: 10px; -fx-font-weight: 700; -fx-padding: 3 8; -fx-background-radius: 4;");
        } else {
            cStatus.setStyle("-fx-background-color: rgba(245, 158, 11, 0.15); -fx-text-fill: #FBBF24; -fx-font-size: 10px; -fx-font-weight: 700; -fx-padding: 3 8; -fx-background-radius: 4;");
        }
        HBox statusBox = new HBox(cStatus);
        statusBox.setPrefWidth(135);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        Label cDate = new Label(inv.date);
        cDate.setPrefWidth(85);
        cDate.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 11.5px; -fx-font-family: 'Inter', sans-serif;");

        row.getChildren().addAll(cName, cRoom, cTotal, statusBox, cDate);
        return row;
    }
}


