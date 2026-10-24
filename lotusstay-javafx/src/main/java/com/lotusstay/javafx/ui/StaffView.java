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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Giao diện Quản lý Nhân sự & Tài khoản (tích hợp chung).
 * Hiển thị chức vụ (Admin, Quản lý, Lễ tân) chuẩn theo thiết kế.
 */
public class StaffView {

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: transparent;");

        VBox card = new VBox(16);
        card.getStyleClass().add("panel");
        VBox.setVgrow(card, Priority.ALWAYS);

        // Header info
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Quản lý nhân sự & Tài khoản");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 700;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        headerRow.getChildren().addAll(title, topSpacer);

        // Data collection
        ObservableList<Staff> masterData = FXCollections.observableArrayList();
        ObservableList<Staff> filteredData = FXCollections.observableArrayList();

        // ── TOP TOOLBAR ───────────────────────────────────────────────────────
        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Tìm theo tên đăng nhập, họ tên, email, SĐT...");
        searchField.setPrefWidth(280);
        searchField.setPrefHeight(38);

        ComboBox<String> roleFilter = new ComboBox<>();
        roleFilter.getItems().addAll("Tất cả chức vụ", "Admin", "Quản lý", "Lễ tân");
        roleFilter.setValue("Tất cả chức vụ");
        roleFilter.setPrefHeight(38);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Tất cả trạng thái", "Đang hoạt động", "Đã khóa");
        statusFilter.setValue("Tất cả trạng thái");
        statusFilter.setPrefHeight(38);

        Button searchBtn = new Button("Tìm kiếm");
        searchBtn.getStyleClass().add("btn-outline");
        searchBtn.setGraphic(IconUtils.icon("search", 13, javafx.scene.paint.Color.web("#94A3B8")));
        searchBtn.setPrefHeight(38);

        Button resetBtn = new Button("Làm mới");
        resetBtn.getStyleClass().add("btn-outline");
        resetBtn.setGraphic(IconUtils.icon("refresh", 13, javafx.scene.paint.Color.web("#94A3B8")));
        resetBtn.setPrefHeight(38);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("Thêm nhân viên");
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setGraphic(IconUtils.icon("plus", 14, javafx.scene.paint.Color.web("#0A0D14")));
        addBtn.setPrefHeight(38);

        Button toggleBtn = new Button("Khóa / Mở khóa");
        toggleBtn.getStyleClass().add("btn-outline");
        toggleBtn.setGraphic(IconUtils.icon("lock", 13, javafx.scene.paint.Color.web("#94A3B8")));
        toggleBtn.setPrefHeight(38);

        Button delSelectedBtn = new Button("Xóa đã chọn");
        delSelectedBtn.getStyleClass().add("btn-danger");
        delSelectedBtn.setGraphic(IconUtils.icon("trash", 13, javafx.scene.paint.Color.web("#EF4444")));
        delSelectedBtn.setPrefHeight(38);

