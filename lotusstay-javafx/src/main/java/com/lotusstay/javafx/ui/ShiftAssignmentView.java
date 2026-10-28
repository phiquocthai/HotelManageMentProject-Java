package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.ShiftAssignment;
import com.lotusstay.javafx.model.Staff;
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
 * Giao diện Phân ca làm việc — Chuẩn Dark Luxury Theme:
 * - Cột ID chuẩn hóa khóa chính (1, 2, 3...)
 * - Thanh tìm kiếm: tên nhân viên, dropdown loại ca, DatePicker ngày làm việc, nút Tìm kiếm, nút Làm mới
 * - Phân trang đầy đủ giống Quản lý khách hàng
 * - Nút 'Chấm công' và 'Xóa' đồng bộ
 */
public class ShiftAssignmentView {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static class ShiftFilterOption {
        public final Long id;
        public final String name;

        public ShiftFilterOption(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: transparent;");

        VBox card = new VBox(20);
        card.getStyleClass().add("panel");
        VBox.setVgrow(card, Priority.ALWAYS);

        // ── Header row ────────────────────────────────────────────────────
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Phân ca làm việc");
        title.getStyleClass().add("page-heading");

        Label sub = new Label("Lịch phân công nhân sự trực ca theo ngày, điều phối ca sáng, chiều, đêm linh hoạt");
        sub.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(title, sub);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button addBtn = new Button("Phân ca mới");
        addBtn.setGraphic(IconUtils.icon("plus", 14, Color.web("#0A0D14")));
        addBtn.setGraphicTextGap(8);
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 13.5px; -fx-font-weight: 800; -fx-padding: 9 22; -fx-background-color: #F59E0B; -fx-text-fill: #0A0D14; -fx-background-radius: 8; -fx-cursor: hand;");

        headerRow.getChildren().addAll(titleBox, headerSpacer, addBtn);

        // ── Toolbar: Tìm kiếm theo tên NV, Loại ca, Ngày làm việc ────────
        TextField staffSearchField = new TextField();
        staffSearchField.setPromptText("Tên nhân viên...");
        staffSearchField.setPrefWidth(220);

        ComboBox<ShiftFilterOption> shiftFilterCombo = new ComboBox<>();
        shiftFilterCombo.setPrefWidth(200);
        ShiftFilterOption allShiftsOption = new ShiftFilterOption(null, "Tất cả loại ca");
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
        dateFilter.setPrefWidth(150);
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

        Button searchBtn = new Button("🔍 Tìm kiếm");
        searchBtn.getStyleClass().add("btn-outline");

        Button refreshBtn = new Button("🔄 Làm mới");
        refreshBtn.getStyleClass().add("btn-outline");

        Region toolSpacer = new Region();
        HBox.setHgrow(toolSpacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, staffSearchField, shiftFilterCombo, dateFilter, searchBtn, refreshBtn, toolSpacer);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // ── Table ─────────────────────────────────────────────────────────
        ObservableList<ShiftAssignment> data = FXCollections.observableArrayList();

        TableView<ShiftAssignment> table = new TableView<>(data);
        table.getStyleClass().add("table-view");
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có dữ liệu phân ca nào."));

        TableColumn<ShiftAssignment, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(70);
        idCol.setPrefWidth(80);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().id != null ? String.valueOf(d.getValue().id) : "—"));
        idCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER_LEFT;");
        idCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 13.5px; -fx-alignment: CENTER_LEFT;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<ShiftAssignment, String> staffCol = new TableColumn<>("Nhân viên");
        staffCol.setMinWidth(200);
        staffCol.setPrefWidth(240);
        staffCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().staff != null ? d.getValue().staff.fullName : "—"));
        staffCol.setStyle("-fx-font-weight: 700; -fx-alignment: CENTER_LEFT;");
        staffCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-weight: 800; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<ShiftAssignment, String> shiftCol = new TableColumn<>("Ca làm việc");
        shiftCol.setMinWidth(130);
        shiftCol.setPrefWidth(140);
        shiftCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().workShift != null ? d.getValue().workShift.shiftName : "—"));
        shiftCol.setStyle("-fx-font-weight: 700; -fx-alignment: CENTER_LEFT;");
        shiftCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 14px; -fx-text-fill: #F59E0B; -fx-font-weight: 700; -fx-alignment: CENTER_LEFT;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<ShiftAssignment, String> startTimeCol = new TableColumn<>("Giờ bắt đầu");
        startTimeCol.setMinWidth(120);
        startTimeCol.setPrefWidth(130);
        startTimeCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().workShift != null && d.getValue().workShift.startTime != null && !d.getValue().workShift.startTime.isBlank()
                        ? d.getValue().workShift.startTime : "—"));
        startTimeCol.setStyle("-fx-alignment: CENTER_LEFT;");
        startTimeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13.5px; -fx-text-fill: #38BDF8; -fx-font-weight: 700; -fx-alignment: CENTER_LEFT;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<ShiftAssignment, String> endTimeCol = new TableColumn<>("Giờ kết thúc");
        endTimeCol.setMinWidth(120);
        endTimeCol.setPrefWidth(130);
        endTimeCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().workShift != null && d.getValue().workShift.endTime != null && !d.getValue().workShift.endTime.isBlank()
                        ? d.getValue().workShift.endTime : "—"));
        endTimeCol.setStyle("-fx-alignment: CENTER_LEFT;");
        endTimeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13.5px; -fx-text-fill: #A78BFA; -fx-font-weight: 700; -fx-alignment: CENTER_LEFT;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<ShiftAssignment, String> dateCol = new TableColumn<>("Ngày làm việc");
        dateCol.setMinWidth(140);
        dateCol.setPrefWidth(150);
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().workDate));
        dateCol.setStyle("-fx-alignment: CENTER_LEFT;");
        dateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 14px; -fx-text-fill: #F1F5F9; -fx-font-weight: 600; -fx-alignment: CENTER_LEFT;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<ShiftAssignment, Void> actionCol = new TableColumn<>("Thao tác");
        actionCol.setMinWidth(180);
        actionCol.setPrefWidth(190);
        actionCol.setStyle("-fx-alignment: CENTER_LEFT;");

        table.getColumns().addAll(idCol, staffCol, shiftCol, startTimeCol, endTimeCol, dateCol, actionCol);

        // ── Thanh phân trang Footer ───────────────────────────────────────
        HBox paginationBar = new HBox(16);
        paginationBar.setAlignment(Pos.CENTER_LEFT);
        paginationBar.setPadding(new Insets(12, 0, 4, 0));

        Label recordInfoLabel = new Label("Hiển thị 0 phân ca");
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
            String dateQuery = selDate != null ? DATE_FMT.format(selDate) : null;

            Util.runAsync(() -> ApiService.shiftAssignmentPage(currentPage[0], currentPageSize[0], staffQuery, shiftId, dateQuery),
                    (PageResponse<ShiftAssignment> page) -> {
                        data.setAll(page.getContent() != null ? page.getContent() : List.of());
                        table.scrollTo(0);

                        int totalPages = Math.max(1, page.totalPages);
                        long totalElements = page.totalElements;
                        currentPage[0] = page.pageNo;

                        if (totalElements == 0) {
                            recordInfoLabel.setText("Hiển thị 0 phân ca");
                        } else {
                            int from = (page.pageNo - 1) * page.pageSize + 1;
                            int to = (int) Math.min((long) page.pageNo * page.pageSize, totalElements);
                            recordInfoLabel.setText("Hiển thị " + from + " – " + to + " trên tổng " + totalElements + " phân ca");
                        }

                        renderPageButtons(pageButtonsBox, currentPage[0], totalPages, newPage -> loadPageHolder[0].accept(newPage));
                    });
        };

        Runnable loadCurrentPage = () -> loadPageHolder[0].accept(currentPage[0]);

        // Action cell: Nút Sửa (đổi ca làm việc linh hoạt) và Xóa
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Sửa");
            private final Button delBtn  = new Button("Xóa");
            private final HBox box = new HBox(8, editBtn, delBtn);

            {
                editBtn.getStyleClass().add("btn-action-edit");
                delBtn.getStyleClass().add("btn-action-del");

                editBtn.setGraphic(IconUtils.icon("edit", 12, Color.WHITE));
                editBtn.setGraphicTextGap(6);
                editBtn.setMinWidth(72);
                editBtn.setStyle("-fx-padding: 4 12; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 72px;");

                delBtn.setGraphic(IconUtils.icon("trash", 12, Color.WHITE));
                delBtn.setGraphicTextGap(6);
                delBtn.setMinWidth(72);
                delBtn.setStyle("-fx-padding: 4 12; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 72px;");

                box.setAlignment(Pos.CENTER_LEFT);
                setAlignment(Pos.CENTER_LEFT);

                editBtn.setOnAction(e -> {
                    ShiftAssignment sa = getTableView().getItems().get(getIndex());
                    openEditDialog(sa, loadCurrentPage);
                });

                delBtn.setOnAction(e -> {
                    ShiftAssignment sa = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Xóa phân ca của nhân viên \"" + (sa.staff != null ? sa.staff.fullName : "")
                                    + "\" vào ngày " + sa.workDate + "?");
                    confirm.setHeaderText("Xác nhận xóa phân ca");
                    confirm.setTitle("LotusStay — Xác nhận");
                    applyTheme(confirm);
                    confirm.showAndWait()
                            .filter(bt -> bt == ButtonType.OK)
                            .ifPresent(bt -> Util.runAsyncVoid(
                                    () -> ApiService.deleteShiftAssignment(sa.id), loadCurrentPage));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // ── Tìm kiếm & Làm mới ──────────────────────────────────────────
        searchBtn.setOnAction(e -> loadPageHolder[0].accept(1));
        staffSearchField.setOnAction(e -> loadPageHolder[0].accept(1));
        shiftFilterCombo.setOnAction(e -> loadPageHolder[0].accept(1));
        dateFilter.setOnAction(e -> loadPageHolder[0].accept(1));

        refreshBtn.setOnAction(e -> {
            staffSearchField.clear();
            shiftFilterCombo.setValue(allShiftsOption);
            dateFilter.setValue(null);
            loadPageHolder[0].accept(1);
        });

        pageSizeCombo.setOnAction(e -> {
            Integer selectedSize = pageSizeCombo.getValue();
            if (selectedSize != null && selectedSize != currentPageSize[0]) {
                currentPageSize[0] = selectedSize;
                loadPageHolder[0].accept(1);
            }
        });

        addBtn.setOnAction(e -> openAddDialog(loadCurrentPage));

        // Load ban đầu
        loadPageHolder[0].accept(1);

        card.getChildren().addAll(headerRow, toolbar, table, paginationBar);
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

    // ── Dialog phân ca mới (Dark Modal) ──────────────────────────────────
    private static void openAddDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("LotusStay — Phân ca làm việc");
        applyTheme(dialog);

        List<Staff> staffList = ApiService.staff();
        List<WorkShift> shiftList = ApiService.workShifts();

        ComboBox<Staff> staffBox = new ComboBox<>();
        staffBox.getItems().addAll(staffList);
        staffBox.setPrefHeight(38);
        staffBox.setMaxWidth(Double.MAX_VALUE);
        staffBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Staff s) { return s == null ? "" : s.fullName + " (" + s.username + ")"; }
            public Staff fromString(String s) { return null; }
        });
        if (!staffList.isEmpty()) staffBox.setValue(staffList.get(0));

        ComboBox<WorkShift> shiftBox = new ComboBox<>();
        shiftBox.getItems().addAll(shiftList);
        shiftBox.setPrefHeight(38);
        shiftBox.setMaxWidth(Double.MAX_VALUE);
        shiftBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(WorkShift w) {
                return w == null ? "" : w.shiftName + (w.startTime != null && w.endTime != null ? " (" + w.startTime + " - " + w.endTime + ")" : "");
            }
            public WorkShift fromString(String s) { return null; }
        });
        if (!shiftList.isEmpty()) shiftBox.setValue(shiftList.get(0));

        // Trường hiển thị giờ bắt đầu và giờ kết thúc (Chỉ đọc, hiển thị theo ca làm việc đã chọn)
        TextField startTimeField = new TextField();
        startTimeField.setEditable(false);
        startTimeField.setFocusTraversable(false);
        startTimeField.setPrefHeight(38);
        startTimeField.setStyle("-fx-background-color: #0B0F17; -fx-text-fill: #38BDF8; -fx-font-weight: 700; " +
                "-fx-font-size: 13.5px; -fx-border-color: #334155; -fx-border-radius: 6; -fx-background-radius: 6; -fx-opacity: 0.95;");

        TextField endTimeField = new TextField();
        endTimeField.setEditable(false);
        endTimeField.setFocusTraversable(false);
        endTimeField.setPrefHeight(38);
        endTimeField.setStyle("-fx-background-color: #0B0F17; -fx-text-fill: #A78BFA; -fx-font-weight: 700; " +
                "-fx-font-size: 13.5px; -fx-border-color: #334155; -fx-border-radius: 6; -fx-background-radius: 6; -fx-opacity: 0.95;");

        Runnable updateTimes = () -> {
            WorkShift curWs = shiftBox.getValue();
            if (curWs != null) {
                startTimeField.setText(curWs.startTime != null && !curWs.startTime.isBlank() ? curWs.startTime : "—");
                endTimeField.setText(curWs.endTime != null && !curWs.endTime.isBlank() ? curWs.endTime : "—");
            } else {
                startTimeField.setText("—");
                endTimeField.setText("—");
            }
        };
        shiftBox.valueProperty().addListener((obs, oldV, newV) -> updateTimes.run());
        updateTimes.run();

        // Quick Preset Chips for shifts (Chọn nhanh ca làm)
        VBox quickShiftBox = new VBox(6);
        Label quickShiftLabel = new Label("⚡ Chọn nhanh ca làm việc:");
        quickShiftLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #F59E0B;");
        FlowPane quickChips = new FlowPane(8, 8);
        for (WorkShift ws : shiftList) {
            Button chipBtn = new Button(ws.shiftName);
            chipBtn.setStyle("-fx-background-color: #1E293B; -fx-text-fill: #E2E8F0; -fx-font-family: 'Inter', sans-serif; " +
                    "-fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 5 12; -fx-background-radius: 6; " +
                    "-fx-border-color: #334155; -fx-border-radius: 6; -fx-cursor: hand;");
            chipBtn.setOnAction(e -> shiftBox.setValue(ws));
            quickChips.getChildren().add(chipBtn);
        }
        quickShiftBox.getChildren().addAll(quickShiftLabel, quickChips);

        Label timeNote = new Label("🔒 Giờ bắt đầu và kết thúc tự động cập nhật theo ca làm việc, không thể sửa trực tiếp.");
        timeNote.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 11.5px; -fx-text-fill: #94A3B8; -fx-font-style: italic;");

        HBox timeRow = new HBox(12,
                buildFormField("Giờ bắt đầu (theo ca)", startTimeField),
                buildFormField("Giờ kết thúc (theo ca)", endTimeField)
        );
        HBox.setHgrow(timeRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(timeRow.getChildren().get(1), Priority.ALWAYS);
        VBox timeSection = new VBox(4, timeRow, timeNote);

        TextField dateField = new TextField();
        dateField.setPromptText("dd/MM/yyyy (VD: 22/10/2026)");
        dateField.setPrefHeight(38);
        dateField.setText(java.time.LocalDate.now().format(DATE_FMT));

        VBox modalContent = new VBox(16);
        modalContent.setPadding(new Insets(24));
        modalContent.setPrefWidth(500);
        modalContent.setStyle("-fx-background-color: #141A24;");

        HBox modalHeader = new HBox(12);
        modalHeader.setAlignment(Pos.CENTER_LEFT);

        Label iconBadge = new Label("📋");
        iconBadge.setStyle("-fx-font-size: 20px; -fx-background-color: rgba(245, 158, 11, 0.18); -fx-padding: 8 12; -fx-background-radius: 10;");

        VBox titleArea = new VBox(3);
        Label hTitle = new Label("Phân ca làm việc mới");
        hTitle.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");
        Label hSub = new Label("Gán ca trực và ngày làm việc cho nhân viên");
        hSub.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-text-fill: #94A3B8;");
        titleArea.getChildren().addAll(hTitle, hSub);
        modalHeader.getChildren().addAll(iconBadge, titleArea);

        VBox formBody = new VBox(14);
        formBody.getChildren().addAll(
                buildFormField("Nhân viên phụ trách (*)", staffBox),
                quickShiftBox,
                buildFormField("Ca làm việc (*)", shiftBox),
                timeSection,
                buildFormField("Ngày làm việc (*)", dateField)
        );

        modalContent.getChildren().addAll(modalHeader, new Separator(), formBody);
        dialog.getDialogPane().setContent(modalContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okBtn != null) {
            okBtn.setText("Lưu phân ca");
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
                Staff s = staffBox.getValue();
                WorkShift w = shiftBox.getValue();
                String date = dateField.getText().trim();
                if (s == null || w == null || date.isEmpty()) {
                    Util.info("Vui lòng chọn đầy đủ nhân viên, ca làm và ngày làm việc.");
                    return null;
                }
                Util.runAsync(() -> ApiService.createShiftAssignment(s.id, w.id, date), sa -> reload.run());
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ── Dialog Sửa phân ca (Dark Modal: đổi ca làm việc, giờ bắt đầu/kết thúc chỉ hiển thị) ────
    private static void openEditDialog(ShiftAssignment sa, Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("LotusStay — Sửa phân ca làm việc");
        applyTheme(dialog);

        List<Staff> staffList = ApiService.staff();
        List<WorkShift> shiftList = ApiService.workShifts();

        ComboBox<Staff> staffBox = new ComboBox<>();
        staffBox.getItems().addAll(staffList);
        staffBox.setPrefHeight(38);
        staffBox.setMaxWidth(Double.MAX_VALUE);
        staffBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Staff s) { return s == null ? "" : s.fullName + " (" + s.username + ")"; }
            public Staff fromString(String s) { return null; }
        });
        if (sa.staff != null) {
            for (Staff s : staffList) {
                if (sa.staff.id != null && sa.staff.id.equals(s.id)) {
                    staffBox.setValue(s);
                    break;
                } else if (sa.staff.fullName != null && sa.staff.fullName.equalsIgnoreCase(s.fullName)) {
                    staffBox.setValue(s);
                    break;
                }
            }
        }
        if (staffBox.getValue() == null && !staffList.isEmpty()) {
            staffBox.setValue(staffList.get(0));
        }

        ComboBox<WorkShift> shiftBox = new ComboBox<>();
        shiftBox.getItems().addAll(shiftList);
        shiftBox.setPrefHeight(38);
        shiftBox.setMaxWidth(Double.MAX_VALUE);
        shiftBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(WorkShift w) {
                return w == null ? "" : w.shiftName + (w.startTime != null && w.endTime != null ? " (" + w.startTime + " - " + w.endTime + ")" : "");
            }
            public WorkShift fromString(String s) { return null; }
        });
        if (sa.workShift != null) {
            for (WorkShift ws : shiftList) {
                if (sa.workShift.id != null && sa.workShift.id.equals(ws.id)) {
                    shiftBox.setValue(ws);
                    break;
                } else if (sa.workShift.shiftName != null && sa.workShift.shiftName.equalsIgnoreCase(ws.shiftName)) {
                    shiftBox.setValue(ws);
                    break;
                }
            }
        }
        if (shiftBox.getValue() == null && !shiftList.isEmpty()) {
            shiftBox.setValue(shiftList.get(0));
        }

        // Trường hiển thị giờ bắt đầu và giờ kết thúc (Chỉ đọc, tự động cập nhật theo ca làm việc đã chọn)
        TextField startTimeField = new TextField();
        startTimeField.setEditable(false);
        startTimeField.setFocusTraversable(false);
        startTimeField.setPrefHeight(38);
        startTimeField.setStyle("-fx-background-color: #0B0F17; -fx-text-fill: #38BDF8; -fx-font-weight: 700; " +
                "-fx-font-size: 13.5px; -fx-border-color: #334155; -fx-border-radius: 6; -fx-background-radius: 6; -fx-opacity: 0.95;");

        TextField endTimeField = new TextField();
        endTimeField.setEditable(false);
        endTimeField.setFocusTraversable(false);
        endTimeField.setPrefHeight(38);
        endTimeField.setStyle("-fx-background-color: #0B0F17; -fx-text-fill: #A78BFA; -fx-font-weight: 700; " +
                "-fx-font-size: 13.5px; -fx-border-color: #334155; -fx-border-radius: 6; -fx-background-radius: 6; -fx-opacity: 0.95;");

        Runnable updateTimes = () -> {
            WorkShift curWs = shiftBox.getValue();
            if (curWs != null) {
                startTimeField.setText(curWs.startTime != null && !curWs.startTime.isBlank() ? curWs.startTime : "—");
                endTimeField.setText(curWs.endTime != null && !curWs.endTime.isBlank() ? curWs.endTime : "—");
            } else {
                startTimeField.setText("—");
                endTimeField.setText("—");
            }
        };
        shiftBox.valueProperty().addListener((obs, oldV, newV) -> updateTimes.run());
        updateTimes.run();

        // Quick Preset Chips for shifts (Cho phép đổi ca làm việc nhanh sang sáng, chiều, tối/đêm...)
        VBox quickShiftBox = new VBox(6);
        Label quickShiftLabel = new Label("⚡ Chọn nhanh ca làm việc:");
        quickShiftLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #F59E0B;");
        FlowPane quickChips = new FlowPane(8, 8);
        for (WorkShift ws : shiftList) {
            Button chipBtn = new Button(ws.shiftName);
            chipBtn.setStyle("-fx-background-color: #1E293B; -fx-text-fill: #E2E8F0; -fx-font-family: 'Inter', sans-serif; " +
                    "-fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 5 12; -fx-background-radius: 6; " +
                    "-fx-border-color: #334155; -fx-border-radius: 6; -fx-cursor: hand;");
            chipBtn.setOnAction(e -> shiftBox.setValue(ws));
            quickChips.getChildren().add(chipBtn);
        }
        quickShiftBox.getChildren().addAll(quickShiftLabel, quickChips);

        Label timeNote = new Label("🔒 Giờ bắt đầu và kết thúc tự động cập nhật theo ca làm việc, không thể sửa trực tiếp.");
        timeNote.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 11.5px; -fx-text-fill: #94A3B8; -fx-font-style: italic;");

        HBox timeRow = new HBox(12,
                buildFormField("Giờ bắt đầu (theo ca)", startTimeField),
                buildFormField("Giờ kết thúc (theo ca)", endTimeField)
        );
        HBox.setHgrow(timeRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(timeRow.getChildren().get(1), Priority.ALWAYS);
        VBox timeSection = new VBox(4, timeRow, timeNote);

        TextField dateField = new TextField();
        dateField.setPromptText("dd/MM/yyyy (VD: 24/10/2026)");
        dateField.setPrefHeight(38);
        dateField.setText(sa.workDate != null ? sa.workDate : java.time.LocalDate.now().format(DATE_FMT));

        VBox modalContent = new VBox(16);
        modalContent.setPadding(new Insets(24));
        modalContent.setPrefWidth(500);
        modalContent.setStyle("-fx-background-color: #141A24;");

        HBox modalHeader = new HBox(12);
        modalHeader.setAlignment(Pos.CENTER_LEFT);

        Label iconBadge = new Label("✏️");
        iconBadge.setStyle("-fx-font-size: 20px; -fx-background-color: rgba(245, 158, 11, 0.18); -fx-padding: 8 12; -fx-background-radius: 10;");

        VBox titleArea = new VBox(3);
        Label hTitle = new Label("Chỉnh sửa phân ca làm việc");
        hTitle.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");
        String empName = sa.staff != null ? sa.staff.fullName : "Nhân viên";
        Label hSub = new Label("Nhân viên: " + empName + " • Mã phân ca: #" + (sa.id != null ? sa.id : "—"));
        hSub.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-text-fill: #94A3B8;");
        titleArea.getChildren().addAll(hTitle, hSub);
        modalHeader.getChildren().addAll(iconBadge, titleArea);

        VBox formBody = new VBox(14);
        formBody.getChildren().addAll(
                buildFormField("Nhân viên phụ trách (*)", staffBox),
                quickShiftBox,
                buildFormField("Ca làm việc (*)", shiftBox),
                timeSection,
                buildFormField("Ngày làm việc (*)", dateField)
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
                Staff s = staffBox.getValue();
                WorkShift w = shiftBox.getValue();
                String date = dateField.getText().trim();
                if (s == null || w == null || date.isEmpty()) {
                    Util.info("Vui lòng chọn đầy đủ nhân viên, ca làm và ngày làm việc.");
                    return null;
                }
                Util.runAsync(() -> ApiService.updateShiftAssignment(sa.id, s.id, w.id, date), updated -> reload.run());
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

    private static void applyTheme(Dialog<?> d) {
        d.getDialogPane().getStylesheets().add(
                ShiftAssignmentView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());
    }
}
