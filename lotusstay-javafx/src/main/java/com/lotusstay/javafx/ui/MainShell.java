package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.MainApp;
import com.lotusstay.javafx.net.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * MainShell — Dark Luxury Theme (Black, Dark Slate & Gold).
 * Cấu trúc chuẩn theo đúng ảnh thiết kế: Nền đen tuyền, menu bên trái nổi bật
 * với Bootstrap Icons,
 * thanh trên cùng trang nhã, vùng hiển thị thoáng đãng và sang trọng.
 */
public class MainShell extends BorderPane {

    private final StackPane content = new StackPane();
    private final Label pageTitle = new Label();
    private final Map<String, Button> navButtons = new LinkedHashMap<>();

    public static MainShell build() {
        return new MainShell();
    }

    private MainShell() {
        setStyle("-fx-background-color: #0A0D14;");
        getStyleClass().add("app-shell");
        if (MainApp.class.getResource("/com/lotusstay/javafx/theme.css") != null) {
            getStylesheets().add(MainApp.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());
        }
        setLeft(buildSidebar());

        VBox centerArea = new VBox();
        centerArea.setStyle("-fx-background-color: #0A0D14;");
        Node topbar = buildTopbar();
        content.setStyle("-fx-background-color: #0A0D14;");
        centerArea.getChildren().addAll(topbar, content);
        VBox.setVgrow(content, Priority.ALWAYS);
        setCenter(centerArea);

        goTo("dashboard");
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setStyle(
                "-fx-background-color: #0A0D14; -fx-min-width: 250px; -fx-pref-width: 250px; -fx-max-width: 250px; -fx-border-color: transparent #1E2532 transparent transparent; -fx-border-width: 0 1 0 0;");

        // ── Brand header ────────────────────────────────────────────────
        HBox brand = new HBox(12);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(20, 18, 18, 20));
        brand.setStyle(
                "-fx-background-color: #0A0D14; -fx-border-color: transparent transparent #1E2532 transparent; -fx-border-width: 0 0 1 0;");

        Node mark = IconUtils.icon("building", 22, Color.web("#F59E0B"));
        VBox names = new VBox(2,
                labelled("LotusStay", "brand-name"),
                labelled("QUẢN LÝ KHÁCH SẠN", "brand-sub"));
        brand.getChildren().addAll(mark, names);

        // ── 1. Section: VẬN HÀNH / TỔNG QUAN ────────────────────────────
        Label opLabel = new Label("VẬN HÀNH");
        opLabel.getStyleClass().add("sidebar-section-label");

        VBox opNav = new VBox(2);
        opNav.setPadding(new Insets(0, 10, 0, 10));
        addNavTo(opNav, "dashboard", "dashboard", "Tổng quan", true, true, true);
        addNavTo(opNav, "rooms", "rooms", "Sơ đồ phòng", true, true, true);
        addNavTo(opNav, "bookings", "bookings", "Đặt phòng", true, true, true);
        addNavTo(opNav, "bookingservices", "bell", "Chi tiết DV phòng", true, true, true);
        addNavTo(opNav, "customers", "customers", "Khách hàng", true, true, true);
        addNavTo(opNav, "invoices", "invoices", "Hóa đơn", true, true, true);

        // ── 2. Section: DANH MỤC ─────────────────────────────────────────
        Label catLabel = new Label("DANH MỤC");
        catLabel.getStyleClass().add("sidebar-section-label");

        VBox catNav = new VBox(2);
        catNav.setPadding(new Insets(0, 10, 0, 10));
        addNavTo(catNav, "roomtypes", "roomtypes", "Loại phòng", true, false, false);
        addNavTo(catNav, "services", "services", "Dịch vụ", true, true, false);
        addNavTo(catNav, "promotions", "promotions", "Khuyến mãi", true, false, false);
        addNavTo(catNav, "paymentmethods", "paymentmethods", "Phương thức thanh toán", true, false, false);

        // ── 3. Section: NHÂN SỰ ──────────────────────────────────────────
        Label hrLabel = new Label("NHÂN SỰ");
        hrLabel.getStyleClass().add("sidebar-section-label");

        VBox hrNav = new VBox(2);
        hrNav.setPadding(new Insets(0, 10, 0, 10));
        addNavTo(hrNav, "staff", "staff", "Nhân viên & Tài khoản", true, true, false);
        addNavTo(hrNav, "workshifts", "workshifts", "Ca làm việc", true, true, false);
        addNavTo(hrNav, "shiftassignments", "shiftassignments", "Phân ca", true, true, false);
        addNavTo(hrNav, "attendance", "attendance", "Chấm công", true, true, false);

        VBox scrollContent = new VBox();
        scrollContent.getChildren().addAll(opLabel, opNav, catLabel, catNav, hrLabel, hrNav);
        scrollContent.setStyle("-fx-background-color: #0A0D14; -fx-padding: 0 0 20 0;");

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #0A0D14; -fx-background: #0A0D14; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // ── User footer ──────────────────────────────────────────────────
        VBox footer = buildUserFooter();

