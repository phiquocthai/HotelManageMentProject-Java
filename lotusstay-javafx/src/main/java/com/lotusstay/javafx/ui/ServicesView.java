package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.ServiceItem;
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

public class ServicesView {

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

        Label title = new Label("Quản lý dịch vụ & Tiện ích");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 700;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        headerRow.getChildren().addAll(title, topSpacer);

        // Data collection
        ObservableList<ServiceItem> masterData = FXCollections.observableArrayList();
        FilteredList<ServiceItem> filteredData = new FilteredList<>(masterData, p -> true);

        // Top Toolbar với bộ lọc theo tên và khoảng giá
        TextField search = new TextField();
        search.setPromptText("🔍 Tìm theo tên dịch vụ...");
        search.setPrefWidth(280);
        search.setMinWidth(220);
        search.setPrefHeight(38);

        TextField minPrice = new TextField();
        minPrice.setPromptText("Giá từ (đ)");
        minPrice.setPrefWidth(145);
        minPrice.setMinWidth(135);
        minPrice.setPrefHeight(38);

        Label rangeDash = new Label("—");
        rangeDash.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: 700;");

        TextField maxPrice = new TextField();
        maxPrice.setPromptText("Giá đến (đ)");
        maxPrice.setPrefWidth(145);
        maxPrice.setMinWidth(135);
        maxPrice.setPrefHeight(38);

        // Thanh trượt mức giá trực quan cho dịch vụ
        Slider priceSlider = new Slider(0, 1000000, 1000000);
        priceSlider.setPrefWidth(160);
        priceSlider.setMinWidth(140);
        priceSlider.setBlockIncrement(20000);
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

        Button addBtn = new Button("Thêm dịch vụ");
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setGraphic(IconUtils.icon("plus", 14, Color.web("#0A0D14")));
        addBtn.setMinWidth(Region.USE_PREF_SIZE);
        addBtn.setPrefWidth(150);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(8, search, minPrice, rangeDash, maxPrice, sliderBox, searchBtn, refreshBtn, spacer, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Filter predicate: Tên + Khoảng giá
        Runnable applyFilter = () -> {
            String q = search.getText() == null ? "" : search.getText().trim().toLowerCase();

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

            filteredData.setPredicate(s -> {
                boolean matchName = q.isEmpty() || (s.name != null && s.name.toLowerCase().contains(q));

                boolean matchPrice = true;
                if (s.price != null) {
                    double p = s.price.doubleValue();
                    if (finalMin != null && p < finalMin) matchPrice = false;
                    if (finalMax != null && p > finalMax) matchPrice = false;
                }

                return matchName && matchPrice;
            });
        };

        search.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        minPrice.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        maxPrice.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        searchBtn.setOnAction(e -> applyFilter.run());

        priceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            long rounded = Math.round(newVal.doubleValue() / 10000.0) * 10000;
            if (rounded >= 1000000) {
                sliderLabel.setText("Trần giá: Tất cả");
                maxPrice.clear();
            } else {
                sliderLabel.setText("Trần giá: " + String.format("%,d đ", rounded));
                maxPrice.setText(String.valueOf(rounded));
            }
            applyFilter.run();
        });

        // Table
        TableView<ServiceItem> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setItems(filteredData);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có dịch vụ nào trong hệ thống."));

        TableColumn<ServiceItem, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(75);
        idCol.setPrefWidth(85);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().id)));
        idCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER_LEFT; -fx-font-size: 13.5px; -fx-text-fill: #FFFFFF;");

        TableColumn<ServiceItem, String> nameCol = new TableColumn<>("Tên dịch vụ");
        nameCol.setMinWidth(260);
        nameCol.setPrefWidth(280);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name));
        nameCol.setStyle("-fx-font-weight: 800;");

        TableColumn<ServiceItem, String> priceCol = new TableColumn<>("Đơn giá (VNĐ)");
        priceCol.setMinWidth(170);
        priceCol.setPrefWidth(180);
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().price)));
        priceCol.setStyle("-fx-font-weight: 800; -fx-text-fill: #10B981; -fx-font-size: 14px;");

        TableColumn<ServiceItem, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setMinWidth(135);
        statusCol.setPrefWidth(145);
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().active ? "Đang phục vụ" : "Tạm ngưng"));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    boolean act = "Đang phục vụ".equalsIgnoreCase(item);
                    Label badge = new Label(item);
                    badge.getStyleClass().addAll("badge", act ? "badge-trong" : "badge-bao_tri");
                    badge.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 12;");
                    setGraphic(badge);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        // Column Thao tác: Sửa, Xóa
        TableColumn<ServiceItem, Void> actionCol = new TableColumn<>("Thao tác");
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
                    ServiceItem s = getTableView().getItems().get(getIndex());
                    openServiceDialog(s, () -> reload(masterData, applyFilter));
                });

                delBtn.setOnAction(e -> {
                    ServiceItem s = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận xóa dịch vụ");
                    alert.setHeaderText("Xóa dịch vụ " + s.name + "?");
                    alert.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt -> {
                        Util.runAsyncVoid(() -> ApiService.deleteService(s.id), () -> reload(masterData, applyFilter));
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(idCol, nameCol, priceCol, statusCol, actionCol);

        // Reload logic
        Runnable loadAction = () -> reload(masterData, applyFilter);
        loadAction.run();

        refreshBtn.setOnAction(e -> {
            search.clear();
            minPrice.clear();
            maxPrice.clear();
            priceSlider.setValue(1000000);
            sliderLabel.setText("Trần giá: Tất cả");
            loadAction.run();
        });

        addBtn.setOnAction(e -> openServiceDialog(null, loadAction));

        card.getChildren().addAll(headerRow, toolbar, table);
        root.setCenter(card);
        return root;
    }

    private static void reload(ObservableList<ServiceItem> masterData, Runnable applyFilter) {
        Util.runAsync(ApiService::services, list -> {
            masterData.setAll(list);
            applyFilter.run();
        });
    }

    private static void openServiceDialog(ServiceItem existing, Runnable reload) {
        boolean isEdit = existing != null;
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Cập nhật dịch vụ" : "Thêm mới dịch vụ");
        dialog.getDialogPane().getStylesheets().add(
                ServicesView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        TextField name = new TextField(isEdit ? existing.name : "");
        name.setPromptText("Tên dịch vụ (VD: Đưa đón sân bay, tối đa 100 ký tự)");

        TextField price = new TextField(isEdit && existing.price != null ? existing.price.toPlainString() : "50000");
        price.setPromptText("Đơn giá (VNĐ)");

        VBox form = new VBox(10,
                labeledBox("Tên dịch vụ (*)", name),
                labeledBox("Đơn giá (don_gia) (*)", price)
        );
        form.setPadding(new Insets(16));
        form.setPrefWidth(420);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String nameVal = name.getText().trim();
                if (nameVal.isBlank()) {
                    Util.error("Tên dịch vụ không được để trống.");
                    return null;
                }
                BigDecimal priceVal;
                try {
                    priceVal = new BigDecimal(price.getText().trim());
                    if (priceVal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException();
                } catch (Exception ex) {
                    Util.error("Đơn giá phải là số dương hợp lệ.");
                    return null;
                }

                final BigDecimal finalPrice = priceVal;

                if (isEdit) {
                    Util.runAsync(() -> ApiService.updateService(existing.id, null, nameVal, finalPrice),
                            s -> reload.run());
                } else {
                    Util.runAsync(() -> ApiService.createService(nameVal, finalPrice),
                            s -> reload.run());
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
