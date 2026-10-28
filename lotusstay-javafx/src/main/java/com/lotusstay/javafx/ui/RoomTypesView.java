package com.lotusstay.javafx.ui;

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

import java.math.BigDecimal;

public class RoomTypesView {

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

        Label title = new Label("Quản lý loại phòng");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 700;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        headerRow.getChildren().addAll(title, topSpacer);

        // Data collection
        ObservableList<RoomType> masterData = FXCollections.observableArrayList();
        FilteredList<RoomType> filteredData = new FilteredList<>(masterData, p -> true);

        // Top Toolbar với bộ lọc đa tiêu chí chuẩn: Tìm tên, Số người, Khoảng giá (Từ - Đến + Slider kéo)
        TextField search = new TextField();
        search.setPromptText("🔍 Tìm theo tên loại phòng...");
        search.setPrefWidth(260);
        search.setMinWidth(200);
        search.setPrefHeight(38);

        ComboBox<String> guestsFilter = new ComboBox<>();
        guestsFilter.getItems().addAll("Tất cả số khách", "1 người", "2 người", "3 người", "4 người", "5+ người");
        guestsFilter.setValue("Tất cả số khách");
        guestsFilter.setMinWidth(150);
        guestsFilter.setPrefWidth(150);
        guestsFilter.setPrefHeight(38);

        TextField minPrice = new TextField();
        minPrice.setPromptText("Giá từ (đ)");
        minPrice.setPrefWidth(140);
        minPrice.setMinWidth(130);
        minPrice.setPrefHeight(38);

        Label rangeDash = new Label("—");
        rangeDash.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: 700;");

        TextField maxPrice = new TextField();
        maxPrice.setPromptText("Giá đến (đ)");
        maxPrice.setPrefWidth(140);
        maxPrice.setMinWidth(130);
        maxPrice.setPrefHeight(38);

        // Thanh trượt mức giá trực quan
        Slider priceSlider = new Slider(0, 3000000, 3000000);
        priceSlider.setPrefWidth(160);
        priceSlider.setMinWidth(140);
        priceSlider.setBlockIncrement(50000);
        Label sliderLabel = new Label("Trần giá: Tất cả");
        sliderLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #F59E0B; -fx-font-weight: 700;");

        VBox sliderBox = new VBox(2, sliderLabel, priceSlider);
        sliderBox.setAlignment(Pos.CENTER_LEFT);

        Button searchBtn = new Button("Tìm kiếm");
        searchBtn.getStyleClass().add("btn-outline");
        searchBtn.setGraphic(IconUtils.icon("search", 13, Color.web("#94A3B8")));
        searchBtn.setMinWidth(Region.USE_PREF_SIZE);

        Button refreshBtn = new Button("Làm mới");
        refreshBtn.getStyleClass().add("btn-outline");
        refreshBtn.setGraphic(IconUtils.icon("refresh", 13, Color.web("#94A3B8")));
        refreshBtn.setMinWidth(Region.USE_PREF_SIZE);