        HBox toolbar = new HBox(10, searchField, roleFilter, statusFilter, searchBtn, resetBtn, spacer, toggleBtn, delSelectedBtn, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // ── TABLE VIEW ────────────────────────────────────────────────────────
        TableView<Staff> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setItems(filteredData);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Không tìm thấy nhân viên nào phù hợp."));

        TableColumn<Staff, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(75);
        idCol.setPrefWidth(85);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().id)));
        idCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER; -fx-font-size: 13.5px; -fx-text-fill: #FFFFFF;");

        TableColumn<Staff, String> codeCol = new TableColumn<>("Mã NV/TK");
        codeCol.setMinWidth(115);
        codeCol.setPrefWidth(125);
        codeCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().code != null ? d.getValue().code : String.format("TK%03d", d.getValue().id)));
        codeCol.setStyle("-fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 13.5px;");

        TableColumn<Staff, String> userCol = new TableColumn<>("Tên đăng nhập");
        userCol.setMinWidth(140);
        userCol.setPrefWidth(160);
        userCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().username != null ? "@" + d.getValue().username : "—"));
        userCol.setStyle("-fx-font-weight: 700; -fx-text-fill: #38BDF8;");

        TableColumn<Staff, String> nameCol = new TableColumn<>("Họ và tên");
        nameCol.setMinWidth(180);
        nameCol.setPrefWidth(200);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().fullName != null ? d.getValue().fullName : "—"));
        nameCol.setStyle("-fx-font-weight: 800;");

        // Cột Chức danh (Entity field: jobTitle)
        TableColumn<Staff, String> jobTitleCol = new TableColumn<>("Chức danh");
        jobTitleCol.setMinWidth(135);
        jobTitleCol.setPrefWidth(145);
        jobTitleCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().jobTitle != null && !d.getValue().jobTitle.isBlank() ? d.getValue().jobTitle : roleDisplay(d.getValue().role)));
        jobTitleCol.setStyle("-fx-font-weight: 600; -fx-text-fill: #CBD5E1;");

        // Cột Vai trò / Quyền hạn (Entity field: role)
        TableColumn<Staff, String> roleCol = new TableColumn<>("Vai trò");
        roleCol.setMinWidth(135);
        roleCol.setPrefWidth(145);
        roleCol.setCellValueFactory(d -> new SimpleStringProperty(roleDisplay(d.getValue().role)));
        roleCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge");
                    badge.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 12;");
                    if (item.equalsIgnoreCase("Admin") || item.equalsIgnoreCase("Quản trị viên")) {
                        badge.getStyleClass().add("badge-dat_truoc");
                    } else if (item.equalsIgnoreCase("Quản lý")) {
                        badge.getStyleClass().add("badge-dang_o");
                    } else {
                        badge.getStyleClass().add("badge-trong");
                    }
                    setGraphic(badge);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Staff, String> phoneCol = new TableColumn<>("SĐT");
        phoneCol.setMinWidth(125);
        phoneCol.setPrefWidth(135);
        phoneCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().phone != null ? d.getValue().phone : "—"));
        phoneCol.setStyle("-fx-font-weight: 600;");

        TableColumn<Staff, String> emailCol = new TableColumn<>("Email");
        emailCol.setMinWidth(190);
        emailCol.setPrefWidth(220);
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().email != null ? d.getValue().email : "—"));

        // Cột Ca làm việc (Entity field: shift)
        TableColumn<Staff, String> shiftCol = new TableColumn<>("Ca làm việc");
        shiftCol.setMinWidth(130);
        shiftCol.setPrefWidth(140);
        shiftCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().shift != null && !d.getValue().shift.isBlank() ? d.getValue().shift : "—"));

        TableColumn<Staff, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setMinWidth(120);
        statusCol.setPrefWidth(130);
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    boolean active = "ACTIVE".equalsIgnoreCase(item);
                    Label badge = new Label(active ? "Hoạt động" : "Đã khóa");
                    badge.getStyleClass().addAll("badge", active ? "badge-trong" : "badge-bao_tri");
                    badge.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 12;");
                    setGraphic(badge);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Staff, String> dateCol = new TableColumn<>("Ngày tạo");
        dateCol.setMinWidth(110);
        dateCol.setPrefWidth(120);
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().createdAt != null ? d.getValue().createdAt : "—"));

        TableColumn<Staff, Void> actionCol = new TableColumn<>("Thao tác");
        actionCol.setMinWidth(225);
        actionCol.setPrefWidth(235);

        table.getColumns().addAll(idCol, codeCol, userCol, nameCol, jobTitleCol, roleCol, phoneCol, emailCol, shiftCol, statusCol, dateCol, actionCol);

        // ── FILTER LOGIC ──────────────────────────────────────────────────────
        Runnable applyFilters = () -> {
            String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
            String roleVal = roleFilter.getValue();
            String statusVal = statusFilter.getValue();

            List<Staff> res = masterData.stream().filter(s -> {
                boolean matchesQuery = query.isEmpty()
                        || (s.username != null && s.username.toLowerCase().contains(query))
                        || (s.fullName != null && s.fullName.toLowerCase().contains(query))
                        || (s.email != null && s.email.toLowerCase().contains(query))
                        || (s.phone != null && s.phone.toLowerCase().contains(query))
                        || (s.code != null && s.code.toLowerCase().contains(query));

                boolean matchesRole = true;
                if ("Admin".equals(roleVal)) matchesRole = "ADMIN".equalsIgnoreCase(s.role);
                else if ("Quản lý".equals(roleVal)) matchesRole = "MANAGER".equalsIgnoreCase(s.role);
                else if ("Lễ tân".equals(roleVal)) matchesRole = "RECEPTION".equalsIgnoreCase(s.role);

                boolean matchesStatus = true;
                if ("Đang hoạt động".equals(statusVal)) matchesStatus = "ACTIVE".equalsIgnoreCase(s.status);
                else if ("Đã khóa".equals(statusVal)) matchesStatus = "INACTIVE".equalsIgnoreCase(s.status);

                return matchesQuery && matchesRole && matchesStatus;
            }).collect(Collectors.toList());

            filteredData.setAll(res);
        };

        Runnable reload = () -> Util.runAsync(ApiService::staff, list -> {
            masterData.setAll(list);
            applyFilters.run();
        });

        searchBtn.setOnAction(e -> applyFilters.run());
        searchField.setOnAction(e -> applyFilters.run());
        roleFilter.setOnAction(e -> applyFilters.run());
        statusFilter.setOnAction(e -> applyFilters.run());

        resetBtn.setOnAction(e -> {
            searchField.clear();
            roleFilter.setValue("Tất cả chức vụ");
            statusFilter.setValue("Tất cả trạng thái");
            applyFilters.run();
        });

        // ── ACTIONS ───────────────────────────────────────────────────────────
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Sửa");
            private final Button lockBtn = new Button("Khóa");
            private final Button delBtn = new Button("Xóa");
            private final HBox box = new HBox(6, editBtn, lockBtn, delBtn);

            {
                editBtn.getStyleClass().add("btn-outline");
                editBtn.setGraphic(IconUtils.icon("edit", 11, javafx.scene.paint.Color.web("#93C5FD")));
                editBtn.setGraphicTextGap(5);
                editBtn.setMinWidth(65);
                editBtn.setStyle("-fx-padding: 4 8; -fx-font-size: 11.5px; -fx-font-weight: 600; -fx-min-width: 65px;");

                lockBtn.getStyleClass().add("btn-outline");
                lockBtn.setGraphic(IconUtils.icon("lock", 11, javafx.scene.paint.Color.web("#F59E0B")));
                lockBtn.setGraphicTextGap(5);
                lockBtn.setMinWidth(65);
                lockBtn.setStyle("-fx-padding: 4 8; -fx-font-size: 11.5px; -fx-font-weight: 600; -fx-min-width: 65px;");

                delBtn.getStyleClass().add("btn-danger");
                delBtn.setGraphic(IconUtils.icon("trash", 11, javafx.scene.paint.Color.web("#F87171")));
                delBtn.setGraphicTextGap(5);
                delBtn.setMinWidth(65);
                delBtn.setStyle("-fx-padding: 4 8; -fx-font-size: 11.5px; -fx-font-weight: 600; -fx-min-width: 65px;");
                box.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    Staff s = getTableView().getItems().get(getIndex());
                    openEditDialog(s, reload);
                });

                lockBtn.setOnAction(e -> {
                    Staff s = getTableView().getItems().get(getIndex());
                    Util.runAsync(() -> ApiService.toggleStaffStatus(s.id), res -> reload.run());
                });

                delBtn.setOnAction(e -> {
                    Staff s = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Xóa tài khoản nhân viên @" + s.username + " (" + s.fullName + ")?");
                    confirm.setHeaderText("Xác nhận xóa");
                    confirm.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt ->
                            Util.runAsyncVoid(() -> ApiService.deleteStaff(s.id), reload));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Staff s = getTableView().getItems().get(getIndex());
                    boolean active = "ACTIVE".equalsIgnoreCase(s.status);
                    lockBtn.setText(active ? "Khóa" : "Mở");
                    lockBtn.setGraphic(IconUtils.icon(active ? "lock" : "unlock", 11, javafx.scene.paint.Color.web("#F59E0B")));
                    setGraphic(box);
                }
            }
        });

        addBtn.setOnAction(e -> openAddDialog(reload));

        toggleBtn.setOnAction(e -> {
            Staff sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                Util.info("Vui lòng chọn 1 nhân viên trong bảng để khóa / mở khóa.");
                return;
            }
            Util.runAsync(() -> ApiService.toggleStaffStatus(sel.id), res -> reload.run());
        });

        delSelectedBtn.setOnAction(e -> {
            Staff sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                Util.info("Vui lòng chọn 1 nhân viên trong bảng để xóa.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Xóa tài khoản nhân viên @" + sel.username + " (" + sel.fullName + ")?");
            confirm.setHeaderText("Xác nhận xóa mục đã chọn");
            confirm.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt ->
                    Util.runAsyncVoid(() -> ApiService.deleteStaff(sel.id), reload));
        });

        // Initial load
        reload.run();

        card.getChildren().addAll(headerRow, toolbar, table);
        root.setCenter(card);
        return root;
    }

    // ── DIALOG THÊM NHÂN VIÊN ─────────────────────────────────────────────────
    private static void openAddDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Thêm nhân viên & Tài khoản mới");
        dialog.setHeaderText("Tạo hồ sơ nhân viên cùng tài khoản đăng nhập hệ thống");
        dialog.getDialogPane().getStylesheets().add(
                StaffView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        TextField username = new TextField();
        username.setPromptText("Tên đăng nhập (VD: letan04)");

        PasswordField password = new PasswordField();
        password.setPromptText("Mật khẩu ban đầu (mặc định: 123456)");

        TextField fullName = new TextField();
        fullName.setPromptText("Họ và tên nhân viên (*)");

        TextField email = new TextField();
        email.setPromptText("Email (VD: letan04@hotel.vn)");

        TextField phone = new TextField();
        phone.setPromptText("VD: 0901 234 567");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Quản trị viên (ADMIN)", "Quản lý (MANAGER)", "Lễ tân (RECEPTION)");
        roleBox.setValue("Lễ tân (RECEPTION)");
        roleBox.setMaxWidth(Double.MAX_VALUE);

        TextField shift = new TextField();
        shift.setPromptText("VD: Ca sáng (6h–14h), Ca chiều (14h–22h)...");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16, 20, 16, 20));

        grid.add(new Label("Tên đăng nhập (*):"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Mật khẩu:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(new Label("Họ và tên (*):"), 0, 2);
        grid.add(fullName, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(email, 1, 3);
        grid.add(new Label("Số điện thoại:"), 0, 4);
        grid.add(phone, 1, 4);
        grid.add(new Label("Chức vụ (*):"), 0, 5);
        grid.add(roleBox, 1, 5);
        grid.add(new Label("Ca làm việc:"), 0, 6);
        grid.add(shift, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String u = username.getText().trim();
                String p = password.getText().trim();
                String fn = fullName.getText().trim();
                String em = email.getText().trim();
                String ph = phone.getText().trim();
                String sh = shift.getText().trim();
                String role = roleFromDisplay(roleBox.getValue());
                String jt = roleDisplay(role);

                if (u.isEmpty() || fn.isEmpty()) {
                    Util.info("Vui lòng nhập đầy đủ Tên đăng nhập và Họ tên.");
                    return null;
                }

                Util.runAsync(() -> ApiService.createStaff(u, p, fn, em, ph, jt, role, sh), s -> reload.run());
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ── DIALOG CẬP NHẬT NHÂN VIÊN ─────────────────────────────────────────────
    private static void openEditDialog(Staff staff, Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Cập nhật thông tin nhân viên");
        dialog.setHeaderText("Chỉnh sửa nhân viên & tài khoản: @" + staff.username);
        dialog.getDialogPane().getStylesheets().add(
                StaffView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        TextField username = new TextField(staff.username != null ? staff.username : "");
        TextField fullName = new TextField(staff.fullName != null ? staff.fullName : "");
        TextField email = new TextField(staff.email != null ? staff.email : "");
        TextField phone = new TextField(staff.phone != null ? staff.phone : "");
        TextField shift = new TextField(staff.shift != null ? staff.shift : "");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Quản trị viên (ADMIN)", "Quản lý (MANAGER)", "Lễ tân (RECEPTION)");
        roleBox.setValue(roleToBoxValue(staff.role));
        roleBox.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16, 20, 16, 20));

        grid.add(new Label("Tên đăng nhập (*):"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Họ và tên (*):"), 0, 1);
        grid.add(fullName, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(email, 1, 2);
        grid.add(new Label("Số điện thoại:"), 0, 3);
        grid.add(phone, 1, 3);
        grid.add(new Label("Chức vụ (*):"), 0, 4);
        grid.add(roleBox, 1, 4);
        grid.add(new Label("Ca làm việc:"), 0, 5);
        grid.add(shift, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String u = username.getText().trim();
                String fn = fullName.getText().trim();
                String em = email.getText().trim();
                String ph = phone.getText().trim();
                String sh = shift.getText().trim();
                String role = roleFromDisplay(roleBox.getValue());
                String jt = roleDisplay(role);

                if (u.isEmpty() || fn.isEmpty()) {
                    Util.info("Vui lòng nhập đầy đủ Tên đăng nhập và Họ tên.");
                    return null;
                }

                Util.runAsync(() -> ApiService.updateStaff(staff.id, u, fn, em, ph, jt, role, sh), s -> reload.run());
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static String roleDisplay(String role) {
        if (role == null) return "Nhân viên";
        return switch (role.toUpperCase()) {
            case "ADMIN" -> "Admin";
            case "MANAGER" -> "Quản lý";
            case "RECEPTION" -> "Lễ tân";
            default -> role;
        };
    }

    private static String roleFromDisplay(String boxVal) {
        if (boxVal == null) return "RECEPTION";
        if (boxVal.contains("ADMIN")) return "ADMIN";
        if (boxVal.contains("MANAGER")) return "MANAGER";
        return "RECEPTION";
    }

    private static String roleToBoxValue(String role) {
        if (role == null) return "Lễ tân (RECEPTION)";
        return switch (role.toUpperCase()) {
            case "ADMIN" -> "Quản trị viên (ADMIN)";
            case "MANAGER" -> "Quản lý (MANAGER)";
            default -> "Lễ tân (RECEPTION)";
        };
    }
}
