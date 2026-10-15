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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class MainShell extends BorderPane {

    private final StackPane content = new StackPane();
    private final Label pageTitle = new Label();
    private final Map<String, Button> navButtons = new LinkedHashMap<>();

    public static MainShell build() {
        return new MainShell();
    }

    private MainShell() {
        getStyleClass().add("app-shell");
        setLeft(buildSidebar());
        
        VBox centerArea = new VBox();
        Node topbar = buildTopbar();
        centerArea.getChildren().addAll(topbar, content);
        VBox.setVgrow(content, Priority.ALWAYS);
        setCenter(centerArea);
        
        goTo("dashboard");
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");

        // ── Brand header ────────────────────────────────────────────────
        HBox brand = new HBox(12);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(18, 16, 16, 18));
        brand.setStyle("-fx-background-color: rgba(0,0,0,0.15); -fx-border-color: transparent transparent #243050 transparent; -fx-border-width: 0 0 1 0;");

        Label mark = new Label("🛎️");
        mark.getStyleClass().add("brand-mark");
        VBox names = new VBox(2,
                labelled("LotusStay", "brand-name"),
                labelled("QUẢN LÝ KHÁCH SẠN", "brand-sub"));
        brand.getChildren().addAll(mark, names);

        // ── Nav section: Vận hành ────────────────────────────────────────
        Label opLabel = new Label("VẬN HÀNH");
        opLabel.getStyleClass().add("sidebar-section-label");

        VBox nav = new VBox(2);
        nav.setPadding(new Insets(0, 8, 0, 8));

        addNav(nav, "dashboard",  "📊  Tổng quan",           true, true, true);
        addNav(nav, "rooms",      "🛏️  Sơ đồ phòng",         true, true, true);
        addNav(nav, "bookings",   "📅  Đặt phòng",           true, true, true);
        addNav(nav, "customers",  "👤  Khách hàng",          true, true, true);
        addNav(nav, "invoices",   "🧾  Hóa đơn",             true, true, true);

        // ── Nav section: Danh mục ────────────────────────────────────────
        Label catLabel = new Label("DANH MỤC");
        catLabel.getStyleClass().add("sidebar-section-label");

        VBox catNav = new VBox(2);
        catNav.setPadding(new Insets(0, 8, 0, 8));

        addNavTo(catNav, "roomtypes",  "🏷️  Loại phòng",       true, false, false);
        addNavTo(catNav, "services",   "🧴  Dịch vụ",          true, true, false);
        addNavTo(catNav, "promotions", "🎁  Khuyến mãi",       true, false, false);

        // ── Nav section: Nhân sự ────────────────────────────────────────
        Label hrLabel = new Label("NHÂN SỰ");
        hrLabel.getStyleClass().add("sidebar-section-label");

        VBox hrNav = new VBox(2);
        hrNav.setPadding(new Insets(0, 8, 0, 8));

        addNavTo(hrNav, "staff",       "🧑‍💼  Nhân viên",        true, false, false);
        addNavTo(hrNav, "shifts",      "⏰  Ca làm việc",      true, false, false);
        addNavTo(hrNav, "assignments", "📁  Phân ca",          true, false, false);
        addNavTo(hrNav, "timekeeping", "✅  Chấm công",        true, false, false);
        addNavTo(hrNav, "users",       "🔐  Tài khoản hệ thống", true, false, false);

        VBox scrollContent = new VBox();
        scrollContent.getChildren().addAll(opLabel, nav, catLabel, catNav, hrLabel, hrNav);
        scrollContent.setStyle("-fx-background-color: transparent; -fx-padding: 0 0 20 0;");

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // ── User footer ──────────────────────────────────────────────────
        VBox footer = buildUserFooter();

        sidebar.getChildren().addAll(brand, scrollPane, footer);
        return sidebar;
    }

    private VBox buildUserFooter() {
        VBox footer = new VBox(6);
        footer.getStyleClass().add("sidebar-user-footer");

        // Avatar
        String fullName = Session.getFullName();
        String initials = fullName != null && !fullName.isBlank()
                ? fullName.substring(0, Math.min(2, fullName.length())).toUpperCase()
                : "?";

        Label avatar = new Label(initials);
        avatar.getStyleClass().add("sidebar-avatar");

        String name = (fullName == null || fullName.isBlank()) ? "Nhân viên" : fullName;
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("sidebar-user-name");

        Label roleLabel = new Label(roleLabel(Session.getRole()));
        roleLabel.getStyleClass().add("sidebar-user-role");

        HBox userInfo = new HBox(10, avatar, new VBox(2, nameLabel, roleLabel));
        userInfo.setAlignment(Pos.CENTER_LEFT);

        Button logout = new Button("⏻  Đăng xuất");
        logout.getStyleClass().add("nav-button");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setStyle("-fx-text-fill: #EF4444; -fx-padding: 6 10;");
        logout.setOnAction(e -> {
            Session.clear();
            Scene loginScene = new Scene(LoginView.build(), 1100, 700);
            loginScene.getStylesheets().add(
                    MainApp.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());
            MainApp.primaryStage.setScene(loginScene);
            MainApp.primaryStage.setMaximized(false);
        });

        footer.getChildren().addAll(userInfo, logout);
        return footer;
    }

    private Label labelled(String text, String styleClass) {
        Label l = new Label(text);
        l.getStyleClass().add(styleClass);
        return l;
    }

    private void addNav(VBox nav, String key, String label, boolean admin, boolean manager, boolean reception) {
        addNavTo(nav, key, label, admin, manager, reception);
    }

    private void addNavTo(VBox nav, String key, String label, boolean admin, boolean manager, boolean reception) {
        boolean visible = (Session.getRole() == null)
                || (Session.isAdmin() && admin)
                || ("MANAGER".equals(Session.getRole()) && manager)
                || ("RECEPTION".equals(Session.getRole()) && reception);
        if (!visible) return;

        Button btn = new Button(label);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> goTo(key));
        navButtons.put(key, btn);
        nav.getChildren().add(btn);
    }

    private HBox buildTopbar() {
        HBox top = new HBox();
        top.getStyleClass().add("topbar");
        top.setAlignment(Pos.CENTER_LEFT);

        VBox titles = new VBox(2);
        pageTitle.getStyleClass().add("page-title");
        String dayStr = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", new Locale("vi", "VN")));
        Label date = new Label(dayStr);
        date.getStyleClass().add("page-date");
        titles.getChildren().addAll(pageTitle, date);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Quick action buttons
        Button quickBooking = new Button("+ Đặt phòng");
        quickBooking.getStyleClass().addAll("btn-primary", "btn-sm");
        quickBooking.setOnAction(e -> goTo("bookings"));

        Button quickCheckIn = new Button("✓ Nhận phòng");
        quickCheckIn.getStyleClass().addAll("btn-outline", "btn-sm");
        quickCheckIn.setOnAction(e -> goTo("rooms"));

        // User pill
        String fullName = Session.getFullName();
        String name = (fullName == null || fullName.isBlank()) ? "Nhân viên" : fullName;
        Label user = new Label("👤  " + name + "  ·  " + roleLabel(Session.getRole()));
        user.getStyleClass().add("user-pill");

        HBox quickActions = new HBox(8, quickBooking, quickCheckIn);
        quickActions.setAlignment(Pos.CENTER);

        top.getChildren().addAll(titles, spacer, quickActions, new Region() {{ setMinWidth(16); }}, user);
        return top;
    }

    private String roleLabel(String role) {
        if (role == null) return "";
        return switch (role) {
            case "ADMIN" -> "Quản trị viên";
            case "MANAGER" -> "Quản lý";
            case "RECEPTION" -> "Lễ tân";
            default -> role;
        };
    }

    private static final Map<String, String> TITLES = Map.ofEntries(
            Map.entry("dashboard", "Tổng quan"),
            Map.entry("rooms",     "Sơ đồ phòng"),
            Map.entry("roomtypes", "Loại phòng"),
            Map.entry("bookings",  "Đặt phòng"),
            Map.entry("customers", "Khách hàng"),
            Map.entry("invoices",  "Hóa đơn"),
            Map.entry("services",  "Dịch vụ"),
            Map.entry("promotions","Khuyến mãi"),
            Map.entry("staff",     "Nhân viên"),
            Map.entry("shifts",    "Ca làm việc"),
            Map.entry("assignments","Phân ca làm việc"),
            Map.entry("timekeeping","Chấm công"),
            Map.entry("users",     "Tài khoản hệ thống")
    );

    public void goTo(String key) {
        navButtons.forEach((k, b) -> b.getStyleClass().remove("active"));
        if (navButtons.containsKey(key)) navButtons.get(key).getStyleClass().add("active");
        pageTitle.setText(TITLES.getOrDefault(key, ""));

        Node view = switch (key) {
            case "rooms"       -> RoomsView.build(this);
            case "roomtypes"   -> RoomTypesView.build();
            case "bookings"    -> BookingsView.build();
            case "customers"   -> CustomersView.build();
            case "invoices"    -> InvoicesView.build();
            case "services"    -> ServicesView.build();
            case "staff"       -> StaffView.build();
            case "users"       -> UsersView.build();
            case "promotions", "shifts", "assignments", "timekeeping" -> new javafx.scene.layout.VBox(new javafx.scene.control.Label("Đang phát triển..."));
            default            -> DashboardView.build();
        };
        content.getChildren().setAll(view);
    }
}
