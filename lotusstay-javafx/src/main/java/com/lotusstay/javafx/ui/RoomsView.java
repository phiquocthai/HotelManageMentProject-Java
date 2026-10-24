package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Booking;
import com.lotusstay.javafx.model.Customer;
import com.lotusstay.javafx.model.Room;
import com.lotusstay.javafx.model.RoomType;
import com.lotusstay.javafx.net.ApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Giao diện Sơ đồ phòng (Room Grid Diagram) - chuẩn theo thiết kế Dark PMS Luxury.
 * Mặc định hiển thị sơ đồ thẻ phòng trực quan với chỉ báo màu sắc trạng thái.
 */
public class RoomsView {

    public static BorderPane build(MainShell shell) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20, 24, 24, 24));
        root.setStyle("-fx-background-color: transparent;");

        VBox container = new VBox(18);
        VBox.setVgrow(container, Priority.ALWAYS);

        // Data collection
        ObservableList<Room> masterData = FXCollections.observableArrayList();
        FilteredList<Room> filteredData = new FilteredList<>(masterData, p -> true);

        // ── 1. TOP TOOLBAR: LEGEND & ACTIONS (Khớp 100% Screenshot) ────────────
        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(4, 0, 8, 0));

        // Legend items (Dot + Text)
        HBox legendBox = new HBox(18);
        legendBox.setAlignment(Pos.CENTER_LEFT);
        legendBox.getChildren().addAll(
                buildLegendItem("#10B981", "Trống"),
                buildLegendItem("#8B5CF6", "Đang ở"),
                buildLegendItem("#F59E0B", "Đang dọn"),
                buildLegendItem("#EF4444", "Đang bảo trì")
        );

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        // Chuyển đổi Sơ đồ phòng / Bảng danh sách
        ToggleButton gridToggle = new ToggleButton("Sơ đồ phòng");
        gridToggle.setGraphic(IconUtils.icon("grid", 13, Color.web("#F59E0B")));
        gridToggle.setGraphicTextGap(6);

        ToggleButton tableToggle = new ToggleButton("Danh sách bảng");
        tableToggle.setGraphic(IconUtils.icon("table", 13, Color.web("#94A3B8")));
        tableToggle.setGraphicTextGap(6);

        ToggleGroup viewGroup = new ToggleGroup();
        gridToggle.setToggleGroup(viewGroup);
        tableToggle.setToggleGroup(viewGroup);
        gridToggle.setSelected(true); // Mặc định hiển thị Sơ đồ phòng
        gridToggle.getStyleClass().add("btn-outline");
        tableToggle.getStyleClass().add("btn-outline");
        gridToggle.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 6 14; -fx-cursor: hand;");
        tableToggle.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 6 14; -fx-cursor: hand;");

        HBox viewSwitcher = new HBox(6, gridToggle, tableToggle);
        viewSwitcher.setAlignment(Pos.CENTER_RIGHT);

        // Nút + Thêm phòng (vàng gold nổi bật chuẩn ảnh 1)
        Button addBtn = new Button("Thêm phòng");
        addBtn.setGraphic(IconUtils.icon("plus", 14, Color.web("#0A0D14")));
        addBtn.setGraphicTextGap(6);
        addBtn.getStyleClass().add("btn-add-room-gold");
        addBtn.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 13.5px; -fx-font-weight: 800; -fx-padding: 8 20; -fx-background-color: #F59E0B; -fx-text-fill: #0A0D14; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(245,158,11,0.35), 8, 0, 0, 2);");

        topBar.getChildren().addAll(legendBox, topSpacer, viewSwitcher, addBtn);

        // ── 2. SƠ ĐỒ THẺ PHÒNG (GRID CARDS - VIEW MẶC ĐỊNH CHUẨN ẢNH 1) ────────
        FlowPane roomGrid = new FlowPane(16, 16);
        roomGrid.setPadding(new Insets(6, 0, 24, 0));
        roomGrid.setPrefWrapLength(5 * 215 + 4 * 16 + 20); // Chuẩn 5 thẻ / hàng y hệt ảnh 1

        ScrollPane gridScroll = new ScrollPane(roomGrid);
        gridScroll.setFitToWidth(true);
        gridScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        gridScroll.getStyleClass().add("room-diagram-scroll");
        VBox.setVgrow(gridScroll, Priority.ALWAYS);

        // ── 3. BẢNG DANH SÁCH (TABLE VIEW - TÙY CHỌN BỔ TRỢ) ─────────────────
        VBox tableContainer = new VBox(12);
        tableContainer.setVisible(false);
        tableContainer.setManaged(false);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        // Bộ lọc khi ở chế độ bảng
        TextField search = new TextField();
        search.setPromptText("Tìm theo số phòng, mã phòng...");
        search.setPrefWidth(220);

        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.getItems().add("Tất cả loại phòng");
        typeFilter.setValue("Tất cả loại phòng");
        typeFilter.setPrefWidth(160);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Tất cả trạng thái", "Trống", "Đang ở", "Đang dọn", "Bảo trì");
        statusFilter.setValue("Tất cả trạng thái");
        statusFilter.setPrefWidth(140);

        HBox filterBar = new HBox(10, search, typeFilter, statusFilter);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        TableView<Room> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setItems(filteredData);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Không có phòng nào phù hợp."));

        TableColumn<Room, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(75);
        idCol.setPrefWidth(85);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().id)));
        idCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER; -fx-font-size: 13.5px; -fx-text-fill: #FFFFFF;");

        TableColumn<Room, String> codeCol = new TableColumn<>("Mã phòng");
        codeCol.setMinWidth(115);
        codeCol.setPrefWidth(125);
        codeCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().code != null ? d.getValue().code : "P" + d.getValue().number));
        codeCol.setStyle("-fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 13.5px;");

        TableColumn<Room, String> numberCol = new TableColumn<>("Số phòng");
        numberCol.setMinWidth(105);
        numberCol.setPrefWidth(115);
        numberCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().number));
        numberCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER; -fx-font-size: 14px; -fx-text-fill: #FFFFFF;");

        TableColumn<Room, String> typeCol = new TableColumn<>("Loại phòng");
        typeCol.setMinWidth(180);
        typeCol.setPrefWidth(210);
        typeCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().roomType != null ? d.getValue().roomType.name : "—"));
        typeCol.setStyle("-fx-font-weight: 600;");

        TableColumn<Room, String> floorCol = new TableColumn<>("Tầng");
        floorCol.setMinWidth(85);
        floorCol.setPrefWidth(95);
        floorCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().floor != null ? d.getValue().floor : 1)));
        floorCol.setStyle("-fx-font-weight: 700; -fx-alignment: CENTER;");

        TableColumn<Room, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setMinWidth(130);
        statusCol.setPrefWidth(140);
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().statusLabel()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Room r = getTableView().getItems().get(getIndex());
                    Label badge = new Label(item);
                    badge.getStyleClass().addAll("badge", r.badgeClass());
                    badge.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 12;");
                    setGraphic(badge);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Room, String> noteCol = new TableColumn<>("Ghi chú");
        noteCol.setMinWidth(180);
        noteCol.setPrefWidth(220);
        noteCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().notes != null && !d.getValue().notes.isBlank() ? d.getValue().notes : "—"));

        TableColumn<Room, Void> actionCol = new TableColumn<>("Thao tác");
        actionCol.setMinWidth(180);
        actionCol.setPrefWidth(190);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Sửa");
            private final Button delBtn = new Button("Xóa");
            private final HBox box = new HBox(8, editBtn, delBtn);

            {
                editBtn.getStyleClass().add("btn-outline");
                editBtn.setGraphic(IconUtils.icon("edit", 12, Color.web("#93C5FD")));
                editBtn.setGraphicTextGap(6);
                editBtn.setMinWidth(68);
                editBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");

                delBtn.getStyleClass().add("btn-danger");
                delBtn.setGraphic(IconUtils.icon("trash", 12, Color.web("#F87171")));
                delBtn.setGraphicTextGap(6);
                delBtn.setMinWidth(68);
                delBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");

                box.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    Room r = getTableView().getItems().get(getIndex());
                    openRoomDialog(r, () -> reload(masterData, null));
                });

                delBtn.setOnAction(e -> {
                    Room r = getTableView().getItems().get(getIndex());
                    confirmDelete(r, () -> reload(masterData, null));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(idCol, codeCol, numberCol, typeCol, floorCol, statusCol, noteCol, actionCol);
        tableContainer.getChildren().addAll(filterBar, table);

        // ── 4. RENDER VÀ ĐỒNG BỘ SƠ ĐỒ THẺ PHÒNG ──────────────────────────────
        Runnable refreshGrid = () -> {
            roomGrid.getChildren().clear();
            for (Room r : filteredData) {
                roomGrid.getChildren().add(buildDiagramCard(r, shell, () -> reload(masterData, null)));
            }
        };

        // Chuyển đổi giữa Sơ đồ phòng và Bảng
        gridToggle.setOnAction(e -> {
            gridScroll.setVisible(true);
            gridScroll.setManaged(true);
            tableContainer.setVisible(false);
            tableContainer.setManaged(false);
            refreshGrid.run();
        });

        tableToggle.setOnAction(e -> {
            gridScroll.setVisible(false);
            gridScroll.setManaged(false);
            tableContainer.setVisible(true);
            tableContainer.setManaged(true);
        });

        // Tải dữ liệu ban đầu
        Runnable loadAction = () -> reload(masterData, refreshGrid);
        loadAction.run();

        // Nút thêm phòng
        addBtn.setOnAction(e -> openRoomDialog(null, loadAction));

        // Nạp danh mục loại phòng vào bộ lọc bảng
        Util.runAsync(ApiService::roomTypes, list -> {
            for (RoomType rt : list) {
                if (!typeFilter.getItems().contains(rt.name)) {
                    typeFilter.getItems().add(rt.name);
                }
            }
        });

        container.getChildren().addAll(topBar, gridScroll, tableContainer);
        root.setCenter(container);
        return root;
    }

    /**
     * Tạo 1 thẻ phòng chuẩn giao diện Sơ đồ phòng (ảnh screenshot của người dùng)
     */
    /**
     * Tạo 1 thẻ phòng chuẩn giao diện Sơ đồ phòng (ảnh 1 của người dùng)
     */
    private static VBox buildDiagramCard(Room r, MainShell shell, Runnable reload) {
        VBox card = new VBox(8);
        card.getStyleClass().add("room-diagram-card");
        card.setPrefWidth(215);
        card.setMinWidth(195);
        card.setPrefHeight(120);

        String st = r.status == null ? "TRONG" : r.status.toUpperCase();
        String borderCol = "#10B981"; // default xanh trống

        switch (st) {
            case "DANG_O" -> {
                card.getStyleClass().add("status-dang_o");
                borderCol = "#8B5CF6";
            }
            case "DANG_DON" -> {
                card.getStyleClass().add("status-dang_don");
                borderCol = "#F59E0B";
            }
            case "BAO_TRI", "DANG_BAO_TRI" -> {
                card.getStyleClass().add("status-bao_tri");
                borderCol = "#EF4444";
            }
            default -> {
                card.getStyleClass().add("status-trong");
                borderCol = "#10B981";
            }
        }

        final String finalBorderCol = borderCol;

        // Inline style dự phòng để đảm bảo 100% hiển thị thẻ nổi chuẩn ảnh 1
        card.setStyle("-fx-background-color: #141A23; -fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-border-color: #212936 #212936 #212936 " + finalBorderCol + "; -fx-border-width: 1 1 1 4; " +
                "-fx-padding: 14 18; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 8, 0, 0, 2);");

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #1B2436; -fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-border-color: #2D3748 #2D3748 #2D3748 " + finalBorderCol + "; -fx-border-width: 1 1 1 4; " +
                "-fx-padding: 14 18; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 12, 0, 0, 4);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #141A23; -fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-border-color: #212936 #212936 #212936 " + finalBorderCol + "; -fx-border-width: 1 1 1 4; " +
                "-fx-padding: 14 18; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 8, 0, 0, 2);"));

        // 1. Tên/Mã phòng lớn màu trắng (P101, P102...)
        String displayNum = r.number != null ? (r.number.startsWith("P") ? r.number : "P" + r.number) : "P000";
        Label numberLabel = new Label(displayNum);
        numberLabel.getStyleClass().add("room-diagram-number");
        numberLabel.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");

        // 2. Thông tin phụ (Phòng Đôi · Tầng 1)
        String typeName = (r.roomType != null && r.roomType.name != null) ? r.roomType.name : "Phòng";
        int floorNum = r.floor != null ? r.floor : 1;
        Label subLabel = new Label(typeName + " · Tầng " + floorNum);
        subLabel.getStyleClass().add("room-diagram-sub");
        subLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-font-weight: 500; -fx-text-fill: #8C9CB2;");

        // 3. Badge trạng thái pill bo góc
        Label badgeLabel = new Label();
        badgeLabel.getStyleClass().add("room-diagram-badge");
        badgeLabel.setMaxWidth(Region.USE_PREF_SIZE);

        switch (st) {
            case "DANG_O" -> {
                badgeLabel.setText("ĐANG Ở");
                badgeLabel.getStyleClass().add("badge-dark-dang_o");
                badgeLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 11px; -fx-font-weight: 800; -fx-padding: 3 10; -fx-background-radius: 8; -fx-background-color: rgba(139, 92, 246, 0.16); -fx-text-fill: #C4B5FD; -fx-border-color: rgba(139, 92, 246, 0.35); -fx-border-radius: 8; -fx-border-width: 1;");
            }
            case "DANG_DON" -> {
                badgeLabel.setText("ĐANG DỌN");
                badgeLabel.getStyleClass().add("badge-dark-dang_don");
                badgeLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 11px; -fx-font-weight: 800; -fx-padding: 3 10; -fx-background-radius: 8; -fx-background-color: rgba(245, 158, 11, 0.16); -fx-text-fill: #FBBF24; -fx-border-color: rgba(245, 158, 11, 0.35); -fx-border-radius: 8; -fx-border-width: 1;");
            }
            case "BAO_TRI", "DANG_BAO_TRI" -> {
                badgeLabel.setText("ĐANG BẢO TRÌ");
                badgeLabel.getStyleClass().add("badge-dark-bao_tri");
                badgeLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 11px; -fx-font-weight: 800; -fx-padding: 3 10; -fx-background-radius: 8; -fx-background-color: rgba(239, 68, 68, 0.16); -fx-text-fill: #F87171; -fx-border-color: rgba(239, 68, 68, 0.35); -fx-border-radius: 8; -fx-border-width: 1;");
            }
            default -> {
                badgeLabel.setText("TRỐNG");
                badgeLabel.getStyleClass().add("badge-dark-trong");
                badgeLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 11px; -fx-font-weight: 800; -fx-padding: 3 10; -fx-background-radius: 8; -fx-background-color: rgba(16, 185, 129, 0.16); -fx-text-fill: #34D399; -fx-border-color: rgba(16, 185, 129, 0.35); -fx-border-radius: 8; -fx-border-width: 1;");
            }
        }

        card.getChildren().addAll(numberLabel, subLabel, badgeLabel);

        // Bấm vào thẻ để thực hiện thao tác nhanh
        card.setOnMouseClicked(e -> handleRoomQuickAction(r, shell, reload));
        return card;
    }

    /**
     * Tạo 1 mục chú thích trên thanh legend (Chấm tròn màu + Chữ)
     */
    private static HBox buildLegendItem(String colorHex, String labelText) {
        Circle dot = new Circle(4.5);
        dot.setFill(Color.web(colorHex));

        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 13px; -fx-font-weight: 500;");

        HBox item = new HBox(8, dot, label);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    private static void reload(ObservableList<Room> masterData, Runnable afterReload) {
        Util.runAsync(ApiService::rooms, list -> {
            masterData.setAll(list);
            if (afterReload != null) afterReload.run();
        });
    }

    private static void confirmDelete(Room r, Runnable reload) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa phòng");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa phòng " + r.number + "?");
        alert.setContentText("Lưu ý: Không thể xóa phòng đang có khách lưu trú.");
        alert.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            Util.runAsyncVoid(() -> ApiService.deleteRoom(r.id), reload);
        });
    }

    private static void handleRoomQuickAction(Room r, MainShell shell, Runnable reload) {
        switch (r.status == null ? "TRONG" : r.status) {
            case "TRONG" -> openCheckInDialog(r, reload);
            case "DANG_O" -> openCheckOutDialog(r, reload);
            case "DANG_DON" -> confirmAndSetStatus(r, "TRONG", "Xác nhận đã dọn xong phòng " + r.number + "?", reload);
            case "BAO_TRI", "DANG_BAO_TRI" -> confirmAndSetStatus(r, "TRONG", "Xác nhận hoàn tất bảo trì phòng " + r.number + "?", reload);
            default -> openRoomDialog(r, reload);
        }
    }

    private static void confirmAndSetStatus(Room r, String newStatus, String question, Runnable reload) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, question);
        confirm.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt ->
                Util.runAsync(() -> ApiService.updateRoomStatus(r.id, newStatus), room -> reload.run()));
    }

    private static void openRoomDialog(Room existing, Runnable reload) {
        boolean isEdit = existing != null;
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Cập nhật thông tin phòng" : "Thêm mới phòng");
        dialog.getDialogPane().getStylesheets().add(
                RoomsView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        TextField code = new TextField(isEdit ? existing.code : "");
        code.setPromptText("Tự động (VD: P015)");
        if (isEdit) code.setDisable(true);

        TextField number = new TextField(isEdit ? existing.number : "");
        number.setPromptText("Số phòng (VD: 101, tối đa 5 ký tự)");

        ComboBox<RoomType> typeBox = new ComboBox<>();
        typeBox.setPromptText("— Chọn loại phòng —");
        typeBox.setMaxWidth(Double.MAX_VALUE);
        Util.runAsync(ApiService::roomTypes, list -> {
            typeBox.getItems().setAll(list);
            if (isEdit && existing.roomType != null) {
                list.stream().filter(t -> t.id.equals(existing.roomType.id)).findFirst().ifPresent(typeBox::setValue);
            }
        });

        TextField floor = new TextField(isEdit && existing.floor != null ? String.valueOf(existing.floor) : "1");
        floor.setPromptText("Tầng (VD: 1, 2, 3...)");

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("TRONG", "DANG_O", "DANG_DON", "BAO_TRI");
        statusBox.setValue(isEdit && existing.status != null ? existing.status : "TRONG");
        statusBox.setMaxWidth(Double.MAX_VALUE);

        TextArea notes = new TextArea(isEdit && existing.notes != null ? existing.notes : "");
        notes.setPromptText("Ghi chú phòng (hướng nhìn, trang bị đặc biệt...)");
        notes.setPrefRowCount(3);

        VBox form = new VBox(10,
                labeledBox("Mã phòng", code),
                labeledBox("Số phòng (so_phong) (*)", number),
                labeledBox("Loại phòng (*)", typeBox),
                labeledBox("Tầng (tang) (*)", floor),
                labeledBox("Trạng thái", statusBox),
                labeledBox("Ghi chú", notes)
        );
        form.setPadding(new Insets(16));
        form.setPrefWidth(420);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String numVal = number.getText().trim();
                if (numVal.isBlank()) {
                    Util.error("Số phòng không được để trống.");
                    return null;
                }
                if (typeBox.getValue() == null) {
                    Util.error("Vui lòng chọn loại phòng.");
                    return null;
                }
                int fl;
                try {
                    fl = Integer.parseInt(floor.getText().trim());
                } catch (Exception ex) {
                    Util.error("Tầng phải là số nguyên.");
                    return null;
                }

                String codeVal = code.getText().trim();
                Long typeId = typeBox.getValue().id;
                String st = statusBox.getValue();
                String noteVal = notes.getText().trim();

                if (isEdit) {
                    Util.runAsync(() -> ApiService.updateRoom(existing.id, codeVal, numVal, typeId, fl, st, noteVal),
                            r -> reload.run());
                } else {
                    Util.runAsync(() -> ApiService.createRoom(codeVal, numVal, typeId, fl, st, noteVal),
                            r -> reload.run());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static void openCheckInDialog(Room r, Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nhận phòng — Phòng " + r.number);
        dialog.getDialogPane().getStylesheets().add(
                RoomsView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        ComboBox<Customer> customerBox = new ComboBox<>();
        customerBox.setPromptText("— Chọn khách hàng có sẵn —");
        customerBox.setMaxWidth(Double.MAX_VALUE);
        Util.runAsync(() -> ApiService.customers(null), customerBox.getItems()::setAll);

        TextField newName = new TextField();
        newName.setPromptText("Tên khách mới (nếu không chọn khách có sẵn ở trên)");
        TextField newPhone = new TextField();
        newPhone.setPromptText("Số điện thoại khách mới");

        DatePicker checkIn = new DatePicker(LocalDate.now());
        DatePicker checkOut = new DatePicker(LocalDate.now().plusDays(1));

        VBox form = new VBox(10,
                new Label("Khách hàng"), customerBox,
                new Label("Hoặc tạo khách hàng mới trực tiếp"), newName, newPhone,
                new HBox(10, labeledBox("Ngày nhận", checkIn), labeledBox("Dự kiến trả", checkOut))
        );
        form.setPadding(new Insets(16));
        form.setPrefWidth(420);
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Long customerId = customerBox.getValue() != null ? customerBox.getValue().id : null;
                Util.runAsync(() -> ApiService.createBooking(
                        customerId, newName.getText(), newPhone.getText(),
                        r.id, checkIn.getValue().toString(), checkOut.getValue().toString(), true
                ), b -> reload.run());
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static void openCheckOutDialog(Room r, Runnable reload) {
        Util.runAsync(ApiService::bookings, bookings -> {
            Optional<Booking> active = bookings.stream()
                    .filter(b -> "DANG_O".equals(b.status) && b.room != null && b.room.id.equals(r.id))
                    .findFirst();

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Trả phòng — Phòng " + r.number);
            dialog.getDialogPane().getStylesheets().add(
                    RoomsView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

            Label guestInfo = new Label(active.map(b -> "Khách đang lưu trú: " + b.customer.fullName + " (" + b.customer.phone + ")")
                    .orElse("Không tìm thấy lượt lưu trú mở — thanh toán vãng lai."));
            guestInfo.getStyleClass().add("hint-text");

            TextField roomFee = new TextField(r.roomType != null ? r.roomType.basePrice.toPlainString() : "0");
            TextField penaltyFee = new TextField("0");
            DatePicker checkOutDate = new DatePicker(LocalDate.now());

            VBox form = new VBox(10, guestInfo,
                    labeledBox("Tiền phòng (VNĐ)", roomFee),
                    labeledBox("Tiền phụ thu / phạt phát sinh (nếu có)", penaltyFee),
                    labeledBox("Ngày trả phòng", checkOutDate));
            form.setPadding(new Insets(16));
            form.setPrefWidth(420);
            dialog.getDialogPane().setContent(form);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

            dialog.setResultConverter(bt -> {
                if (bt == ButtonType.OK) {
                    BigDecimal fee = parseMoney(roomFee.getText());
                    BigDecimal penalty = parseMoney(penaltyFee.getText());
                    Long bookingId = active.map(b -> b.id).orElse(null);
                    Long customerId = active.map(b -> b.customer.id).orElse(null);
                    Util.runAsync(() -> ApiService.checkout(
                            bookingId, r.id, customerId, checkOutDate.getValue().toString(),
                            fee, penalty, List.of()
                    ), inv -> reload.run());
                }
                return null;
            });
            dialog.showAndWait();
        });
    }

    private static BigDecimal parseMoney(String s) {
        try { return new BigDecimal(s.trim().isEmpty() ? "0" : s.trim()); }
        catch (Exception e) { return BigDecimal.ZERO; }
    }

    private static VBox labeledBox(String label, javafx.scene.Node field) {
        Label l = new Label(label);
        l.getStyleClass().add("form-label");
        return new VBox(4, l, field);
    }
}
