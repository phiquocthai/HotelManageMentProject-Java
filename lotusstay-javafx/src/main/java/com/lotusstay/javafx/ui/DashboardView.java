package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.net.ApiService;
import com.lotusstay.javafx.net.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalTime;

public class DashboardView {

    public static ScrollPane build() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: transparent;");

        // 1. Hero Banner
        VBox hero = new VBox(8);
        hero.getStyleClass().add("hero-banner");
        
        String greeting = "Chào buổi tối";
        int hour = LocalTime.now().getHour();
        if (hour >= 5 && hour < 12) greeting = "Chào buổi sáng";
        else if (hour >= 12 && hour < 18) greeting = "Chào buổi chiều";

        String roleName = Session.getRole() != null && Session.getRole().equals("ADMIN") ? "Quản trị viên" : "Bạn";
        Label heroTitle = new Label(greeting + ", " + roleName + " 👋");
        heroTitle.getStyleClass().add("hero-title");
        
        Label heroSub = new Label("Chúc bạn một ca làm việc thuận lợi — mọi số liệu phòng, đặt phòng và doanh thu\nđược cập nhật theo thời gian thực ngay bên dưới.");
        heroSub.getStyleClass().add("hero-sub");
        
        hero.getChildren().addAll(heroTitle, heroSub);
        root.getChildren().add(hero);

        // 2. KPI Cards Row
        HBox kpiRow = new HBox(16);
        root.getChildren().add(kpiRow);

        Label loading = new Label("Đang tải số liệu…");
        loading.getStyleClass().add("hint-text");
        kpiRow.getChildren().add(loading);

        Util.runAsync(ApiService::dashboard, stats -> {
            kpiRow.getChildren().clear();
            
            kpiRow.getChildren().addAll(
                    kpiCard("Doanh thu hôm nay", Util.money(stats.todayRevenue) + " ₫", stats.todayInvoiceCount + " hóa đơn", "💰"),
                    kpiCard("Công suất phòng", Math.round(((double) stats.occupiedRooms / stats.totalRooms) * 100) + "%", stats.occupiedRooms + "/" + stats.totalRooms + " phòng đang ở", "📈"),
                    kpiCard("Phòng trống", String.valueOf(stats.availableRooms), "Sẵn sàng đón khách", "🛏️"),
                    kpiCard("Sắp trả phòng", String.valueOf(stats.upcomingCheckouts), "Trong 24 giờ tới", "⏰")
            );
        });

        // 3. Bottom Sections: Recent Invoices & Room Status
        HBox bottomSections = new HBox(20);
        
        // 3.1 Recent Invoices Panel
        VBox invoicesPanel = new VBox(16);
        invoicesPanel.getStyleClass().add("panel");
        HBox.setHgrow(invoicesPanel, Priority.ALWAYS);
        
        HBox invoicesHeader = new HBox();
        Label invoicesTitle = new Label("Hóa đơn gần đây");
        invoicesTitle.getStyleClass().add("section-title");
        Region invSpacer = new Region();
        HBox.setHgrow(invSpacer, Priority.ALWAYS);
        Label viewAll = new Label("Xem tất cả");
        viewAll.getStyleClass().add("btn-outline"); // reusing style for button look
        viewAll.setStyle("-fx-padding: 4 12; -fx-font-size: 12px;");
        invoicesHeader.getChildren().addAll(invoicesTitle, invSpacer, viewAll);
        
        VBox invoicesList = new VBox(0); // Add table mock or real table here
        // Mock table header
        HBox theader = new HBox();
        theader.setPadding(new Insets(10, 0, 10, 0));
        theader.setStyle("-fx-border-color: transparent transparent #E5E7EB transparent; -fx-border-width: 0 0 1 0;");
        Label th1 = new Label("Khách hàng"); th1.setPrefWidth(120); th1.setStyle("-fx-text-fill: #6B7280; -fx-font-weight: bold;");
        Label th2 = new Label("Phòng"); th2.setPrefWidth(80); th2.setStyle("-fx-text-fill: #6B7280; -fx-font-weight: bold;");
        Label th3 = new Label("Tổng tiền"); th3.setPrefWidth(120); th3.setStyle("-fx-text-fill: #6B7280; -fx-font-weight: bold;");
        Label th4 = new Label("Ngày"); th4.setPrefWidth(100); th4.setStyle("-fx-text-fill: #6B7280; -fx-font-weight: bold;");
        theader.getChildren().addAll(th1, th2, th3, th4);
        invoicesList.getChildren().add(theader);
        
        // Mock rows
        invoicesList.getChildren().addAll(
            mockInvoiceRow("Vũ Thị F", "412", "1.080.000 ₫", "04/10/2026"),
            mockInvoiceRow("Hoàng Văn E", "108", "460.000 ₫", "03/10/2026"),
            mockInvoiceRow("Phạm Thị D", "310", "900.000 ₫", "02/10/2026")
        );
        
        invoicesPanel.getChildren().addAll(invoicesHeader, invoicesList);
        
        // 3.2 Room Status Panel
        VBox statusPanel = new VBox(16);
        statusPanel.getStyleClass().add("panel");
        statusPanel.setPrefWidth(300);
        statusPanel.setMinWidth(300);
        
        Label statusTitle = new Label("Tình trạng phòng");
        statusTitle.getStyleClass().add("section-title");
        statusPanel.getChildren().add(statusTitle);
        // Can add more content here later
        
        bottomSections.getChildren().addAll(invoicesPanel, statusPanel);
        root.getChildren().add(bottomSections);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return scroll;
    }
    
    private static HBox mockInvoiceRow(String c, String r, String t, String d) {
        HBox row = new HBox();
        row.setPadding(new Insets(12, 0, 12, 0));
        row.setStyle("-fx-border-color: transparent transparent #E5E7EB transparent; -fx-border-width: 0 0 1 0;");
        Label c1 = new Label(c); c1.setPrefWidth(120);
        Label c2 = new Label(r); c2.setPrefWidth(80);
        Label c3 = new Label(t); c3.setPrefWidth(120);
        Label c4 = new Label(d); c4.setPrefWidth(100);
        row.getChildren().addAll(c1, c2, c3, c4);
        return row;
    }

    private static VBox kpiCard(String label, String value, String sub, String iconStr) {
        VBox card = new VBox(10);
        card.getStyleClass().add("kpi-card-new");
        HBox.setHgrow(card, Priority.ALWAYS);
        
        HBox top = new HBox();
        Label l = new Label(label);
        l.getStyleClass().add("kpi-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label icon = new Label(iconStr);
        icon.setStyle("-fx-background-color: #F3F4F6; -fx-padding: 4 8; -fx-background-radius: 8;");
        top.getChildren().addAll(l, spacer, icon);

        Label v = new Label(value);
        v.getStyleClass().add("kpi-value-new");
        
        Label s = new Label(sub);
        s.getStyleClass().add("kpi-sub");

        card.getChildren().addAll(top, v, s);
        return card;
    }
}
