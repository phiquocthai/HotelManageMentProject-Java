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

    private static final int DEFAULT_PAGE_SIZE = 10;

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
        searchField.setPromptText("🔍  Tìm theo ID, họ tên, email, SĐT...");
        searchField.setPrefWidth(280);
        searchField.setMinWidth(220);
        searchField.setPrefHeight(38);

        ComboBox<String> roleFilter = new ComboBox<>();
        roleFilter.getItems().addAll("Tất cả vai trò", "Quản trị viên", "Quản lý", "Lễ tân");
        roleFilter.setValue("Tất cả vai trò");
        roleFilter.setPrefHeight(38);
        roleFilter.setMinWidth(170);
        roleFilter.setPrefWidth(170);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Tất cả trạng thái", "Đang hoạt động", "Đã khóa");
        statusFilter.setValue("Tất cả trạng thái");
        statusFilter.setPrefHeight(38);
        statusFilter.setMinWidth(180);
        statusFilter.setPrefWidth(180);

        Button searchBtn = new Button("Tìm kiếm");
        searchBtn.getStyleClass().add("btn-outline");
        searchBtn.setGraphic(IconUtils.icon("search", 13, javafx.scene.paint.Color.web("#94A3B8")));
        searchBtn.setPrefHeight(38);
        searchBtn.setMinWidth(Region.USE_PREF_SIZE);
        searchBtn.setPrefWidth(115);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button delSelectedBtn = new Button("Xóa đã chọn");
        delSelectedBtn.getStyleClass().add("btn-danger");
        delSelectedBtn.setGraphic(IconUtils.icon("trash", 13, javafx.scene.paint.Color.web("#EF4444")));
        delSelectedBtn.setPrefHeight(38);
        delSelectedBtn.setMinWidth(Region.USE_PREF_SIZE);
        delSelectedBtn.setPrefWidth(130);

        Button addBtn = new Button("Thêm nhân viên");
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setGraphic(IconUtils.icon("plus", 14, javafx.scene.paint.Color.web("#0A0D14")));
        addBtn.setPrefHeight(38);
        addBtn.setMinWidth(175);
        addBtn.setPrefWidth(175);

        // Thanh toolbar chuẩn: bỏ nút Làm mới và nút Khóa/Mở khóa trên toolbar theo yêu cầu
        HBox toolbar = new HBox(12, searchField, roleFilter, statusFilter, searchBtn, spacer, delSelectedBtn, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // ── TABLE VIEW ────────────────────────────────────────────────────────
        TableView<Staff> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setItems(filteredData);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Không tìm thấy nhân viên nào phù hợp."));

        TableColumn<Staff, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(60);
        idCol.setPrefWidth(70);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().id)));
        idCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER_LEFT; -fx-font-size: 13.5px; -fx-text-fill: #FFFFFF;");

        TableColumn<Staff, String> userCol = new TableColumn<>("Tên đăng nhập");
        userCol.setMinWidth(130);
        userCol.setPrefWidth(145);
        userCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().username != null ? "@" + d.getValue().username : "—"));
        userCol.setStyle("-fx-font-weight: 700; -fx-text-fill: #38BDF8; -fx-font-size: 13.5px;");

        TableColumn<Staff, String> nameCol = new TableColumn<>("Họ và tên");
        nameCol.setMinWidth(160);
        nameCol.setPrefWidth(180);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().fullName != null ? d.getValue().fullName : "—"));
        nameCol.setStyle("-fx-font-weight: 800; -fx-font-size: 13.5px;");

        // Cột Vai trò (Entity field: role)
        TableColumn<Staff, String> roleCol = new TableColumn<>("Vai trò");
        roleCol.setMinWidth(130);
        roleCol.setPrefWidth(145);
        roleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoleDisplay()));
        roleCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    if ("Quản trị viên".equals(item)) {
                        setStyle("-fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 13px;");
                    } else if ("Quản lý".equals(item)) {
                        setStyle("-fx-font-weight: 800; -fx-text-fill: #A78BFA; -fx-font-size: 13px;");
                    } else {
                        setStyle("-fx-font-weight: 800; -fx-text-fill: #38BDF8; -fx-font-size: 13px;");
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Staff, String> phoneCol = new TableColumn<>("SĐT");
        phoneCol.setMinWidth(115);
        phoneCol.setPrefWidth(125);
        phoneCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().phone != null ? d.getValue().phone : "—"));
        phoneCol.setStyle("-fx-font-weight: 600; -fx-font-size: 13.5px;");

        TableColumn<Staff, String> emailCol = new TableColumn<>("Email");
        emailCol.setMinWidth(200);
        emailCol.setPrefWidth(220);
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().email != null ? d.getValue().email : "—"));
        emailCol.setStyle("-fx-font-size: 13.5px;");

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
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
        TableColumn<Staff, Void> actionCol = new TableColumn<>("Thao tác");
        actionCol.setMinWidth(230);
        actionCol.setPrefWidth(240);

        // Bỏ cột 'Mã NV/TK', 'Vai trò', 'Ca làm việc' và 'Ngày tạo' theo yêu cầu
        table.getColumns().addAll(idCol, userCol, nameCol, roleCol, phoneCol, emailCol, statusCol, actionCol);

        // ── Thanh phân trang Footer ───────────────────────────────────────
        HBox paginationBar = new HBox(16);
        paginationBar.setAlignment(Pos.CENTER_LEFT);
        paginationBar.setPadding(new Insets(12, 0, 4, 0));

        Label recordInfoLabel = new Label("Hiển thị 0 nhân viên");
        recordInfoLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px; -fx-text-fill: #94A3B8; -fx-font-weight: 600;");

        Label sizeLabel = new Label("Số bản ghi / trang:");
        sizeLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-text-fill: #94A3B8; -fx-font-weight: 500;");

        ComboBox<Integer> pageSizeCombo = new ComboBox<>(FXCollections.observableArrayList(5, 10, 15, 20));
        pageSizeCombo.setValue(DEFAULT_PAGE_SIZE);
        pageSizeCombo.setStyle(
                "-fx-background-color: #1E293B; -fx-border-color: rgba(148, 163, 184, 0.3); " +
                "-fx-border-radius: 6; -fx-background-radius: 6; -fx-pref-height: 28px; -fx-font-size: 12px; -fx-cursor: hand;"
        );

        HBox leftPagingBox = new HBox(16, recordInfoLabel, sizeLabel, pageSizeCombo);
        leftPagingBox.setAlignment(Pos.CENTER_LEFT);

        Region pagSpacer = new Region();
        HBox.setHgrow(pagSpacer, Priority.ALWAYS);

        HBox pageButtonsBox = new HBox(6);
        pageButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        paginationBar.getChildren().addAll(leftPagingBox, pagSpacer, pageButtonsBox);

        // State phân trang & hàm load
        int[] currentPage = new int[]{1};
        int[] currentPageSize = new int[]{DEFAULT_PAGE_SIZE};

        // ── FILTER & PAGING LOGIC ─────────────────────────────────────────────
        final Runnable[] applyFiltersRef = new Runnable[1];
        applyFiltersRef[0] = () -> {
            String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
            String roleVal = roleFilter.getValue();
            String statusVal = statusFilter.getValue();

            List<Staff> res = masterData.stream().filter(s -> {
                String rDisp = s.getRoleDisplay();
                boolean matchesQuery = query.isEmpty()
                        || (s.id != null && String.valueOf(s.id).contains(query))
                        || (s.username != null && s.username.toLowerCase().contains(query))
                        || (s.fullName != null && s.fullName.toLowerCase().contains(query))
                        || (s.email != null && s.email.toLowerCase().contains(query))
                        || (s.phone != null && s.phone.toLowerCase().contains(query))
                        || rDisp.toLowerCase().contains(query);

                boolean matchesRole = true;
                if ("Admin".equals(roleVal) || "Quản trị viên".equals(roleVal)) matchesRole = "ADMIN".equalsIgnoreCase(s.role);
                else if ("Quản lý".equals(roleVal)) matchesRole = "MANAGER".equalsIgnoreCase(s.role);
                else if ("Lễ tân".equals(roleVal)) matchesRole = "RECEPTION".equalsIgnoreCase(s.role);

                boolean matchesStatus = true;
                if ("Đang hoạt động".equals(statusVal)) matchesStatus = "ACTIVE".equalsIgnoreCase(s.status);
                else if ("Đã khóa".equals(statusVal)) matchesStatus = "INACTIVE".equalsIgnoreCase(s.status);

                return matchesQuery && matchesRole && matchesStatus;
            }).collect(Collectors.toList());

            int totalElements = res.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalElements / currentPageSize[0]));
            if (currentPage[0] > totalPages) currentPage[0] = totalPages;
            if (currentPage[0] < 1) currentPage[0] = 1;

            int from = totalElements == 0 ? 0 : (currentPage[0] - 1) * currentPageSize[0];
            int to = Math.min(from + currentPageSize[0], totalElements);

            filteredData.setAll(totalElements > 0 ? res.subList(from, to) : List.of());
            table.scrollTo(0);

            if (totalElements == 0) {
                recordInfoLabel.setText("Hiển thị 0 nhân viên");
            } else {
                recordInfoLabel.setText("Hiển thị " + (from + 1) + " – " + to + " trên tổng " + totalElements + " nhân viên");
            }

            renderPageButtons(pageButtonsBox, currentPage[0], totalPages, newPage -> {
                currentPage[0] = newPage;
                applyFiltersRef[0].run();
            });
        };
        Runnable applyFiltersAndPaging = () -> applyFiltersRef[0].run();

        Runnable onFilterChange = () -> {
            currentPage[0] = 1;
            applyFiltersAndPaging.run();
        };

        Runnable reload = () -> Util.runAsync(ApiService::staff, list -> {
            masterData.setAll(list);
            applyFiltersAndPaging.run();
        });

        searchBtn.setOnAction(e -> onFilterChange.run());
        searchField.setOnAction(e -> onFilterChange.run());
        roleFilter.setOnAction(e -> onFilterChange.run());
        statusFilter.setOnAction(e -> onFilterChange.run());

        pageSizeCombo.setOnAction(e -> {
            Integer selectedSize = pageSizeCombo.getValue();
            if (selectedSize != null && selectedSize != currentPageSize[0]) {
                currentPageSize[0] = selectedSize;
                currentPage[0] = 1;
                applyFiltersAndPaging.run();
            }
        });

        // ── ACTIONS ───────────────────────────────────────────────────────────
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Sửa");
            private final Button lockBtn = new Button("Khóa");
            private final Button delBtn = new Button("Xóa");
            private final HBox box = new HBox(6, editBtn, lockBtn, delBtn);

            {
                editBtn.getStyleClass().add("btn-action-edit");
                editBtn.setGraphic(IconUtils.icon("edit", 11, javafx.scene.paint.Color.WHITE));
                editBtn.setGraphicTextGap(5);
                editBtn.setMinWidth(68);
                editBtn.setStyle("-fx-padding: 4 9; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");

                lockBtn.getStyleClass().add("btn-outline");
                lockBtn.setGraphic(IconUtils.icon("lock", 11, javafx.scene.paint.Color.web("#F59E0B")));
                lockBtn.setGraphicTextGap(5);
                lockBtn.setMinWidth(68);
                lockBtn.setStyle("-fx-padding: 4 9; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");

                delBtn.getStyleClass().add("btn-danger");
                delBtn.setGraphic(IconUtils.icon("trash", 11, javafx.scene.paint.Color.WHITE));
                delBtn.setGraphicTextGap(5);
                delBtn.setMinWidth(68);
                delBtn.setStyle("-fx-padding: 4 9; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");
                box.setAlignment(Pos.CENTER_LEFT);
                setAlignment(Pos.CENTER_LEFT);

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

        card.getChildren().addAll(headerRow, toolbar, table, paginationBar);
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
        grid.add(new Label("Vai trò (*):"), 0, 5);
        grid.add(roleBox, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String u = username.getText().trim();
                String p = password.getText().trim();
                String fn = fullName.getText().trim();
                String em = email.getText().trim();
                String ph = phone.getText().trim();
                String role = roleFromDisplay(roleBox.getValue());
                String jt = roleDisplay(role);

                if (u.isEmpty() || fn.isEmpty()) {
                    Util.info("Vui lòng nhập đầy đủ Tên đăng nhập và Họ tên.");
                    return null;
                }

                Util.runAsync(() -> ApiService.createStaff(u, p, fn, em, ph, jt, role, null), s -> reload.run());
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
        grid.add(new Label("Vai trò (*):"), 0, 4);
        grid.add(roleBox, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String u = username.getText().trim();
                String fn = fullName.getText().trim();
                String em = email.getText().trim();
                String ph = phone.getText().trim();
                String role = roleFromDisplay(roleBox.getValue());
                String jt = roleDisplay(role);

                if (u.isEmpty() || fn.isEmpty()) {
                    Util.info("Vui lòng nhập đầy đủ Tên đăng nhập và Họ tên.");
                    return null;
                }

                Util.runAsync(() -> ApiService.updateStaff(staff.id, u, fn, em, ph, jt, role, null), s -> reload.run());
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

    // ── Pagination helpers ────────────────────────────────────────────────
    private static void renderPageButtons(HBox box, int cur, int total, java.util.function.Consumer<Integer> onSelect) {
        box.getChildren().clear();

        Button prev = new Button("‹");
        prev.setStyle(pageButtonStyle(false));
        prev.setDisable(cur <= 1);
        prev.setOnAction(e -> onSelect.accept(cur - 1));
        box.getChildren().add(prev);

        if (total <= 7) {
            for (int i = 1; i <= total; i++) {
                box.getChildren().add(createNumBtn(i, i == cur, onSelect));
            }
        } else {
            if (cur <= 4) {
                for (int i = 1; i <= 4; i++) {
                    box.getChildren().add(createNumBtn(i, i == cur, onSelect));
                }
                box.getChildren().add(createEllipsis());
                box.getChildren().add(createNumBtn(total, total == cur, onSelect));
            } else if (cur >= total - 3) {
                box.getChildren().add(createNumBtn(1, 1 == cur, onSelect));
                box.getChildren().add(createEllipsis());
                for (int i = total - 3; i <= total; i++) {
                    box.getChildren().add(createNumBtn(i, i == cur, onSelect));
                }
            } else {
                box.getChildren().add(createNumBtn(1, 1 == cur, onSelect));
                box.getChildren().add(createEllipsis());
                box.getChildren().add(createNumBtn(cur - 1, false, onSelect));
                box.getChildren().add(createNumBtn(cur, true, onSelect));
                box.getChildren().add(createNumBtn(cur + 1, false, onSelect));
                box.getChildren().add(createEllipsis());
                box.getChildren().add(createNumBtn(total, false, onSelect));
            }
        }

        Button next = new Button("›");
        next.setStyle(pageButtonStyle(false));
        next.setDisable(cur >= total);
        next.setOnAction(e -> onSelect.accept(cur + 1));
        box.getChildren().add(next);
    }

    private static Button createNumBtn(int page, boolean active, java.util.function.Consumer<Integer> onSelect) {
        Button b = new Button(String.valueOf(page));
        b.setStyle(pageButtonStyle(active));
        b.setOnAction(e -> {
            if (!active) onSelect.accept(page);
        });
        return b;
    }

    private static Label createEllipsis() {
        Label l = new Label("…");
        l.setStyle("-fx-text-fill: #64748B; -fx-padding: 0 4; -fx-font-weight: 700;");
        return l;
    }

    private static String pageButtonStyle(boolean active) {
        if (active) {
            return "-fx-background-color: #D97706; -fx-text-fill: #FFFFFF; " +
                    "-fx-font-weight: 700; -fx-min-width: 30px; -fx-min-height: 30px; " +
                    "-fx-background-radius: 6; -fx-font-size: 12px; -fx-cursor: hand;";
        }
        return "-fx-background-color: #1E293B; -fx-text-fill: #94A3B8; " +
                "-fx-border-color: rgba(148, 163, 184, 0.25); -fx-border-radius: 6; " +
                "-fx-background-radius: 6; -fx-min-width: 30px; -fx-min-height: 30px; " +
                "-fx-font-size: 12px; -fx-cursor: hand;";
    }
}
