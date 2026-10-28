package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Attendance;
import com.lotusstay.javafx.model.WorkShift;
import com.lotusstay.javafx.net.ApiService;
import com.lotusstay.javafx.net.PageResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Giao diện Bảng chấm công — Chuẩn Dark Luxury Theme:
 * Hỗ trợ bộ lọc tìm kiếm (Tên NV, Ca làm, Ngày, Trạng thái),
 * Phân trang chuyên nghiệp và Thao tác Sửa (xanh) / Xóa (đỏ).
 */
public class AttendanceView {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ISO_FMT  = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static class ShiftFilterOption {
        public Long id;
        public String name;
        public ShiftFilterOption(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        @Override
        public String toString() { return name; }
    }

    public static class StatusFilterOption {
        public String code;
        public String label;
        public StatusFilterOption(String code, String label) {
            this.code = code;
            this.label = label;
        }
        @Override
        public String toString() { return label; }
    }

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: transparent;");

        VBox card = new VBox(16);
        card.getStyleClass().add("panel");
        VBox.setVgrow(card, Priority.ALWAYS);

        // ── Header row ────────────────────────────────────────────────────
        VBox titleBox = new VBox(4);
        Label title = new Label("Bảng chấm công");
        title.getStyleClass().add("page-heading");

        Label sub = new Label("Ghi nhận thời gian vào/ra thực tế, đối soát giờ làm và quản lý tình trạng đủ công, đi trễ, vắng mặt");
        sub.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(title, sub);

        // ── Toolbar: Tìm kiếm (Tên NV, Ca làm, Ngày, Trạng thái) ──────────
        TextField staffSearchField = new TextField();
        staffSearchField.setPromptText("Tên nhân viên...");
        staffSearchField.setPrefWidth(200);

        ComboBox<ShiftFilterOption> shiftFilterCombo = new ComboBox<>();
        shiftFilterCombo.setPrefWidth(180);
        ShiftFilterOption allShiftsOption = new ShiftFilterOption(null, "Tất cả ca làm");
        shiftFilterCombo.getItems().add(allShiftsOption);
        Util.runAsync(ApiService::workShifts, shifts -> {
            for (WorkShift ws : shifts) {
                String lbl = ws.shiftName + (ws.startTime != null && ws.endTime != null ? " (" + ws.startTime + " - " + ws.endTime + ")" : "");
                shiftFilterCombo.getItems().add(new ShiftFilterOption(ws.id, lbl));
            }
        });
        shiftFilterCombo.setValue(allShiftsOption);

