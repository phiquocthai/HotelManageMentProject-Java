package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Staff;
import com.lotusstay.javafx.net.ApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Giao diện Tài khoản hệ thống — Chuẩn Dark Luxury Theme:
 * Nền than chì #141A24, chữ trắng sáng #FFFFFF, badge vai trò và trạng thái sắc nét.
 */
public class TaiKhoanView {

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: transparent;");

        VBox card = new VBox(20);
        card.getStyleClass().add("panel");
        VBox.setVgrow(card, Priority.ALWAYS);

        // ── Header ────────────────────────────────────────────────────────
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Tài khoản hệ thống");
        title.getStyleClass().add("page-heading");

        Label sub = new Label("Danh sách tài khoản đăng nhập người dùng, phân quyền truy cập và trạng thái hoạt động");
        sub.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(title, sub);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button addBtn = new Button("Thêm tài khoản");
        addBtn.setGraphic(IconUtils.icon("plus", 14, Color.web("#0A0D14")));
        addBtn.setGraphicTextGap(8);
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 13.5px; -fx-font-weight: 800; -fx-padding: 9 22; -fx-background-color: #F59E0B; -fx-text-fill: #0A0D14; -fx-background-radius: 8; -fx-cursor: hand;");

        headerRow.getChildren().addAll(titleBox, headerSpacer, addBtn);

        // ── Table ─────────────────────────────────────────────────────────
        ObservableList<Staff> data = FXCollections.observableArrayList();

