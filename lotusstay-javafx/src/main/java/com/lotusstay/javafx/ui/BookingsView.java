package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Booking;
import com.lotusstay.javafx.model.Customer;
import com.lotusstay.javafx.model.Room;
import com.lotusstay.javafx.net.ApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BookingsView {

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: transparent;");

        VBox container = new VBox(16);
        container.getStyleClass().add("panel");
        VBox.setVgrow(container, Priority.ALWAYS);

        // Header info
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        Label title = new Label("Quản lý đặt phòng & Lưu trú");
        title.getStyleClass().add("section-title");
        title.setStyle(
                "-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");

        Label subtitle = new Label(
                "Theo dõi danh sách khách đặt, tiến hành nhận/trả phòng và cập nhật tình trạng phòng tức thì");
        subtitle.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-text-fill: #8C9CB2;");
        titleBox.getChildren().addAll(title, subtitle);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Button topAddBtn = new Button("+ Đặt phòng mới");
        topAddBtn.getStyleClass().add("btn-gold");
        topAddBtn.setStyle(
                "-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 13px; -fx-font-weight: 800; -fx-background-color: #F59E0B; -fx-text-fill: #0A0D14; -fx-background-radius: 8; -fx-padding: 8 18; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.4), 10, 0, 0, 2);");

        headerRow.getChildren().addAll(titleBox, topSpacer, topAddBtn);

        // Data collection
        ObservableList<Booking> masterData = FXCollections.observableArrayList();
        ObservableList<Booking> pagedData = FXCollections.observableArrayList();

        // KPI Summary Cards
        Label totalCountLbl = new Label("0");
        Label stayingCountLbl = new Label("0");
        Label reservedCountLbl = new Label("0");
        Label completedCountLbl = new Label("0");

        GridPane kpiGrid = buildKpiRow(totalCountLbl, stayingCountLbl, reservedCountLbl, completedCountLbl);

        Runnable updateKpi = () -> {
            long total = masterData.size();
            long staying = masterData.stream()
                    .filter(b -> "DANG_O".equalsIgnoreCase(b.status) || "DANG_SU_DUNG".equalsIgnoreCase(b.status))
                    .count();
            long reserved = masterData.stream()
                    .filter(b -> "DAT_TRUOC".equalsIgnoreCase(b.status) || "CHO_XAC_NHAN".equalsIgnoreCase(b.status))
                    .count();
            long completed = masterData.stream()
                    .filter(b -> "DA_TRA".equalsIgnoreCase(b.status) || "DA_TRA_PHONG".equalsIgnoreCase(b.status))
                    .count();

            totalCountLbl.setText(String.valueOf(total));
            stayingCountLbl.setText(String.valueOf(staying));
            reservedCountLbl.setText(String.valueOf(reserved));
            completedCountLbl.setText(String.valueOf(completed));
        };

        // Top Toolbar
        TextField search = new TextField();
        search.setPromptText("Tìm theo ID, mã, tên khách, SĐT, số phòng...");
        search.setPrefWidth(280);
        search.setStyle(
                "-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px; -fx-background-color: #121824; -fx-border-color: #232D3F; -fx-border-radius: 6; -fx-text-fill: #FFFFFF; -fx-padding: 7 12;");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Tất cả trạng thái", "Đang lưu trú", "Đã đặt trước", "Đã trả phòng", "Đã hủy");
        statusFilter.setValue("Tất cả trạng thái");
        statusFilter.setPrefWidth(165);
        statusFilter.setStyle(
                "-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px; -fx-background-color: #121824; -fx-border-color: #232D3F; -fx-border-radius: 6; -fx-text-fill: #FFFFFF;");

        Button searchBtn = new Button("Tìm kiếm");
        searchBtn.setGraphic(IconUtils.icon("search", 13, Color.web("#CBD5E1")));
        searchBtn.setGraphicTextGap(6);
        searchBtn.getStyleClass().add("btn-outline");
        searchBtn.setStyle(
                "-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-font-weight: 600; -fx-background-color: #141A24; -fx-border-color: #2D3A4F; -fx-border-radius: 6; -fx-text-fill: #CBD5E1; -fx-padding: 7 14; -fx-cursor: hand;");

        Button refreshBtn = new Button("Làm mới");
        refreshBtn.setGraphic(IconUtils.icon("refresh", 13, Color.web("#CBD5E1")));
        refreshBtn.setGraphicTextGap(6);
        refreshBtn.getStyleClass().add("btn-outline");
        refreshBtn.setStyle(
                "-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-font-weight: 600; -fx-background-color: #141A24; -fx-border-color: #2D3A4F; -fx-border-radius: 6; -fx-text-fill: #CBD5E1; -fx-padding: 7 14; -fx-cursor: hand;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, search, statusFilter, searchBtn, refreshBtn, spacer);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Table
        TableView<Booking> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setItems(pagedData);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có lượt đặt phòng nào trong hệ thống."));
        table.setStyle("-fx-font-family: 'Inter', sans-serif;");

        // ── Pagination Bar & Logic ────────────────────────────────────────────
        int[] currentPage = new int[]{1};
        int[] currentPageSize = new int[]{PaginationHelper.DEFAULT_PAGE_SIZE};
        PaginationHelper.PaginationBar paginationBar = new PaginationHelper.PaginationBar("đơn đặt phòng");

        final Runnable[] applyFilterRef = new Runnable[1];
        applyFilterRef[0] = () -> {
            String q = search.getText() == null ? "" : search.getText().trim().toLowerCase();
            String st = statusFilter.getValue();

            List<Booking> res = masterData.stream().filter(b -> {
                boolean matchSearch = q.isEmpty()
                        || (b.id != null && String.valueOf(b.id).contains(q))
                        || (b.code != null && b.code.toLowerCase().contains(q))
                        || (b.customer != null && b.customer.fullName != null
                                && b.customer.fullName.toLowerCase().contains(q))
                        || (b.customer != null && b.customer.phone != null && b.customer.phone.contains(q))
                        || (b.customer != null && b.customer.citizenId != null && b.customer.citizenId.contains(q))
                        || (b.room != null && b.room.number != null && b.room.number.toLowerCase().contains(q));

                boolean matchStatus = st == null || "Tất cả trạng thái".equals(st)
                        || b.statusLabel().equalsIgnoreCase(st);

                return matchSearch && matchStatus;
            }).collect(Collectors.toList());

            int totalElements = res.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalElements / currentPageSize[0]));
            if (currentPage[0] > totalPages) currentPage[0] = totalPages;
            if (currentPage[0] < 1) currentPage[0] = 1;

            int from = totalElements == 0 ? 0 : (currentPage[0] - 1) * currentPageSize[0];
            int to = Math.min(from + currentPageSize[0], totalElements);

            pagedData.setAll(totalElements > 0 ? res.subList(from, to) : List.of());
            table.scrollTo(0);

            paginationBar.update(currentPage[0], currentPageSize[0], totalElements, newPage -> {
                currentPage[0] = newPage;
                applyFilterRef[0].run();
            });

            updateKpi.run();
        };
        Runnable applyFilter = () -> applyFilterRef[0].run();

        Runnable onFilterChange = () -> {
            currentPage[0] = 1;
            applyFilter.run();
        };

        search.textProperty().addListener((obs, oldVal, newVal) -> onFilterChange.run());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> onFilterChange.run());
        searchBtn.setOnAction(e -> onFilterChange.run());

        paginationBar.getPageSizeCombo().setOnAction(e -> {
            Integer selectedSize = paginationBar.getPageSizeCombo().getValue();
            if (selectedSize != null && selectedSize != currentPageSize[0]) {
                currentPageSize[0] = selectedSize;
                currentPage[0] = 1;
                applyFilter.run();
            }
        });

        TableColumn<Booking, String> idCol = new TableColumn<>("MÃ ĐẶT");
        idCol.setMinWidth(95);
        idCol.setPrefWidth(105);
        idCol.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().code != null ? d.getValue().code : "DP" + d.getValue().id));
        idCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle(
                            "-fx-font-family: 'Inter', sans-serif; -fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-alignment: CENTER_LEFT; -fx-font-size: 13px;");
                }
            }
        });

        TableColumn<Booking, Void> custCol = new TableColumn<>("KHÁCH HÀNG");
        custCol.setMinWidth(200);
        custCol.setPrefWidth(225);
        custCol.setCellFactory(col -> new TableCell<>() {
            private final VBox box = new VBox(2);
            private final Label nameLbl = new Label();
            private final Label phoneLbl = new Label();

            {
                nameLbl.setStyle(
                        "-fx-font-family: 'Inter', sans-serif; -fx-font-weight: 700; -fx-text-fill: #FFFFFF; -fx-font-size: 13px;");
                phoneLbl.setStyle(
                        "-fx-font-family: 'Inter', sans-serif; -fx-font-size: 11.5px; -fx-text-fill: #8C9CB2;");
                box.getChildren().addAll(nameLbl, phoneLbl);
                box.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Booking b = getTableView().getItems().get(getIndex());
                    if (b.customer != null) {
                        nameLbl.setText(b.customer.fullName != null ? b.customer.fullName : "Khách vãng lai");
                        phoneLbl.setText(
                                "📞 " + (b.customer.phone != null && !b.customer.phone.isBlank() ? b.customer.phone
                                        : "Chưa có SĐT"));
                    } else {
                        nameLbl.setText("—");
                        phoneLbl.setText("");
                    }
                    setGraphic(box);
                }
            }
        });

        TableColumn<Booking, Void> roomCol = new TableColumn<>("PHÒNG");
        roomCol.setMinWidth(140);
        roomCol.setPrefWidth(160);
        roomCol.setCellFactory(col -> new TableCell<>() {
            private final VBox box = new VBox(2);
            private final Label roomLbl = new Label();
            private final Label typeLbl = new Label();

            {
                roomLbl.setStyle(
                        "-fx-font-family: 'Montserrat', sans-serif; -fx-font-weight: 800; -fx-text-fill: #FCD34D; -fx-font-size: 13px;");
                typeLbl.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 11px; -fx-text-fill: #94A3B8;");
                box.getChildren().addAll(roomLbl, typeLbl);
                box.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Booking b = getTableView().getItems().get(getIndex());
                    if (b.room != null) {
                        roomLbl.setText("Phòng " + b.room.number);
                        typeLbl.setText(b.room.roomType != null ? b.room.roomType.name : "Tiêu chuẩn");
                    } else {
                        roomLbl.setText("—");
                        typeLbl.setText("");
                    }
                    setGraphic(box);
                }
            }
        });

        TableColumn<Booking, String> guestsCol = new TableColumn<>("SỐ KHÁCH");
        guestsCol.setMinWidth(90);
        guestsCol.setPrefWidth(100);
        guestsCol.setCellValueFactory(d -> new SimpleStringProperty(
                (d.getValue().numberOfGuests != null ? d.getValue().numberOfGuests : 1) + " người"));
        guestsCol.setStyle("-fx-alignment: CENTER_LEFT; -fx-font-weight: 600; -fx-text-fill: #E2E8F0;");

        TableColumn<Booking, String> inCol = new TableColumn<>("NGÀY NHẬN");
        inCol.setMinWidth(110);
        inCol.setPrefWidth(120);
        inCol.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().checkIn != null ? d.getValue().checkIn : "—"));
        inCol.setStyle("-fx-text-fill: #E2E8F0;");

        TableColumn<Booking, String> outCol = new TableColumn<>("DỰ KIẾN TRẢ");
        outCol.setMinWidth(110);
        outCol.setPrefWidth(120);
        outCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().checkOutPlanned != null ? d.getValue().checkOutPlanned : "—"));
        outCol.setStyle("-fx-text-fill: #E2E8F0;");

        TableColumn<Booking, String> actInCol = new TableColumn<>("NHẬN THỰC TẾ");
        actInCol.setMinWidth(135);
        actInCol.setPrefWidth(145);
        actInCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().actualCheckIn != null ? d.getValue().actualCheckIn : "Chưa nhận"));
        actInCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    if ("Chưa nhận".equals(item)) {
                        setStyle("-fx-text-fill: #64748B; -fx-font-style: italic;");
                    } else {
                        setStyle("-fx-text-fill: #34D399; -fx-font-weight: 600;");
                    }
                }
            }
        });

        TableColumn<Booking, String> actOutCol = new TableColumn<>("TRẢ THỰC TẾ");
        actOutCol.setMinWidth(135);
        actOutCol.setPrefWidth(145);
        actOutCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().actualCheckOut != null ? d.getValue().actualCheckOut : "Chưa trả"));
        actOutCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    if ("Chưa trả".equals(item)) {
                        setStyle("-fx-text-fill: #64748B; -fx-font-style: italic;");
                    } else {
                        setStyle("-fx-text-fill: #60A5FA; -fx-font-weight: 600;");
                    }
                }
            }
        });

        TableColumn<Booking, String> statusCol = new TableColumn<>("TRẠNG THÁI");
        statusCol.setMinWidth(135);
        statusCol.setPrefWidth(145);
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().statusLabel()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Booking b = getTableView().getItems().get(getIndex());
                    Label badge = new Label(item);
                    badge.getStyleClass().addAll("badge", b.badgeClass());
                    badge.setStyle(
                            "-fx-font-size: 11.5px; -fx-font-weight: 800; -fx-padding: 4 12; -fx-background-radius: 6;");
                    setGraphic(badge);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Booking, String> noteCol = new TableColumn<>("GHI CHÚ");
        noteCol.setPrefWidth(150);
        noteCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().notes != null && !d.getValue().notes.isBlank() ? d.getValue().notes : "—"));
        noteCol.setStyle("-fx-text-fill: #94A3B8;");

        // Action Column: Check-in, Check-out, Cancel
        TableColumn<Booking, Void> actionCol = new TableColumn<>("THAO TÁC");
        actionCol.setMinWidth(280);
        actionCol.setPrefWidth(295);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button checkInBtn = new Button("Nhận phòng");
            private final Button checkOutBtn = new Button("Trả phòng");
            private final Button cancelBtn = new Button("Hủy");
            private final Button serviceBtn = new Button("+ Dịch vụ");
            private final Label completedLbl = new Label("Đã hoàn tất");
            private final HBox box = new HBox(6);

            {
                checkInBtn.getStyleClass().add("btn-gold");
                checkInBtn.setGraphic(IconUtils.icon("check", 11, Color.web("#0A0D14")));
                checkInBtn.setGraphicTextGap(5);
                checkInBtn.setStyle(
                        "-fx-padding: 4 10; -fx-font-size: 11.5px; -fx-font-weight: 700; -fx-background-color: #F59E0B; -fx-text-fill: #0A0D14; -fx-background-radius: 6; -fx-cursor: hand;");

                checkOutBtn.setStyle(
                        "-fx-padding: 4 10; -fx-font-size: 11.5px; -fx-font-weight: 700; -fx-background-color: #10B981; -fx-text-fill: #FFFFFF; -fx-background-radius: 6; -fx-cursor: hand;");
                checkOutBtn.setGraphic(IconUtils.icon("exit", 11, Color.web("#FFFFFF")));
                checkOutBtn.setGraphicTextGap(5);

                cancelBtn.getStyleClass().add("btn-danger");
                cancelBtn.setGraphic(IconUtils.icon("cancel", 11, Color.web("#F87171")));
                cancelBtn.setGraphicTextGap(5);
                cancelBtn.setStyle(
                        "-fx-padding: 4 9; -fx-font-size: 11.5px; -fx-font-weight: 600; -fx-background-color: rgba(239, 68, 68, 0.15); -fx-text-fill: #F87171; -fx-border-color: rgba(239, 68, 68, 0.4); -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");

                serviceBtn.getStyleClass().add("btn-outline");
                serviceBtn.setGraphic(IconUtils.icon("bell", 11, Color.web("#38BDF8")));
                serviceBtn.setGraphicTextGap(4);
                serviceBtn.setStyle(
                        "-fx-padding: 4 9; -fx-font-size: 11.5px; -fx-font-weight: 600; -fx-border-color: rgba(56, 189, 248, 0.4); -fx-text-fill: #38BDF8; -fx-background-radius: 6; -fx-border-radius: 6; -fx-cursor: hand;");

                completedLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B; -fx-font-weight: 600;");

                box.setAlignment(Pos.CENTER_LEFT);
                setAlignment(Pos.CENTER_LEFT);

                checkInBtn.setOnAction(e -> {
                    Booking b = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận nhận phòng");
                    alert.setHeaderText("Xác nhận khách nhận phòng " + (b.room != null ? b.room.number : "") + "?");
                    alert.setContentText("Trạng thái phòng sẽ được chuyển sang 'Đang ở'.");
                    alert.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt -> {
                        Util.runAsync(() -> ApiService.checkIn(b.id), res -> reload(masterData, applyFilter));
                    });
                });

                checkOutBtn.setOnAction(e -> {
                    Booking b = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận trả phòng");
                    alert.setHeaderText("Xác nhận trả phòng " + (b.room != null ? b.room.number : "") + "?");
                    alert.setContentText("Trạng thái phòng sẽ được chuyển sang 'Đang dọn'.");
                    alert.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt -> {
                        Util.runAsync(() -> ApiService.checkOutBooking(b.id), res -> reload(masterData, applyFilter));
                    });
                });

                cancelBtn.setOnAction(e -> {
                    Booking b = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận hủy đặt phòng");
                    alert.setHeaderText("Hủy lượt đặt phòng " + (b.code != null ? b.code : "DP" + b.id) + "?");
                    alert.setContentText("Bạn có chắc chắn muốn hủy đặt phòng này không?");
                    alert.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt -> {
                        Util.runAsync(() -> ApiService.cancelBooking(b.id), res -> reload(masterData, applyFilter));
                    });
                });

                serviceBtn.setOnAction(e -> {
                    Booking b = getTableView().getItems().get(getIndex());
                    BookingServiceDetailsView.openAddDialog(b.id, () -> reload(masterData, applyFilter));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Booking b = getTableView().getItems().get(getIndex());
                    box.getChildren().clear();

                    String st = b.status != null ? b.status.toUpperCase() : "DAT_TRUOC";
                    if ("DAT_TRUOC".equals(st) || "CHO_XAC_NHAN".equals(st)) {
                        box.getChildren().addAll(checkInBtn, serviceBtn, cancelBtn);
                    } else if ("DANG_O".equals(st) || "DANG_SU_DUNG".equals(st)) {
                        box.getChildren().addAll(checkOutBtn, serviceBtn, cancelBtn);
                    } else if ("DA_TRA".equals(st) || "DA_TRA_PHONG".equals(st)) {
                        completedLbl.setText("✓ Đã hoàn tất");
                        completedLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #10B981; -fx-font-weight: 600;");
                        box.getChildren().addAll(completedLbl, serviceBtn);
                    } else if ("DA_HUY".equals(st) || "HUY".equals(st)) {
                        completedLbl.setText("✕ Đã hủy");
                        completedLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #EF4444; -fx-font-weight: 600;");
                        box.getChildren().add(completedLbl);
                    }
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(idCol, custCol, roomCol, guestsCol, inCol, outCol, actInCol, actOutCol, statusCol,
                noteCol, actionCol);

        // Reload logic
        Runnable loadAction = () -> reload(masterData, applyFilter);
        loadAction.run();

        refreshBtn.setOnAction(e -> {
            search.clear();
            statusFilter.setValue("Tất cả trạng thái");
            loadAction.run();
        });

        topAddBtn.setOnAction(e -> openAddDialog(loadAction));

        container.getChildren().addAll(headerRow, kpiGrid, toolbar, table, paginationBar);
        root.setCenter(container);
        return root;
    }

    private static GridPane buildKpiRow(Label totalLbl, Label stayingLbl, Label reservedLbl, Label completedLbl) {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25.0);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }

        grid.add(
                buildKpiCard("TỔNG ĐẶT PHÒNG", totalLbl, "Tất cả lượt đặt",
                        IconUtils.icon("document", 13, Color.web("#F59E0B")), "rgba(245, 158, 11, 0.15)", "#F59E0B"),
                0, 0);
        grid.add(buildKpiCard("ĐANG LƯU TRÚ", stayingLbl, "Khách đang ở phòng",
                IconUtils.icon("bed", 13, Color.web("#8B5CF6")), "rgba(139, 92, 246, 0.15)", "#A78BFA"), 1, 0);
        grid.add(
                buildKpiCard("ĐÃ ĐẶT TRƯỚC", reservedLbl, "Chờ khách nhận phòng",
                        IconUtils.icon("clock", 13, Color.web("#38BDF8")), "rgba(56, 189, 248, 0.15)", "#38BDF8"),
                2, 0);
        grid.add(
                buildKpiCard("ĐÃ TRẢ PHÒNG", completedLbl, "Lưu trú hoàn tất",
                        IconUtils.icon("check", 13, Color.web("#10B981")), "rgba(16, 185, 129, 0.15)", "#34D399"),
                3, 0);

        return grid;
    }

    private static VBox buildKpiCard(String title, Label valLbl, String subtext, Node icon, String badgeBg,
            String valColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 16, 12, 16));
        card.setStyle(
                "-fx-background-color: #121824; -fx-border-color: #1E293B; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(title);
        l.setStyle(
                "-fx-text-fill: #8C9CB2; -fx-font-size: 11px; -fx-font-weight: 700; -fx-font-family: 'Inter', sans-serif;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        StackPane badge = new StackPane(icon);
        badge.setPrefSize(26, 26);
        badge.setStyle("-fx-background-color: " + badgeBg + "; -fx-background-radius: 6;");

        top.getChildren().addAll(l, sp, badge);

        valLbl.setStyle(
                "-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: "
                        + valColor + ";");

        Label sub = new Label(subtext);
        sub.setStyle("-fx-text-fill: #8C9CB2; -fx-font-size: 11px; -fx-font-family: 'Inter', sans-serif;");

        card.getChildren().addAll(top, valLbl, sub);
        return card;
    }

    private static void reload(ObservableList<Booking> masterData, Runnable applyFilter) {
        Util.runAsync(ApiService::bookings, list -> {
            masterData.setAll(list);
            applyFilter.run();
        });
    }

    private static void openAddDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Đặt phòng & Đăng ký lưu trú mới");
        dialog.setHeaderText("Tạo lượt đặt phòng mới cho khách hàng");
        dialog.getDialogPane().getStylesheets().add(
                BookingsView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        // Choice: Khách hàng có sẵn hoặc khách hàng mới
        RadioButton rbExisting = new RadioButton("Khách hàng đã có trong hệ thống");
        RadioButton rbNew = new RadioButton("Nhập thông tin khách hàng mới");
        ToggleGroup custGroup = new ToggleGroup();
        rbExisting.setToggleGroup(custGroup);
        rbNew.setToggleGroup(custGroup);
        rbExisting.setSelected(true);
        rbExisting.setStyle("-fx-text-fill: #E2E8F0; -fx-font-weight: 600;");
        rbNew.setStyle("-fx-text-fill: #E2E8F0; -fx-font-weight: 600;");

        HBox custTypeRow = new HBox(16, rbExisting, rbNew);
        custTypeRow.setAlignment(Pos.CENTER_LEFT);

        ComboBox<Customer> customerBox = new ComboBox<>();
        customerBox.setPromptText("— Chọn khách hàng có sẵn —");
        customerBox.setMaxWidth(Double.MAX_VALUE);
        Util.runAsync(() -> ApiService.customers(null), customerBox.getItems()::setAll);

        TextField newName = new TextField();
        newName.setPromptText("Họ và tên khách hàng (*)");
        TextField newPhone = new TextField();
        newPhone.setPromptText("Số điện thoại khách hàng (*)");
        TextField newCitizenId = new TextField();
        newCitizenId.setPromptText("Số CCCD / Hộ chiếu (12 số)");

        VBox existingBox = new VBox(6, customerBox);
        VBox newBox = new VBox(8, newName, newPhone, newCitizenId);
        newBox.setVisible(false);
        newBox.setManaged(false);

        custGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isNew = newVal == rbNew;
            existingBox.setVisible(!isNew);
            existingBox.setManaged(!isNew);
            newBox.setVisible(isNew);
            newBox.setManaged(isNew);
        });

        ComboBox<Room> roomBox = new ComboBox<>();
        roomBox.setPromptText("— Chọn phòng trống —");
        roomBox.setMaxWidth(Double.MAX_VALUE);
        Util.runAsync(ApiService::rooms, rooms -> rooms.stream()
                .filter(r -> "TRONG".equals(r.status))
                .forEach(roomBox.getItems()::add));

        DatePicker checkIn = new DatePicker(LocalDate.now());
        DatePicker checkOut = new DatePicker(LocalDate.now().plusDays(1));

        Spinner<Integer> guestsSpinner = new Spinner<>(1, 10, 1);
        guestsSpinner.setEditable(true);
        guestsSpinner.setPrefWidth(120);

        CheckBox checkInNow = new CheckBox("Nhận phòng ngay (Khách làm thủ tục Check-in trực tiếp)");
        checkInNow.setSelected(true);
        checkInNow.setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: 700;");

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Ghi chú đặc biệt (yêu cầu phòng tầng cao, giường phụ...)");
        notesArea.setPrefRowCount(2);

        VBox form = new VBox(12,
                labeledBox("LỰA CHỌN KHÁCH HÀNG", custTypeRow),
                existingBox,
                newBox,
                labeledBox("PHÒNG TRỐNG (*)", roomBox),
                new HBox(12, labeledBox("NGÀY NHẬN (*)", checkIn), labeledBox("NGÀY TRẢ DỰ KIẾN (*)", checkOut),
                        labeledBox("SỐ KHÁCH", guestsSpinner)),
                checkInNow,
                labeledBox("GHI CHÚ", notesArea));
        form.setPadding(new Insets(16));
        form.setPrefWidth(480);
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText("Xác nhận đặt phòng");
        okBtn.getStyleClass().add("btn-gold");

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                if (roomBox.getValue() == null) {
                    Util.error("Vui lòng chọn phòng trống.");
                    return null;
                }

                Long customerId = null;
                String cName = null;
                String cPhone = null;
                String cCccd = null;

                if (rbExisting.isSelected()) {
                    if (customerBox.getValue() == null) {
                        Util.error("Vui lòng chọn khách hàng có sẵn hoặc chuyển sang nhập khách mới.");
                        return null;
                    }
                    customerId = customerBox.getValue().id;
                } else {
                    cName = newName.getText().trim();
                    cPhone = newPhone.getText().trim();
                    cCccd = newCitizenId.getText().trim();
                    if (cName.isBlank()) {
                        Util.error("Họ và tên khách hàng không được để trống.");
                        return null;
                    }
                    if (cPhone.isBlank()) {
                        Util.error("Số điện thoại không được để trống.");
                        return null;
                    }
                }

                if (checkIn.getValue() == null || checkOut.getValue() == null) {
                    Util.error("Vui lòng chọn ngày nhận và ngày trả phòng.");
                    return null;
                }

                if (checkOut.getValue().isBefore(checkIn.getValue())) {
                    Util.error("Ngày trả phòng dự kiến phải bằng hoặc sau ngày nhận phòng.");
                    return null;
                }

                final Long finalCustomerId = customerId;
                final String finalName = cName;
                final String finalPhone = cPhone;
                final String finalCccd = cCccd;
                final Long roomId = roomBox.getValue().id;
                final String inStr = checkIn.getValue().toString();
                final String outStr = checkOut.getValue().toString();
                final Integer guests = guestsSpinner.getValue();
                final String notes = notesArea.getText().trim();
                final boolean isNow = checkInNow.isSelected();

                Util.runAsync(() -> ApiService.createBooking(
                        finalCustomerId, finalName, finalPhone, finalCccd,
                        roomId, inStr, outStr, guests, notes, isNow), b -> reload.run());
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static VBox labeledBox(String label, javafx.scene.Node field) {
        Label l = new Label(label);
        l.setStyle(
                "-fx-font-family: 'Inter', sans-serif; -fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #94A3B8;");
        return new VBox(4, l, field);
    }
}
