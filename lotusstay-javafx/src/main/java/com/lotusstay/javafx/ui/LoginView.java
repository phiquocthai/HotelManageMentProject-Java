package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.MainApp;
import com.lotusstay.javafx.net.ApiClient;
import com.lotusstay.javafx.net.ApiService;
import com.lotusstay.javafx.net.Session;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.InputStream;

/**
 * LoginView — Giao diện đăng nhập chuẩn Luxury 5-Star Hotel.
 * Tông màu chủ đạo: Đen tuyền (Obsidian #070A10), xám đá phiến thanh lịch,
 * tô điểm bằng ánh vàng vương giả (#F59E0B / #FBBF24 / #FCD34D),
 * kết hợp typography đẳng cấp và biểu tượng vector sắc nét.
 */
public class LoginView {

    public static StackPane build() {
        StackPane root = new StackPane();
        root.getStyleClass().add("login-root");
        root.setStyle("-fx-background-color: #070A10;");

        // ── 1. BACKGROUND IMAGE (login_bg.jpg) ────────────────────────────────
        try {
            InputStream bgStream = LoginView.class.getResourceAsStream("/com/lotusstay/javafx/images/login_bg.jpg");
            if (bgStream != null) {
                Image bgImage = new Image(bgStream);
                BackgroundImage bImage = new BackgroundImage(
                        bgImage,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
                );
                root.setBackground(new Background(bImage));
            } else {
                root.setStyle("-fx-background-color: radial-gradient(focus-distance 0%, center 50% 50%, radius 70%, #151D2A, #070A10);");
            }
        } catch (Exception e) {
            root.setStyle("-fx-background-color: radial-gradient(focus-distance 0%, center 50% 50%, radius 70%, #151D2A, #070A10);");
        }

        // ── 2. DARK LUXURY OVERLAY WITH GOLDEN AMBIENCE ───────────────────────
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: radial-gradient(focus-distance 0%, center 50% 50%, radius 75%, rgba(15, 23, 42, 0.72), rgba(7, 10, 16, 0.95));");

        // ── 3. MAIN GLASSMORPHISM CARD CONTAINER ─────────────────────────────
        HBox container = new HBox();
        container.setMaxWidth(990);
        container.setMaxHeight(590);
        container.setStyle("-fx-background-color: rgba(14, 19, 30, 0.94); " +
                "-fx-background-radius: 22; " +
                "-fx-border-radius: 22; " +
                "-fx-border-color: rgba(245, 158, 11, 0.40); " +
                "-fx-border-width: 1.2; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.88), 45, 0, 0, 16);");

        // ── LEFT BRAND & PRESTIGE PANEL ───────────────────────────────────────
        VBox leftPanel = new VBox(22);
        leftPanel.setPrefWidth(435);
        leftPanel.setMinWidth(380);
        leftPanel.setPadding(new Insets(38, 36, 36, 38));
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(245, 158, 11, 0.08) 0%, rgba(15, 23, 42, 0.65) 100%); " +
                "-fx-background-radius: 22 0 0 22; " +
                "-fx-border-color: transparent rgba(245, 158, 11, 0.20) transparent transparent; " +
                "-fx-border-width: 0 1 0 0;");

        // ── Emblem Logo (Lotus vector + gold halo squircle) ───────────────────
        Node lotusSvg = IconUtils.icon("lotus", 32, Color.web("#FBBF24"));
        StackPane emblem = new StackPane(lotusSvg);
        emblem.setPrefSize(58, 58);
        emblem.setMinSize(58, 58);
        emblem.setMaxSize(58, 58);
        emblem.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, rgba(245, 158, 11, 0.22) 0%, rgba(180, 83, 9, 0.12) 100%); " +
                "-fx-border-color: rgba(251, 191, 36, 0.65); -fx-border-width: 1.4; -fx-border-radius: 16; -fx-background-radius: 16; " +
                "-fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.45), 18, 0, 0, 0);");

        // Brand Typography
        Label brandName = new Label("LOTUSSTAY");
        brandName.setStyle("-fx-text-fill: #FBBF24; -fx-font-family: 'Montserrat', sans-serif; " +
                "-fx-font-size: 26px; -fx-font-weight: 900; -fx-letter-spacing: 3px; " +
                "-fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.35), 8, 0, 0, 0);");

        Label brandSub = new Label("HỆ THỐNG QUẢN LÝ KHÁCH SẠN 5 SAO");
        brandSub.setStyle("-fx-text-fill: #FDE68A; -fx-font-family: 'Montserrat', sans-serif; " +
                "-fx-font-size: 10px; -fx-font-weight: 800; -fx-letter-spacing: 1.5px; " +
                "-fx-background-color: rgba(245, 158, 11, 0.14); -fx-border-color: rgba(245, 158, 11, 0.35); " +
                "-fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 3 10;");

        Label tagline = new Label("Giải pháp quản trị lưu trú toàn diện, tối ưu buồng phòng & tài khoản nhân sự chuẩn quốc tế.");
        tagline.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12.5px; -fx-line-spacing: 3.5px; -fx-font-family: 'Inter', sans-serif;");
        tagline.setWrapText(true);

        VBox brandBox = new VBox(8, emblem, brandName, brandSub, tagline);

        // Gold hairline separator
        Region goldDivider = new Region();
        goldDivider.setPrefHeight(1);
        goldDivider.setMaxHeight(1);
        goldDivider.setStyle("-fx-background-color: linear-gradient(to right, rgba(245, 158, 11, 0.6), rgba(245, 158, 11, 0.05));");

        // Prestige Feature List with Vector Icons & Gold Accent Badges
        VBox features = new VBox(11,
                featureItem("rooms", "Sơ đồ phòng đa tầng trực quan", "Theo dõi trạng thái phòng nhận/trả & dọn dẹp theo thời gian thực"),
                featureItem("bookings", "Đặt phòng & Check-in/out nhanh", "Tối ưu hóa quy trình tiếp đón khách, thanh toán và phụ phí"),
                featureItem("staff", "Quản lý nhân sự & Phân quyền", "Bảo mật tài khoản đa cấp độ, chấm công & phân ca linh hoạt"),
                featureItem("invoices", "Tự động xuất hóa đơn & Báo cáo", "Tính toán doanh thu chính xác, minh bạch và xuất báo cáo tức thì")
        );

        Region spacerLeft = new Region();
        VBox.setVgrow(spacerLeft, Priority.ALWAYS);

        // Trust badge footer with 5 gold stars
        HBox starsBox = new HBox(4);
        for (int i = 0; i < 5; i++) {
            starsBox.getChildren().add(IconUtils.icon("star-fill", 10.5, Color.web("#F59E0B")));
        }
        Label trustLabel = new Label("LotusStay PMS v2.5  ·  Enterprise 5-Star Standard");
        trustLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px; -fx-font-family: 'Inter', sans-serif;");
        VBox leftFooter = new VBox(5, starsBox, trustLabel);

        leftPanel.getChildren().addAll(brandBox, goldDivider, features, spacerLeft, leftFooter);

        // ── RIGHT LOGIN FORM PANEL ────────────────────────────────────────────
        VBox rightPanel = new VBox(20);
        rightPanel.setPadding(new Insets(38, 44, 38, 44));
        rightPanel.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // Eyebrow badge
        Label eyebrow = new Label("CỔNG TRUY CẬP HỆ THỐNG NỘI BỘ");
        eyebrow.setStyle("-fx-text-fill: #F59E0B; -fx-font-family: 'Montserrat', sans-serif; " +
                "-fx-font-size: 10.5px; -fx-font-weight: 800; -fx-letter-spacing: 1.8px;");

        Label welcomeLabel = new Label("Đăng nhập hệ thống");
        welcomeLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-family: 'Montserrat', sans-serif; " +
                "-fx-font-size: 24px; -fx-font-weight: 800;");

        Label subLabel = new Label("Vui lòng nhập tài khoản nhân viên được cấp để tiếp tục tác nghiệp.");
        subLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-font-family: 'Inter', sans-serif;");

        VBox headingBox = new VBox(5, eyebrow, welcomeLabel, subLabel);

        // ── Input Fields ──────────────────────────────────────────────────────
        String defaultFieldStyle = "-fx-background-color: rgba(9, 13, 21, 0.85); -fx-text-fill: #FFFFFF; " +
                "-fx-prompt-text-fill: #556477; -fx-border-color: #263346; -fx-border-radius: 9; -fx-background-radius: 9; " +
                "-fx-padding: 0 15; -fx-font-size: 13.5px; -fx-font-family: 'Inter', sans-serif;";

        String focusedFieldStyle = "-fx-background-color: rgba(15, 23, 42, 0.95); -fx-text-fill: #FFFFFF; " +
                "-fx-prompt-text-fill: #556477; -fx-border-color: #F59E0B; -fx-border-radius: 9; -fx-background-radius: 9; " +
                "-fx-padding: 0 15; -fx-font-size: 13.5px; -fx-font-family: 'Inter', sans-serif; " +
                "-fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.35), 10, 0, 0, 0);";

        // Username Field
        HBox userLabelBox = new HBox(7,
                IconUtils.icon("person-badge", 13, Color.web("#F59E0B")),
                createFieldLabel("Tài khoản / Mã nhân viên")
        );
        userLabelBox.setAlignment(Pos.CENTER_LEFT);

        TextField userField = new TextField();
        userField.setPromptText("VD: admin01, quanly01, letan01...");
        userField.setPrefHeight(44);
        userField.setStyle(defaultFieldStyle);
        userField.focusedProperty().addListener((obs, wasFocused, isFocused) ->
                userField.setStyle(isFocused ? focusedFieldStyle : defaultFieldStyle));

        // Password Field
        HBox passLabelBox = new HBox(7,
                IconUtils.icon("lock", 13, Color.web("#F59E0B")),
                createFieldLabel("Mật khẩu bảo mật")
        );
        passLabelBox.setAlignment(Pos.CENTER_LEFT);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Nhập mật khẩu truy cập...");
        passField.setPrefHeight(44);
        passField.setStyle(defaultFieldStyle);
        passField.focusedProperty().addListener((obs, wasFocused, isFocused) ->
                passField.setStyle(isFocused ? focusedFieldStyle : defaultFieldStyle));

        VBox userBox = new VBox(6, userLabelBox, userField);
        VBox passBox = new VBox(6, passLabelBox, passField);

        // ── Error Message Banner ──────────────────────────────────────────────
        HBox errorBox = new HBox(8);
        errorBox.setAlignment(Pos.CENTER_LEFT);
        errorBox.setStyle("-fx-background-color: rgba(239, 68, 68, 0.12); " +
                "-fx-border-color: rgba(239, 68, 68, 0.45); -fx-border-radius: 9; -fx-background-radius: 9; -fx-padding: 9 13;");
        Node warnIcon = IconUtils.icon("warning", 15, Color.web("#F87171"));
        Label errorMsg = new Label();
        errorMsg.setStyle("-fx-text-fill: #FCA5A5; -fx-font-size: 12.5px; -fx-font-weight: 500; -fx-font-family: 'Inter', sans-serif;");
        errorMsg.setWrapText(true);
        HBox.setHgrow(errorMsg, Priority.ALWAYS);
        errorBox.getChildren().addAll(warnIcon, errorMsg);
        errorBox.setVisible(false);
        errorBox.setManaged(false);

        // ── Radiant Gold Login Button ─────────────────────────────────────────
        Button loginBtn = new Button("ĐĂNG NHẬP VÀO HỆ THỐNG");
        loginBtn.setPrefHeight(46);
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        String btnNormal = "-fx-background-color: linear-gradient(to right, #F59E0B 0%, #D97706 100%); " +
                "-fx-text-fill: #0A0D14; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 13.5px; -fx-font-weight: 800; -fx-letter-spacing: 1.2px; " +
                "-fx-border-color: #FCD34D; -fx-border-width: 1; -fx-border-radius: 9; -fx-background-radius: 9; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.45), 16, 0, 0, 3);";

        String btnHover = "-fx-background-color: linear-gradient(to right, #FBBF24 0%, #F59E0B 100%); " +
                "-fx-text-fill: #0A0D14; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 13.5px; -fx-font-weight: 800; -fx-letter-spacing: 1.2px; " +
                "-fx-border-color: #FEF08A; -fx-border-width: 1; -fx-border-radius: 9; -fx-background-radius: 9; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.65), 22, 0, 0, 4);";

        String btnPressed = "-fx-background-color: linear-gradient(to right, #D97706 0%, #B45309 100%); " +
                "-fx-text-fill: #0A0D14; -fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 13.5px; -fx-font-weight: 800; -fx-letter-spacing: 1.2px; " +
                "-fx-border-color: #F59E0B; -fx-border-width: 1; -fx-border-radius: 9; -fx-background-radius: 9; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.25), 10, 0, 0, 2);";

        loginBtn.setStyle(btnNormal);
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(btnHover));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(btnNormal));
        loginBtn.setOnMousePressed(e -> loginBtn.setStyle(btnPressed));
        loginBtn.setOnMouseReleased(e -> loginBtn.setStyle(loginBtn.isHover() ? btnHover : btnNormal));

        // ── Quick Demo Login Chips ────────────────────────────────────────────
        Label hintTitle = new Label("Tài khoản mẫu (Click để chọn nhanh):");
        hintTitle.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 11.5px; -fx-font-weight: 600; -fx-font-family: 'Inter', sans-serif;");

        Button fillAdmin = createDemoChip("Quản trị viên", "admin01 / admin123", "admin01", "admin123", userField, passField);
        Button fillReception = createDemoChip("Lễ tân", "letan01 / letan123", "letan01", "letan123", userField, passField);

        HBox hintBox = new HBox(8, fillAdmin, fillReception);
        hintBox.setAlignment(Pos.CENTER_LEFT);
        VBox quickHints = new VBox(7, hintTitle, hintBox);

        // ── Login Logic Execution ─────────────────────────────────────────────
        Runnable doLogin = () -> {
            errorBox.setVisible(false);
            errorBox.setManaged(false);
            String u = userField.getText().trim();
            String p = passField.getText();
            if (u.isEmpty() || p.isEmpty()) {
                errorMsg.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
                errorBox.setVisible(true);
                errorBox.setManaged(true);
                return;
            }
            loginBtn.setDisable(true);
            loginBtn.setText("ĐANG XÁC THỰC...");
            new Thread(() -> {
                try {
                    var res = ApiService.login(u, p);
                    Session.set(res.token, res.userId, res.username, res.fullName, res.role);
                    Platform.runLater(() -> {
                        Scene shellScene = new Scene(MainShell.build(), 1280, 780);
                        shellScene.setFill(javafx.scene.paint.Color.web("#0A0D14"));
                        shellScene.getStylesheets().add(
                                MainApp.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());
                        MainApp.primaryStage.setScene(shellScene);
                        MainApp.primaryStage.setMaximized(true);
                    });
                } catch (ApiClient.ApiException ex) {
                    Platform.runLater(() -> {
                        errorMsg.setText(ex.getMessage());
                        errorBox.setVisible(true);
                        errorBox.setManaged(true);
                        loginBtn.setDisable(false);
                        loginBtn.setText("ĐĂNG NHẬP VÀO HỆ THỐNG");
                    });
                }
            }).start();
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passField.setOnAction(e -> doLogin.run());
        userField.setOnAction(e -> passField.requestFocus());

        rightPanel.getChildren().addAll(headingBox, userBox, passBox, errorBox, loginBtn, quickHints);

        container.getChildren().addAll(leftPanel, rightPanel);
        overlay.getChildren().add(container);
        root.getChildren().addAll(overlay);

        return root;
    }

    private static Label createFieldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #E2E8F0; -fx-font-size: 12.5px; -fx-font-weight: 600; -fx-font-family: 'Inter', sans-serif;");
        return l;
    }

    private static HBox featureItem(String iconName, String title, String subtitle) {
        Node iconNode = IconUtils.icon(iconName, 13.5, Color.web("#FBBF24"));
        StackPane iconBadge = new StackPane(iconNode);
        iconBadge.setPrefSize(28, 28);
        iconBadge.setMinSize(28, 28);
        iconBadge.setMaxSize(28, 28);
        iconBadge.setStyle("-fx-background-color: rgba(245, 158, 11, 0.12); " +
                "-fx-border-color: rgba(245, 158, 11, 0.30); -fx-border-radius: 7; -fx-background-radius: 7;");

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: #F1F5F9; -fx-font-size: 12.5px; -fx-font-weight: 600; -fx-font-family: 'Inter', sans-serif;");

        Label subLbl = new Label(subtitle);
        subLbl.setStyle("-fx-text-fill: #8292A6; -fx-font-size: 11px; -fx-font-family: 'Inter', sans-serif;");

        VBox textCol = new VBox(1.5, titleLbl, subLbl);
        HBox box = new HBox(10, iconBadge, textCol);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private static Button createDemoChip(String role, String creds, String username, String password, TextField uField, PasswordField pField) {
        Button btn = new Button();
        Node keyIcon = IconUtils.icon("key", 11.5, Color.web("#F59E0B"));
        Label roleLbl = new Label(role + ":");
        roleLbl.setStyle("-fx-text-fill: #FBBF24; -fx-font-weight: 700; -fx-font-size: 11px; -fx-font-family: 'Inter', sans-serif;");
        Label credLbl = new Label(creds);
        credLbl.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 11px; -fx-font-family: 'Inter', sans-serif;");
        HBox content = new HBox(5, keyIcon, roleLbl, credLbl);
        content.setAlignment(Pos.CENTER);
        btn.setGraphic(content);

        String normal = "-fx-background-color: rgba(255, 255, 255, 0.05); -fx-border-color: rgba(255, 255, 255, 0.10); " +
                "-fx-border-radius: 7; -fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 5 10;";
        String hover = "-fx-background-color: rgba(245, 158, 11, 0.12); -fx-border-color: rgba(245, 158, 11, 0.45); " +
                "-fx-border-radius: 7; -fx-background-radius: 7; -fx-cursor: hand; -fx-padding: 5 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.25), 8, 0, 0, 0);";

        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
        btn.setOnAction(e -> {
            uField.setText(username);
            pField.setText(password);
        });
        return btn;
    }
}