        TableView<Staff> table = new TableView<>(data);
        table.getStyleClass().add("table-view");
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có tài khoản nào."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Staff, String> userCol = new TableColumn<>("TÊN ĐĂNG NHẬP");
        userCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().username != null ? "@" + d.getValue().username : "—"));
        userCol.prefWidthProperty().bind(table.widthProperty().multiply(0.16));
        userCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 14px; -fx-padding: 12 16;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Staff, String> nameCol = new TableColumn<>("HỌ VÀ TÊN");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().fullName != null ? d.getValue().fullName : "—"));
        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        nameCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 14px; -fx-text-fill: #FFFFFF; -fx-font-weight: 700; -fx-padding: 12 16;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Staff, String> emailCol = new TableColumn<>("EMAIL");
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().email != null ? d.getValue().email : "—"));
        emailCol.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        emailCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 14px; -fx-text-fill: #CBD5E1; -fx-padding: 12 16;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Staff, String> phoneCol = new TableColumn<>("SĐT");
        phoneCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().phone != null ? d.getValue().phone : "—"));
        phoneCol.prefWidthProperty().bind(table.widthProperty().multiply(0.13));
        phoneCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 14px; -fx-text-fill: #CBD5E1; -fx-padding: 12 16;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        // Cột Vai trò
        TableColumn<Staff, String> roleCol = new TableColumn<>("VAI TRÒ");
        roleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().role));
        roleCol.prefWidthProperty().bind(table.widthProperty().multiply(0.11));
        roleCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); setText(null); return; }
                Label badge = new Label(roleLabel(item));
                badge.getStyleClass().add("badge");
                badge.getStyleClass().add(roleBadgeClass(item));
                badge.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 12;");
                setGraphic(badge);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setStyle("-fx-padding: 12 16;");
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        // Cột Trạng thái
        TableColumn<Staff, String> statusCol = new TableColumn<>("TRẠNG THÁI");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status));
        statusCol.prefWidthProperty().bind(table.widthProperty().multiply(0.11));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); setText(null); return; }
                boolean active = "ACTIVE".equalsIgnoreCase(item);
                Label badge = new Label(active ? "● HOẠT ĐỘNG" : "● ĐÃ KHÓA");
                badge.getStyleClass().addAll("badge", active ? "badge-success" : "badge-danger");
                badge.setStyle("-fx-font-size: 11.5px; -fx-font-weight: 700; -fx-padding: 4 12;");
                setGraphic(badge);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setStyle("-fx-padding: 12 16;");
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        TableColumn<Staff, Void> actionCol = new TableColumn<>("THAO TÁC");
        actionCol.setMinWidth(110);
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.10));

        table.getColumns().addAll(userCol, nameCol, emailCol, phoneCol, roleCol, statusCol, actionCol);

        // ── Reload ────────────────────────────────────────────────────────
        Runnable reload = () -> Util.runAsync(ApiService::staff, data::setAll);

        // ── Action cell ───────────────────────────────────────────────────
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button lockBtn = new Button();

            {
                lockBtn.setMinWidth(85);
                lockBtn.setOnAction(e -> {
                    Staff s = getTableView().getItems().get(getIndex());
                    Util.runAsync(() -> ApiService.toggleStaffStatus(s.id), res -> reload.run());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Staff s = getTableView().getItems().get(getIndex());
                boolean active = "ACTIVE".equalsIgnoreCase(s.status);
                lockBtn.setText(active ? "Khóa" : "Mở khóa");
                lockBtn.setGraphic(IconUtils.icon(active ? "lock" : "unlock", 11, active ? Color.web("#F87171") : Color.WHITE));
                lockBtn.setGraphicTextGap(6);
                lockBtn.getStyleClass().clear();
                lockBtn.getStyleClass().add(active ? "btn-action-del" : "btn-action-edit");
                setGraphic(lockBtn);
                setStyle("-fx-padding: 8 16;");
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        addBtn.setOnAction(e -> openAddDialog(reload));
        reload.run();

        card.getChildren().addAll(headerRow, table);
        root.setCenter(card);
        return root;
    }

    // ── Dialog thêm tài khoản (Dark Modal) ────────────────────────────────
    private static void openAddDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("LotusStay — Thêm tài khoản hệ thống");
        applyTheme(dialog);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Tên đăng nhập (VD: letan04)");
        usernameField.setPrefHeight(38);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu khởi tạo (mặc định: 123456)");
        passwordField.setPrefHeight(38);

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Họ và tên nhân viên (*)");
        fullNameField.setPrefHeight(38);

        TextField emailField = new TextField();
        emailField.setPromptText("email@hotel.vn");
        emailField.setPrefHeight(38);

        TextField phoneField = new TextField();
        phoneField.setPromptText("0901 234 567");
        phoneField.setPrefHeight(38);

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Quản trị viên (ADMIN)", "Quản lý (MANAGER)", "Lễ tân (RECEPTION)");
        roleBox.setValue("Lễ tân (RECEPTION)");
        roleBox.setPrefHeight(38);
        roleBox.setMaxWidth(Double.MAX_VALUE);

        VBox modalContent = new VBox(16);
        modalContent.setPadding(new Insets(24));
        modalContent.setPrefWidth(500);
        modalContent.setStyle("-fx-background-color: #141A24;");

        HBox modalHeader = new HBox(12);
        modalHeader.setAlignment(Pos.CENTER_LEFT);

        Label iconBadge = new Label("👤");
        iconBadge.setStyle("-fx-font-size: 20px; -fx-background-color: rgba(245, 158, 11, 0.18); -fx-padding: 8 12; -fx-background-radius: 10;");

        VBox titleArea = new VBox(3);
        Label hTitle = new Label("Tạo tài khoản người dùng mới");
        hTitle.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");
        Label hSub = new Label("Cấp quyền truy cập hệ thống quản trị LotusStay");
        hSub.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-text-fill: #94A3B8;");
        titleArea.getChildren().addAll(hTitle, hSub);
        modalHeader.getChildren().addAll(iconBadge, titleArea);

        VBox formBody = new VBox(12);
        formBody.getChildren().addAll(
                buildFormField("Tên đăng nhập (*)", usernameField),
                buildFormField("Mật khẩu", passwordField),
                buildFormField("Họ và tên (*)", fullNameField),
                buildFormField("Email", emailField),
                buildFormField("Số điện thoại", phoneField),
                buildFormField("Vai trò phân quyền (*)", roleBox)
        );

        modalContent.getChildren().addAll(modalHeader, new Separator(), formBody);
        dialog.getDialogPane().setContent(modalContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okBtn != null) {
            okBtn.setText("Tạo tài khoản");
            okBtn.getStyleClass().add("btn-gold");
            okBtn.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-weight: 800; -fx-background-color: #F59E0B; -fx-text-fill: #0A0D14;");
        }
        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelBtn != null) {
            cancelBtn.setText("Hủy bỏ");
            cancelBtn.setStyle("-fx-text-fill: #94A3B8;");
        }

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String u  = usernameField.getText().trim();
                String p  = passwordField.getText().trim();
                String fn = fullNameField.getText().trim();
                String em = emailField.getText().trim();
                String ph = phoneField.getText().trim();
                String rv = roleBox.getValue();
                if (u.isEmpty() || fn.isEmpty()) {
                    Util.info("Vui lòng nhập Tên đăng nhập và Họ tên.");
                    return null;
                }
                String role = roleFromDisplay(rv);
                String jt   = roleLabel(role);
                Util.runAsync(() -> ApiService.createStaff(u, p, fn, em, ph, jt, role, ""), s -> reload.run());
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static VBox buildFormField(String labelText, Control inputControl) {
        VBox box = new VBox(6);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #E2E8F0;");
        box.getChildren().addAll(label, inputControl);
        return box;
    }

    private static String roleLabel(String role) {
        if (role == null) return "Nhân viên";
        return switch (role.toUpperCase()) {
            case "ADMIN"     -> "Quản trị viên";
            case "MANAGER"   -> "Quản lý";
            case "RECEPTION" -> "Lễ tân";
            default -> role;
        };
    }

    private static String roleBadgeClass(String role) {
        if (role == null) return "badge-reception";
        return switch (role.toUpperCase()) {
            case "ADMIN"     -> "badge-purple";
            case "MANAGER"   -> "badge-blue";
            case "RECEPTION" -> "badge-teal";
            default -> "badge-teal";
        };
    }

    private static String roleFromDisplay(String boxVal) {
        if (boxVal == null) return "RECEPTION";
        if (boxVal.contains("ADMIN")) return "ADMIN";
        if (boxVal.contains("MANAGER")) return "MANAGER";
        return "RECEPTION";
    }

    private static void applyTheme(Dialog<?> d) {
        d.getDialogPane().getStylesheets().add(
                TaiKhoanView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());
    }
}