        DatePicker dateFilter = new DatePicker();
        dateFilter.setPromptText("dd/MM/yyyy");
        dateFilter.setPrefWidth(140);
        dateFilter.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? DATE_FMT.format(date) : "";
            }
            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.isBlank()) return null;
                try {
                    return LocalDate.parse(string.trim(), DATE_FMT);
                } catch (Exception e) {
                    return null;
                }
            }
        });

        ComboBox<StatusFilterOption> statusFilterCombo = new ComboBox<>();
        statusFilterCombo.setMinWidth(180);
        statusFilterCombo.setPrefWidth(180);
        StatusFilterOption allStatusOption = new StatusFilterOption("ALL", "Tất cả trạng thái");
        statusFilterCombo.getItems().addAll(
                allStatusOption,
                new StatusFilterOption("DU_CONG", "Đủ công"),
                new StatusFilterOption("DI_TRE", "Đi trễ"),
                new StatusFilterOption("VE_SOM", "Về sớm"),
                new StatusFilterOption("VANG_MAT", "Vắng mặt")
        );
        statusFilterCombo.setValue(allStatusOption);

        Button searchBtn = new Button("🔍 Tìm kiếm");
        searchBtn.getStyleClass().add("btn-outline");

        Button refreshBtn = new Button("🔄 Làm mới");
        refreshBtn.getStyleClass().add("btn-outline");

        Region toolSpacer = new Region();
        HBox.setHgrow(toolSpacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, staffSearchField, shiftFilterCombo, dateFilter, statusFilterCombo, searchBtn, refreshBtn, toolSpacer);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // ── Table ─────────────────────────────────────────────────────────
        ObservableList<Attendance> data = FXCollections.observableArrayList();

        TableView<Attendance> table = new TableView<>(data);
        table.getStyleClass().add("table-view");
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có dữ liệu chấm công nào."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 1. Cột ID (Primary Key)
        TableColumn<Attendance, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(65);
        idCol.prefWidthProperty().bind(table.widthProperty().multiply(0.06));
        idCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().id != null ? String.valueOf(d.getValue().id) : "—"));
        idCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 14px; -fx-padding: 11 10;");
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // 2. Cột Nhân viên
        TableColumn<Attendance, String> staffCol = new TableColumn<>("NHÂN VIÊN");
        staffCol.setMinWidth(160);
        staffCol.prefWidthProperty().bind(table.widthProperty().multiply(0.18));
        staffCol.setCellValueFactory(d -> {
            if (d.getValue().shiftAssignment != null && d.getValue().shiftAssignment.staff != null)
                return new SimpleStringProperty(d.getValue().shiftAssignment.staff.fullName);
            return new SimpleStringProperty("—");
        });
        staffCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-weight: 800; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 11 12;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        // 3. Cột Ca làm
        TableColumn<Attendance, String> shiftCol = new TableColumn<>("CA LÀM");
        shiftCol.setMinWidth(130);
        shiftCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));
        shiftCol.setCellValueFactory(d -> {
            if (d.getValue().shiftAssignment != null && d.getValue().shiftAssignment.workShift != null)
                return new SimpleStringProperty(d.getValue().shiftAssignment.workShift.shiftName);
            return new SimpleStringProperty("—");
        });
        shiftCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13.5px; -fx-text-fill: #F59E0B; -fx-font-weight: 700; -fx-padding: 11 12;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        // 4. Cột Ngày
        TableColumn<Attendance, String> dateCol = new TableColumn<>("NGÀY");
        dateCol.setMinWidth(110);
        dateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.11));
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().shiftAssignment != null ? formatDisplayDate(d.getValue().shiftAssignment.workDate) : "—"));
        dateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13.5px; -fx-text-fill: #CBD5E1; -fx-font-weight: 600; -fx-padding: 11 12;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        // 5. Cột Giờ vào
        TableColumn<Attendance, String> inCol = new TableColumn<>("GIỜ VÀO");
        inCol.setMinWidth(95);
        inCol.prefWidthProperty().bind(table.widthProperty().multiply(0.09));
        inCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().checkInTime != null ? d.getValue().checkInTime : "—"));
        inCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13.5px; -fx-text-fill: #F1F5F9; -fx-font-weight: 600; -fx-padding: 11 10;");
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // 6. Cột Giờ ra
        TableColumn<Attendance, String> outCol = new TableColumn<>("GIỜ RA");
        outCol.setMinWidth(95);
        outCol.prefWidthProperty().bind(table.widthProperty().multiply(0.09));
        outCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().checkOutTime != null ? d.getValue().checkOutTime : "—"));
        outCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13.5px; -fx-text-fill: #F1F5F9; -fx-font-weight: 600; -fx-padding: 11 10;");
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // 7. Cột Trạng thái (hiển thị đủ chữ, không bị ba chấm)
        TableColumn<Attendance, String> statusCol = new TableColumn<>("TRẠNG THÁI");
        statusCol.setMinWidth(135);
        statusCol.prefWidthProperty().bind(table.widthProperty().multiply(0.12));
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                Label badge = new Label(statusLabel(item));
                badge.getStyleClass().add("badge");
                badge.getStyleClass().add(statusBadgeClass(item));
                badge.setTextOverrun(OverrunStyle.CLIP);
                badge.setMinWidth(Region.USE_PREF_SIZE);
                badge.setStyle("-fx-font-size: 11.5px; -fx-font-weight: 800; -fx-padding: 4 10;");
                setGraphic(badge);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setStyle("-fx-padding: 6 10;");
                setAlignment(Pos.CENTER);
            }
        });

        // 8. Cột Ghi chú
        TableColumn<Attendance, String> noteCol = new TableColumn<>("GHI CHÚ");
        noteCol.setMinWidth(140);
        noteCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));
        noteCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().note != null && !d.getValue().note.isBlank() ? d.getValue().note : "—"));
        noteCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px; -fx-text-fill: #94A3B8; -fx-padding: 11 12;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        // 9. Cột Thao tác (Sửa: xanh, Xóa: đỏ)
        TableColumn<Attendance, Void> actionCol = new TableColumn<>("THAO TÁC");
        actionCol.setMinWidth(160);
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.13));

        table.getColumns().addAll(idCol, staffCol, shiftCol, dateCol, inCol, outCol, statusCol, noteCol, actionCol);

        // ── Thanh phân trang Footer ───────────────────────────────────────
        HBox paginationBar = new HBox(16);
        paginationBar.setAlignment(Pos.CENTER_LEFT);
        paginationBar.setPadding(new Insets(12, 0, 4, 0));

        Label recordInfoLabel = new Label("Hiển thị 0 bản ghi chấm công");
        recordInfoLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px; -fx-text-fill: #94A3B8; -fx-font-weight: 600;");

        Label sizeLabel = new Label("Số bản ghi / trang:");
        sizeLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-text-fill: #94A3B8; -fx-font-weight: 500;");

        ComboBox<Integer> pageSizeCombo = new ComboBox<>(FXCollections.observableArrayList(5, 10, 15, 20));
        pageSizeCombo.setValue(DEFAULT_PAGE_SIZE);

        HBox leftPagingBox = new HBox(16, recordInfoLabel, sizeLabel, pageSizeCombo);
        leftPagingBox.setAlignment(Pos.CENTER_LEFT);

        Region pagSpacer = new Region();
        HBox.setHgrow(pagSpacer, Priority.ALWAYS);

        HBox pageButtonsBox = new HBox(6);
        pageButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        paginationBar.getChildren().addAll(leftPagingBox, pagSpacer, pageButtonsBox);

        // ── State phân trang & hàm load ───────────────────────────────────
        int[] currentPage = new int[]{1};
        int[] currentPageSize = new int[]{DEFAULT_PAGE_SIZE};

        @SuppressWarnings("unchecked")
        java.util.function.Consumer<Integer>[] loadPageHolder = new java.util.function.Consumer[1];

        loadPageHolder[0] = targetPage -> {
            currentPage[0] = Math.max(1, targetPage);
            String staffQuery = staffSearchField.getText() == null ? "" : staffSearchField.getText().trim();
            ShiftFilterOption selShift = shiftFilterCombo.getValue();
            Long shiftId = selShift != null ? selShift.id : null;
            LocalDate selDate = dateFilter.getValue();
            String dateQuery = selDate != null ? ISO_FMT.format(selDate) : null;
            StatusFilterOption selStatus = statusFilterCombo.getValue();
            String statusQuery = (selStatus != null && !"ALL".equalsIgnoreCase(selStatus.code)) ? selStatus.code : null;

            Util.runAsync(() -> ApiService.attendancePage(currentPage[0], currentPageSize[0], staffQuery, shiftId, dateQuery, statusQuery),
                    (PageResponse<Attendance> page) -> {
                        data.setAll(page.getContent() != null ? page.getContent() : List.of());
                        table.scrollTo(0);

                        int totalPages = Math.max(1, page.totalPages);
                        long totalElements = page.totalElements;
                        currentPage[0] = page.pageNo;

                        if (totalElements == 0) {
                            recordInfoLabel.setText("Hiển thị 0 bản ghi chấm công");
                        } else {
                            int from = (page.pageNo - 1) * page.pageSize + 1;
                            int to = (int) Math.min((long) page.pageNo * page.pageSize, totalElements);
                            recordInfoLabel.setText("Hiển thị " + from + " – " + to + " trên tổng " + totalElements + " bản ghi chấm công");
                        }

                        renderPageButtons(pageButtonsBox, currentPage[0], totalPages, newPage -> loadPageHolder[0].accept(newPage));
                    });
        };

        Runnable loadCurrentPage = () -> loadPageHolder[0].accept(currentPage[0]);

        // ── Action cell: Nút Sửa (xanh) & Nút Xóa (đỏ) ─────────────────────
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Sửa");
            private final Button delBtn  = new Button("Xóa");
            private final HBox box = new HBox(8, editBtn, delBtn);

            {
                editBtn.getStyleClass().add("btn-action-edit");
                editBtn.setGraphic(IconUtils.icon("edit", 12, Color.WHITE));
                editBtn.setGraphicTextGap(6);
                editBtn.setMinWidth(68);

                delBtn.getStyleClass().add("btn-action-del");
                delBtn.setGraphic(IconUtils.icon("trash", 12, Color.WHITE));
                delBtn.setGraphicTextGap(6);
                delBtn.setMinWidth(68);

                box.setAlignment(Pos.CENTER_LEFT);
                setStyle("-fx-padding: 6 12;");
                setAlignment(Pos.CENTER_LEFT);

                editBtn.setOnAction(e -> {
                    Attendance att = getTableView().getItems().get(getIndex());
                    openEditDialog(att, loadCurrentPage);
                });

                delBtn.setOnAction(e -> {
                    Attendance att = getTableView().getItems().get(getIndex());
                    String staffName = (att.shiftAssignment != null && att.shiftAssignment.staff != null)
                            ? att.shiftAssignment.staff.fullName : "nhân viên";
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Bạn có chắc chắn muốn xóa bản ghi chấm công ID #" + att.id + " của " + staffName + "?");
                    confirm.setHeaderText("Xác nhận xóa chấm công");
                    confirm.setTitle("LotusStay — Xác nhận");
                    applyTheme(confirm);
                    confirm.showAndWait()
                            .filter(bt -> bt == ButtonType.OK)
                            .ifPresent(bt -> Util.runAsyncVoid(
                                    () -> ApiService.deleteAttendance(att.id), loadCurrentPage));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // ── Search & Filter triggers ──────────────────────────────────────
        searchBtn.setOnAction(e -> loadPageHolder[0].accept(1));
        staffSearchField.setOnAction(e -> loadPageHolder[0].accept(1));
        shiftFilterCombo.setOnAction(e -> loadPageHolder[0].accept(1));
        dateFilter.setOnAction(e -> loadPageHolder[0].accept(1));
        statusFilterCombo.setOnAction(e -> loadPageHolder[0].accept(1));

        refreshBtn.setOnAction(e -> {
            staffSearchField.clear();
            shiftFilterCombo.setValue(allShiftsOption);
            dateFilter.setValue(null);
            statusFilterCombo.setValue(allStatusOption);
            loadPageHolder[0].accept(1);
        });

        pageSizeCombo.setOnAction(e -> {
            if (pageSizeCombo.getValue() != null && pageSizeCombo.getValue() != currentPageSize[0]) {
                currentPageSize[0] = pageSizeCombo.getValue();
                loadPageHolder[0].accept(1);
            }
        });

        // Tải trang đầu tiên
        loadPageHolder[0].accept(1);

        card.getChildren().addAll(titleBox, toolbar, table, paginationBar);
        root.setCenter(card);
        return root;
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
        l.setStyle("-fx-text-fill: #64748B; -fx-font-size: 14px; -fx-padding: 0 4;");
        return l;
    }

    private static String pageButtonStyle(boolean active) {
        if (active) {
            return "-fx-background-color: #0284C7; -fx-text-fill: #FFFFFF; -fx-font-weight: 800; " +
                    "-fx-background-radius: 6; -fx-min-width: 32px; -fx-pref-height: 32px; -fx-cursor: hand;";
        }
        return "-fx-background-color: #1E293B; -fx-text-fill: #CBD5E1; -fx-font-weight: 600; " +
                "-fx-background-radius: 6; -fx-min-width: 32px; -fx-pref-height: 32px; -fx-cursor: hand; " +
                "-fx-border-color: #334155; -fx-border-radius: 6;";
    }

    // ── Dialog sửa chấm công (Dark Modal) ─────────────────────────────────
    private static void openEditDialog(Attendance att, Runnable reload) {
        String staffName  = (att.shiftAssignment != null && att.shiftAssignment.staff != null)
                ? att.shiftAssignment.staff.fullName : "—";
        String shiftName  = (att.shiftAssignment != null && att.shiftAssignment.workShift != null)
                ? att.shiftAssignment.workShift.shiftName : "—";
        String workDate   = att.shiftAssignment != null ? formatDisplayDate(att.shiftAssignment.workDate) : "—";

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("LotusStay — Cập nhật chấm công");
        applyTheme(dialog);

        TextField checkInField  = new TextField(att.checkInTime != null  ? att.checkInTime  : "");
        checkInField.setPromptText("HH:mm (VD: 06:00)");
        checkInField.setPrefHeight(38);

        TextField checkOutField = new TextField(att.checkOutTime != null ? att.checkOutTime : "");
        checkOutField.setPromptText("HH:mm hoặc để trống");
        checkOutField.setPrefHeight(38);

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("DU_CONG", "DI_TRE", "VE_SOM", "VANG_MAT");
        statusBox.setValue(att.status != null ? att.status : "DU_CONG");
        statusBox.setPrefHeight(38);
        statusBox.setMaxWidth(Double.MAX_VALUE);
        statusBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(String s) {
                if (s == null) return "";
                return switch (s) {
                    case "DU_CONG"  -> "Đủ công";
                    case "DI_TRE"   -> "Đi trễ";
                    case "VE_SOM"   -> "Về sớm";
                    case "VANG_MAT" -> "Vắng mặt";
                    default -> s;
                };
            }
            public String fromString(String s) { return s; }
        });

        TextField noteField = new TextField(att.note != null ? att.note : "");
        noteField.setPromptText("Ghi chú chấm công...");
        noteField.setPrefHeight(38);

        VBox modalContent = new VBox(16);
        modalContent.setPadding(new Insets(24));
        modalContent.setPrefWidth(480);
        modalContent.setStyle("-fx-background-color: #141A24;");

        HBox modalHeader = new HBox(12);
        modalHeader.setAlignment(Pos.CENTER_LEFT);

        Label iconBadge = new Label("⏱️");
        iconBadge.setStyle("-fx-font-size: 20px; -fx-background-color: rgba(245, 158, 11, 0.18); -fx-padding: 8 12; -fx-background-radius: 10;");

        VBox titleArea = new VBox(3);
        Label hTitle = new Label("Cập nhật: " + staffName);
        hTitle.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");
        Label hSub = new Label(shiftName + " • Ngày: " + workDate);
        hSub.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px; -fx-text-fill: #F59E0B; -fx-font-weight: 600;");
        titleArea.getChildren().addAll(hTitle, hSub);
        modalHeader.getChildren().addAll(iconBadge, titleArea);

        VBox formBody = new VBox(12);
        formBody.getChildren().addAll(
                buildFormField("Giờ vào thực tế (*)", checkInField),
                buildFormField("Giờ ra thực tế", checkOutField),
                buildFormField("Trạng thái công (*)", statusBox),
                buildFormField("Ghi chú", noteField)
        );

        modalContent.getChildren().addAll(modalHeader, new Separator(), formBody);
        dialog.getDialogPane().setContent(modalContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okBtn != null) {
            okBtn.setText("Lưu thay đổi");
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
                String ci   = checkInField.getText().trim();
                String co   = checkOutField.getText().trim().isEmpty() ? null : checkOutField.getText().trim();
                String stat = statusBox.getValue();
                String note = noteField.getText().trim();
                Util.runAsync(() -> ApiService.updateAttendance(att.id, ci, co, stat, note), a -> reload.run());
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static String statusLabel(String s) {
        if (s == null) return "—";
        return switch (s.toUpperCase()) {
            case "DU_CONG"  -> "ĐỦ CÔNG";
            case "DI_TRE"   -> "ĐI TRỄ";
            case "VE_SOM"   -> "VỀ SỚM";
            case "VANG_MAT" -> "VẮNG MẶT";
            default -> s;
        };
    }

    private static String statusBadgeClass(String s) {
        if (s == null) return "badge-trong";
        return switch (s.toUpperCase()) {
            case "DU_CONG"  -> "badge-dang_o";
            case "DI_TRE"   -> "badge-orange";
            case "VE_SOM"   -> "badge-bao_tri";
            case "VANG_MAT" -> "badge-huy";
            default -> "badge-trong";
        };
    }

    private static String formatDisplayDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return "—";
        try {
            if (dateStr.contains("-")) {
                LocalDate d = LocalDate.parse(dateStr.trim(), ISO_FMT);
                return DATE_FMT.format(d);
            }
        } catch (Exception ignored) {}
        return dateStr;
    }

    private static VBox buildFormField(String labelText, Control inputControl) {
        VBox box = new VBox(6);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #E2E8F0;");
        box.getChildren().addAll(label, inputControl);
        return box;
    }

    private static void applyTheme(Dialog<?> d) {
        d.getDialogPane().getStylesheets().add(
                AttendanceView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());
    }
}