        sidebar.getChildren().addAll(brand, scrollPane, footer);
        return sidebar;
    }

    private VBox buildUserFooter() {
        VBox footer = new VBox(8);
        footer.getStyleClass().add("sidebar-user-footer");
        footer.setStyle(
                "-fx-background-color: #0A0D14; -fx-border-color: #1E2532 transparent transparent transparent; -fx-border-width: 1 0 0 0; -fx-padding: 14 16;");

        String fullName = Session.getFullName();
        String name = (fullName == null || fullName.isBlank()) ? "admin" : fullName;
        String initials = fullName != null && !fullName.isBlank()
                ? fullName.substring(0, Math.min(2, fullName.length())).toUpperCase()
                : "PH";

        HBox userRow = new HBox(10);
        userRow.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label(initials);
        avatar.setStyle(
                "-fx-background-color: #F59E0B; -fx-text-fill: #0A0D14; -fx-font-weight: 800; -fx-font-size: 11px; -fx-padding: 5 8; -fx-background-radius: 6;");

        VBox userText = new VBox(2);
        Label nameLabel = new Label(name);
        nameLabel.setStyle(
                "-fx-font-family: 'Inter', sans-serif; -fx-font-weight: 700; -fx-font-size: 12.5px; -fx-text-fill: #FFFFFF;");

        Label roleLabel = new Label(roleLabel(Session.getRole()));
        roleLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 11px; -fx-text-fill: #8C9CB2;");
        userText.getChildren().addAll(nameLabel, roleLabel);

        userRow.getChildren().addAll(avatar, userText);

        Button logout = new Button("Đăng xuất");
        logout.setGraphic(IconUtils.icon("logout", 14, Color.web("#EF4444")));
        logout.setGraphicTextGap(8);
        logout.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #EF4444; -fx-font-family: 'Inter', sans-serif; -fx-font-size: 12px; -fx-font-weight: 600; -fx-cursor: hand; -fx-padding: 4 0;");
        logout.setOnAction(e -> {
            Session.clear();
            Scene loginScene = new Scene(LoginView.build(), 1100, 700);
            loginScene.setFill(Color.web("#0A0D14"));
            loginScene.getStylesheets().add(
                    MainApp.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());
            MainApp.primaryStage.setScene(loginScene);
            MainApp.primaryStage.setMaximized(false);
        });

        footer.getChildren().addAll(userRow, logout);
        return footer;
    }

    private Label labelled(String text, String styleClass) {
        Label l = new Label(text);
        l.getStyleClass().add(styleClass);
        return l;
    }

    private void addNavTo(VBox nav, String key, String iconName, String label, boolean admin, boolean manager,
            boolean reception) {
        boolean visible = (Session.getRole() == null)
                || (Session.isAdmin() && admin)
                || ("MANAGER".equals(Session.getRole()) && manager)
                || ("RECEPTION".equals(Session.getRole()) && reception);
        if (!visible)
            return;

        Button btn = new Button(label);
        btn.getStyleClass().add("nav-button");
        btn.setGraphic(IconUtils.icon(iconName, 16, Color.web("#8C9CB2")));
        btn.setGraphicTextGap(12);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> goTo(key));
        navButtons.put(key, btn);
        nav.getChildren().add(btn);
    }

    private HBox buildTopbar() {
        HBox top = new HBox();
        top.getStyleClass().add("topbar");
        top.setStyle(
                "-fx-background-color: #0D111A; -fx-border-color: transparent transparent #1E2532 transparent; -fx-border-width: 0 0 1 0; -fx-padding: 14 28;");
        top.setAlignment(Pos.CENTER_LEFT);

        VBox titles = new VBox(2);
        pageTitle.setStyle(
                "-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");
        String dayStr = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", new Locale("vi", "VN")));
        Label date = new Label(dayStr);
        date.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px; -fx-text-fill: #8C9CB2;");
        titles.getChildren().addAll(pageTitle, date);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Quick action buttons
        Button quickBooking = new Button("Đặt phòng");
        quickBooking.setGraphic(IconUtils.icon("plus", 12, Color.web("#0A0D14")));
        quickBooking.setGraphicTextGap(6);
        quickBooking.setStyle(
                "-fx-font-family: 'Montserrat', sans-serif; -fx-font-weight: 800; -fx-font-size: 12.5px; -fx-background-color: #F59E0B; -fx-text-fill: #0A0D14; -fx-background-radius: 6; -fx-padding: 6 14; -fx-cursor: hand;");
        quickBooking.setOnAction(e -> goTo("bookings"));

        Button quickCheckIn = new Button("Nhận phòng");
        quickCheckIn.setGraphic(IconUtils.icon("check", 12, Color.web("#FFFFFF")));
        quickCheckIn.setGraphicTextGap(6);
        quickCheckIn.setStyle(
                "-fx-font-family: 'Inter', sans-serif; -fx-font-weight: 600; -fx-font-size: 12.5px; -fx-background-color: #1A2230; -fx-border-color: #334155; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #FFFFFF; -fx-padding: 6 14; -fx-cursor: hand;");
        quickCheckIn.setOnAction(e -> goTo("rooms"));

        // User box matching the screenshot
        String fullName = Session.getFullName();
        String name = (fullName == null || fullName.isBlank()) ? "admin" : fullName;

        HBox userBox = new HBox(10);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setStyle(
                "-fx-background-color: #141A24; -fx-border-color: #232D3F; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 6 14;");

        Label avatarBox = new Label("A");
        avatarBox.setStyle(
                "-fx-background-color: #F59E0B; -fx-text-fill: #0A0D14; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 3 8; -fx-background-radius: 4;");

        VBox userMeta = new VBox(1);
        Label uName = new Label(name);
        uName.setStyle(
                "-fx-font-family: 'Inter', sans-serif; -fx-font-weight: 700; -fx-font-size: 12.5px; -fx-text-fill: #FFFFFF;");
        Label uRole = new Label(roleLabel(Session.getRole()));
        uRole.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 11px; -fx-text-fill: #8C9CB2;");
        userMeta.getChildren().addAll(uName, uRole);

        Label powerIcon = new Label("⏻");
        powerIcon.setStyle("-fx-text-fill: #64748B; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0 0 0 6;");
        powerIcon.setOnMouseClicked(e -> {
            Session.clear();
            Scene loginScene = new Scene(LoginView.build(), 1100, 700);
            loginScene.getStylesheets().add(
                    MainApp.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());
            MainApp.primaryStage.setScene(loginScene);
            MainApp.primaryStage.setMaximized(false);
        });

        userBox.getChildren().addAll(avatarBox, userMeta, powerIcon);

        HBox quickActions = new HBox(8, quickBooking, quickCheckIn);
        quickActions.setAlignment(Pos.CENTER);

        top.getChildren().addAll(titles, spacer, quickActions, new Region() {
            {
                setMinWidth(14);
            }
        }, userBox);
        return top;
    }

    private String roleLabel(String role) {
        if (role == null)
            return "Quản trị viên";
        return switch (role) {
            case "ADMIN" -> "Quản trị viên";
            case "MANAGER" -> "Quản lý";
            case "RECEPTION" -> "Lễ tân";
            default -> role;
        };
    }

    private static final Map<String, String> TITLES = Map.ofEntries(
            Map.entry("dashboard", "Tổng quan hệ thống"),
            Map.entry("staff", "Quản lý Nhân viên & Tài khoản"),
            Map.entry("workshifts", "Ca làm việc"),
            Map.entry("shiftassignments", "Phân ca làm việc"),
            Map.entry("attendance", "Chấm công"),
            Map.entry("taikhoan", "Quản lý Nhân viên & Tài khoản"),
            Map.entry("customers", "Quản lý khách hàng"),
            Map.entry("rooms", "Sơ đồ phòng"),
            Map.entry("roomtypes", "Danh mục loại phòng"),
            Map.entry("bookings", "Quản lý đặt phòng & lưu trú"),
            Map.entry("bookingservices", "Quản lý Chi tiết dịch vụ phòng"),
            Map.entry("services", "Quản lý dịch vụ"),
            Map.entry("promotions", "Chương trình khuyến mãi"),
            Map.entry("paymentmethods", "Danh mục phương thức thanh toán"),
            Map.entry("invoices", "Quản lý hóa đơn & tài chính"));

    public void goTo(String key) {
        navButtons.forEach((k, b) -> {
            b.getStyleClass().remove("active");
            if (b.getGraphic() instanceof StackPane sp && !sp.getChildren().isEmpty()) {
                if (sp.getChildren().get(0) instanceof SVGPath svg) {
                    svg.setFill(Color.web("#8C9CB2"));
                }
            }
        });
        if (navButtons.containsKey(key)) {
            Button activeBtn = navButtons.get(key);
            activeBtn.getStyleClass().add("active");
            if (activeBtn.getGraphic() instanceof StackPane sp && !sp.getChildren().isEmpty()) {
                if (sp.getChildren().get(0) instanceof SVGPath svg) {
                    svg.setFill(Color.web("#F59E0B"));
                }
            }
        }
        pageTitle.setText(TITLES.getOrDefault(key, ""));

        Node view = switch (key) {
            case "staff", "taikhoan" -> StaffView.build();
            case "workshifts" -> WorkShiftView.build();
            case "shiftassignments" -> ShiftAssignmentView.build();
            case "attendance" -> AttendanceView.build();
            case "customers" -> CustomersView.build();
            case "rooms" -> RoomsView.build(this);
            case "roomtypes" -> RoomTypesView.build();
            case "bookings" -> BookingsView.build();
            case "bookingservices" -> BookingServiceDetailsView.build();
            case "services" -> ServicesView.build();
            case "invoices" -> InvoicesView.build();
            case "promotions" -> PromotionsView.build();
            case "paymentmethods" -> PaymentMethodsView.build();
            default -> DashboardView.build(this);
        };
        content.getChildren().setAll(view);
    }
}
