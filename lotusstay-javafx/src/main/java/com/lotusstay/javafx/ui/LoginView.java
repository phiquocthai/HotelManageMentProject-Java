package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.MainApp;
import com.lotusstay.javafx.net.ApiClient;
import com.lotusstay.javafx.net.ApiService;
import com.lotusstay.javafx.net.Session;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginView {

    public static HBox build() {
        HBox root = new HBox();
        root.getStyleClass().add("login-root");

        // ── LEFT PANEL ───────────────────────────────────────────────────
        VBox leftPanel = new VBox(28);
        leftPanel.getStyleClass().add("login-left-panel");
        leftPanel.setPrefWidth(460);
        leftPanel.setMinWidth(400);
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(leftPanel, Priority.NEVER);

        Label logoMark = new Label("🌸");
        logoMark.setStyle("-fx-font-size: 44px;");

        Label brandName = new Label("LOTUSSTAY");
        brandName.getStyleClass().add("login-brand-name");

        Label tagline = new Label("Phần mềm quản lý khách sạn\nhiện đại, chuyên nghiệp và toàn diện.");
        tagline.getStyleClass().add("login-brand-tagline");
        tagline.setWrapText(true);

        VBox brandBox = new VBox(10, logoMark, brandName, tagline);

        VBox features = new VBox(6,
                featureItem("✓  Quản lý phòng & đặt phòng trực quan"),
                featureItem("✓  Sơ đồ tầng theo thời gian thực"),
                featureItem("✓  Hóa đơn & thanh toán tích hợp"),
                featureItem("✓  Quản lý nhân viên & ca làm việc"),
                featureItem("✓  Báo cáo doanh thu đa chiều")
        );

        Region spacer1 = new Region();
        VBox.setVgrow(spacer1, Priority.ALWAYS);

        Label version = new Label("LotusStay PMS v2.0  ·  © 2025 LotusStay");
        version.setStyle("-fx-text-fill: #334155; -fx-font-size: 11px;");

        Region spacer2 = new Region();
        VBox.setVgrow(spacer2, Priority.ALWAYS);

        leftPanel.getChildren().addAll(brandBox, spacer1, features, spacer2, version);

        // ── RIGHT PANEL / LOGIN FORM ──────────────────────────────────────
        StackPane rightPanel = new StackPane();
        rightPanel.getStyleClass().add("login-right-panel");
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        VBox card = new VBox(20);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(400);

        // Heading
        Label welcomeLabel = new Label("Chào mừng trở lại 👋");
        welcomeLabel.getStyleClass().add("login-title");
        Label subLabel = new Label("Đăng nhập để tiếp tục quản lý khách sạn.");
        subLabel.getStyleClass().add("login-sub");
        VBox headingBox = new VBox(6, welcomeLabel, subLabel);

        // Fields
        Label userLabel = new Label("Tên đăng nhập");
        userLabel.getStyleClass().add("form-label");
        TextField userField = new TextField();
        userField.setPromptText("Nhập tên đăng nhập...");
        userField.setPrefHeight(42);
        userField.setMaxWidth(Double.MAX_VALUE);
        VBox userBox = new VBox(6, userLabel, userField);

        Label passLabel = new Label("Mật khẩu");
        passLabel.getStyleClass().add("form-label");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Nhập mật khẩu...");
        passField.setPrefHeight(42);
        passField.setMaxWidth(Double.MAX_VALUE);
        VBox passBox = new VBox(6, passLabel, passField);

        // Error message
        Label error = new Label();
        error.getStyleClass().add("login-error");
        error.setVisible(false);
        error.setManaged(false);
        error.setWrapText(true);
        error.setMaxWidth(Double.MAX_VALUE);

        // Login button
        Button loginBtn = new Button("Đăng nhập");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setPrefHeight(44);
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        // Hint
        Label hint = new Label("🔑  Tài khoản mẫu: admin / admin123   ·   reception / reception123");
        hint.getStyleClass().add("login-hint");
        hint.setMaxWidth(Double.MAX_VALUE);

        Runnable doLogin = () -> {
            error.setVisible(false);
            error.setManaged(false);
            String u = userField.getText().trim();
            String p = passField.getText();
            if (u.isEmpty() || p.isEmpty()) {
                error.setText("⚠  Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
                error.setVisible(true);
                error.setManaged(true);
                return;
            }
            loginBtn.setDisable(true);
            loginBtn.setText("Đang đăng nhập…");
            new Thread(() -> {
                try {
                    var res = ApiService.login(u, p);
                    Session.set(res.token, res.userId, res.username, res.fullName, res.role);
                    Platform.runLater(() -> {
                        Scene shellScene = new Scene(MainShell.build(), 1280, 780);
                        shellScene.getStylesheets().add(
                                MainApp.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());
                        MainApp.primaryStage.setScene(shellScene);
                        MainApp.primaryStage.setMaximized(true);
                    });
                } catch (ApiClient.ApiException ex) {
                    Platform.runLater(() -> {
                        error.setText("✗  " + ex.getMessage());
                        error.setVisible(true);
                        error.setManaged(true);
                        loginBtn.setDisable(false);
                        loginBtn.setText("Đăng nhập");
                    });
                }
            }).start();
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passField.setOnAction(e -> doLogin.run());
        userField.setOnAction(e -> passField.requestFocus());

        card.getChildren().addAll(headingBox, userBox, passBox, error, loginBtn, hint);
        rightPanel.getChildren().add(card);

        root.getChildren().addAll(leftPanel, rightPanel);
        return root;
    }

    private static Label featureItem(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("login-feature-item");
        return l;
    }
}