        Button addBtn = new Button("Thêm loại phòng");
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setGraphic(IconUtils.icon("plus", 14, Color.web("#0A0D14")));
        addBtn.setMinWidth(Region.USE_PREF_SIZE);
        addBtn.setPrefWidth(160);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Bỏ nút 'Xóa mục đã chọn' theo yêu cầu
        HBox toolbar = new HBox(8, search, guestsFilter, minPrice, rangeDash, maxPrice, sliderBox, searchBtn, refreshBtn, spacer, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Filter đa tiêu chí (Tên + Số người tối đa + Khoảng giá từ - đến)
        Runnable applyFilter = () -> {
            String q = search.getText() == null ? "" : search.getText().trim().toLowerCase();
            String guestVal = guestsFilter.getValue();

            Double minVal = null;
            try {
                if (minPrice.getText() != null && !minPrice.getText().trim().isEmpty()) {
                    minVal = Double.parseDouble(minPrice.getText().trim().replaceAll("[^0-9.]", ""));
                }
            } catch (Exception ignored) {}

            Double maxVal = null;
            try {
                if (maxPrice.getText() != null && !maxPrice.getText().trim().isEmpty()) {
                    maxVal = Double.parseDouble(maxPrice.getText().trim().replaceAll("[^0-9.]", ""));
                }
            } catch (Exception ignored) {}

            final Double finalMin = minVal;
            final Double finalMax = maxVal;

            filteredData.setPredicate(rt -> {
                boolean matchName = q.isEmpty() || (rt.name != null && rt.name.toLowerCase().contains(q));

                boolean matchGuests = true;
                if (guestVal != null && !"Tất cả số khách".equals(guestVal)) {
                    int g = rt.maxGuests != null ? rt.maxGuests : 2;
                    if ("1 người".equals(guestVal)) matchGuests = (g == 1);
                    else if ("2 người".equals(guestVal)) matchGuests = (g == 2);
                    else if ("3 người".equals(guestVal)) matchGuests = (g == 3);
                    else if ("4 người".equals(guestVal)) matchGuests = (g == 4);
                    else if ("5+ người".equals(guestVal)) matchGuests = (g >= 5);
                }

                boolean matchPrice = true;
                if (rt.basePrice != null) {
                    double p = rt.basePrice.doubleValue();
                    if (finalMin != null && p < finalMin) matchPrice = false;
                    if (finalMax != null && p > finalMax) matchPrice = false;
                }

                return matchName && matchGuests && matchPrice;
            });
        };

        // Kéo thanh trượt sẽ tự động cập nhật ô giá đến và lọc bảng realtime
        priceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double v = newVal.doubleValue();
            if (v >= 2950000) {
                sliderLabel.setText("Trần giá: Tất cả");
                maxPrice.clear();
            } else {
                long rounded = Math.round(v / 50000.0) * 50000;
                sliderLabel.setText("Trần: " + Util.money(BigDecimal.valueOf(rounded)));
                maxPrice.setText(String.valueOf(rounded));
            }
            applyFilter.run();
        });

        search.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        guestsFilter.setOnAction(e -> applyFilter.run());
        minPrice.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        maxPrice.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        searchBtn.setOnAction(e -> applyFilter.run());

        // Table
        TableView<RoomType> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setItems(filteredData);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có loại phòng nào trong hệ thống."));

        TableColumn<RoomType, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(75);
        idCol.setPrefWidth(85);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().id)));
        idCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER_LEFT; -fx-font-size: 13.5px; -fx-text-fill: #FFFFFF;");

        TableColumn<RoomType, String> nameCol = new TableColumn<>("Tên loại phòng");
        nameCol.setMinWidth(220);
        nameCol.setPrefWidth(240);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().name != null ? d.getValue().name : "—"));
        nameCol.setStyle("-fx-font-weight: 800;");

        TableColumn<RoomType, String> priceCol = new TableColumn<>("Giá cơ bản (VNĐ)");
        priceCol.setMinWidth(170);
        priceCol.setPrefWidth(180);
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().basePrice)));
        priceCol.setStyle("-fx-font-weight: 800; -fx-text-fill: #10B981; -fx-font-size: 14px;");

        TableColumn<RoomType, String> guestsCol = new TableColumn<>("Số người tối đa");
        guestsCol.setMinWidth(135);
        guestsCol.setPrefWidth(145);
        guestsCol.setCellValueFactory(d -> new SimpleStringProperty(
                (d.getValue().maxGuests != null ? d.getValue().maxGuests : 2) + " người"));
        guestsCol.setStyle("-fx-alignment: CENTER_LEFT; -fx-font-weight: 600;");

        TableColumn<RoomType, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setMinWidth(135);
        statusCol.setPrefWidth(145);
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().active ? "Đang áp dụng" : "Tạm ngưng"));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    boolean act = "Đang áp dụng".equalsIgnoreCase(item);
                    Label badge = new Label(item);
                    badge.getStyleClass().addAll("badge", act ? "badge-trong" : "badge-bao_tri");
                    badge.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 12;");
                    setGraphic(badge);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        // Column Thao tác: Sửa, Xóa (Wireframe Image 3)
        TableColumn<RoomType, Void> actionCol = new TableColumn<>("Thao tác");
        actionCol.setMinWidth(180);
        actionCol.setPrefWidth(190);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Sửa");
            private final Button delBtn = new Button("Xóa");
            private final HBox box = new HBox(8, editBtn, delBtn);

            {
                editBtn.getStyleClass().add("btn-action-edit");
                editBtn.setGraphic(IconUtils.icon("edit", 12, Color.WHITE));
                editBtn.setGraphicTextGap(6);
                editBtn.setMinWidth(68);
                editBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");

                delBtn.getStyleClass().add("btn-danger");
                delBtn.setGraphic(IconUtils.icon("trash", 12, Color.WHITE));
                delBtn.setGraphicTextGap(6);
                delBtn.setMinWidth(68);
                delBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");

                box.setAlignment(Pos.CENTER_LEFT);
                setAlignment(Pos.CENTER_LEFT);

                editBtn.setOnAction(e -> {
                    RoomType rt = getTableView().getItems().get(getIndex());
                    openRoomTypeDialog(rt, () -> reload(masterData, applyFilter));
                });

                delBtn.setOnAction(e -> {
                    RoomType rt = getTableView().getItems().get(getIndex());
                    confirmDelete(rt, () -> reload(masterData, applyFilter));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // Bỏ cột 'Mã LP' theo yêu cầu, chỉ giữ ID
        table.getColumns().addAll(idCol, nameCol, priceCol, guestsCol, statusCol, actionCol);

        // Reload logic
        Runnable loadAction = () -> reload(masterData, applyFilter);
        loadAction.run();

        refreshBtn.setOnAction(e -> {
            search.clear();
            guestsFilter.setValue("Tất cả số khách");
            minPrice.clear();
            maxPrice.clear();
            priceSlider.setValue(3000000);
            sliderLabel.setText("Trần giá: Tất cả");
            loadAction.run();
        });

        addBtn.setOnAction(e -> openRoomTypeDialog(null, loadAction));

        card.getChildren().addAll(headerRow, toolbar, table);
        root.setCenter(card);
        return root;
    }

    private static void reload(ObservableList<RoomType> masterData, Runnable applyFilter) {
        Util.runAsync(ApiService::roomTypes, list -> {
            masterData.setAll(list);
            applyFilter.run();
        });
    }

    private static void confirmDelete(RoomType rt, Runnable reload) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa loại phòng");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa loại phòng " + rt.name + " (ID: #" + rt.id + ")?");
        alert.setContentText("Lưu ý: Không thể xóa nếu loại phòng này đang được gán cho các phòng trong khách sạn.");
        alert.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            Util.runAsyncVoid(() -> ApiService.deleteRoomType(rt.id), reload);
        });
    }

    private static void openRoomTypeDialog(RoomType existing, Runnable reload) {
        boolean isEdit = existing != null;
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Cập nhật loại phòng" : "Thêm mới loại phòng");
        dialog.getDialogPane().getStylesheets().add(
                RoomTypesView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        TextField name = new TextField(isEdit ? existing.name : "");
        name.setPromptText("Tên loại phòng (VD: Phòng Deluxe Suite, tối đa 50 ký tự)");

        TextField price = new TextField(isEdit && existing.basePrice != null ? existing.basePrice.toPlainString() : "500000");
        price.setPromptText("Giá cơ bản theo ngày (VNĐ)");

        TextField maxGuests = new TextField(isEdit && existing.maxGuests != null ? String.valueOf(existing.maxGuests) : "2");
        maxGuests.setPromptText("Số người tối đa (VD: 2, 4)");

        VBox form = new VBox(10,
                labeledBox("Tên loại phòng (*)", name),
                labeledBox("Giá cơ bản (VNĐ) (*)", price),
                labeledBox("Số người tối đa (*)", maxGuests)
        );
        form.setPadding(new Insets(16));
        form.setPrefWidth(420);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String nameVal = name.getText().trim();
                if (nameVal.isBlank()) {
                    Util.error("Tên loại phòng không được để trống.");
                    return null;
                }
                BigDecimal priceVal;
                try {
                    priceVal = new BigDecimal(price.getText().trim());
                    if (priceVal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException();
                } catch (Exception ex) {
                    Util.error("Giá cơ bản phải là số dương hợp lệ.");
                    return null;
                }
                int maxG;
                try {
                    maxG = Integer.parseInt(maxGuests.getText().trim());
                    if (maxG <= 0) throw new IllegalArgumentException();
                } catch (Exception ex) {
                    Util.error("Số người tối đa phải là số nguyên lớn hơn 0.");
                    return null;
                }

                String codeVal = isEdit ? existing.code : null;
                final BigDecimal finalPrice = priceVal;
                final Integer finalMaxGuests = maxG;

                if (isEdit) {
                    Util.runAsync(() -> ApiService.updateRoomType(existing.id, codeVal, nameVal, finalPrice, finalMaxGuests),
                            rt -> reload.run());
                } else {
                    Util.runAsync(() -> ApiService.createRoomType(codeVal, nameVal, finalPrice, finalMaxGuests),
                            rt -> reload.run());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static VBox labeledBox(String label, javafx.scene.Node field) {
        Label l = new Label(label);
        l.getStyleClass().add("form-label");
        return new VBox(4, l, field);
    }
}
