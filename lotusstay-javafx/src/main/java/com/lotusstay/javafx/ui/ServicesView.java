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

        // Top Toolbar
        TextField search = new TextField();
        search.setPromptText("Tìm theo tên dịch vụ, mã dịch vụ...");
        search.setPrefWidth(320);

        Button searchBtn = new Button("🔍 Tìm kiếm");
        searchBtn.getStyleClass().add("btn-outline");

        Button refreshBtn = new Button("🔄 Làm mới");
        refreshBtn.getStyleClass().add("btn-outline");

        Button addBtn = new Button("+ Thêm dịch vụ");
        addBtn.getStyleClass().add("btn-gold");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, search, searchBtn, refreshBtn, spacer, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Filter predicate
        Runnable applyFilter = () -> {
            String q = search.getText() == null ? "" : search.getText().trim().toLowerCase();
            filteredData.setPredicate(s -> {
                if (q.isEmpty()) return true;
                boolean matchName = s.name != null && s.name.toLowerCase().contains(q);
                boolean matchCode = s.code != null && s.code.toLowerCase().contains(q);
                return matchName || matchCode;
            });
        };

        search.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        searchBtn.setOnAction(e -> applyFilter.run());

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
        idCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER; -fx-font-size: 13.5px; -fx-text-fill: #FFFFFF;");

        TableColumn<ServiceItem, String> codeCol = new TableColumn<>("Mã DV");
        codeCol.setMinWidth(115);
        codeCol.setPrefWidth(125);
        codeCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().code != null ? d.getValue().code : String.format("DV%03d", d.getValue().id)));
        codeCol.setStyle("-fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 13.5px;");

        TableColumn<ServiceItem, String> nameCol = new TableColumn<>("Tên dịch vụ");
        nameCol.setMinWidth(260);
        nameCol.setPrefWidth(280);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name));
        nameCol.setStyle("-fx-font-weight: 800;");

        TableColumn<ServiceItem, String> priceCol = new TableColumn<>("Đơn giá (VNĐ)");
        priceCol.setMinWidth(170);
        priceCol.setPrefWidth(180);
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().price) + " ₫"));
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
                    setAlignment(Pos.CENTER);
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
                    ServiceItem s = getTableView().getItems().get(getIndex());
                    openServiceDialog(s, () -> reload(masterData, applyFilter));
                });

                delBtn.setOnAction(e -> {
                    ServiceItem s = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận xóa dịch vụ");
                    alert.setHeaderText("Xóa dịch vụ " + s.name + " (" + s.code + ")?");
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

        table.getColumns().addAll(idCol, codeCol, nameCol, priceCol, statusCol, actionCol);

        // Reload logic
        Runnable loadAction = () -> reload(masterData, applyFilter);
        loadAction.run();

        refreshBtn.setOnAction(e -> {
            search.clear();
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

        TextField code = new TextField(isEdit ? existing.code : "");
        code.setPromptText("Tự động (VD: DV006)");
        if (isEdit) code.setDisable(true);

        TextField name = new TextField(isEdit ? existing.name : "");
        name.setPromptText("Tên dịch vụ (VD: Đưa đón sân bay, tối đa 100 ký tự)");

        TextField price = new TextField(isEdit && existing.price != null ? existing.price.toPlainString() : "50000");
        price.setPromptText("Đơn giá (VNĐ)");

        VBox form = new VBox(10,
                labeledBox("Mã dịch vụ", code),
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

                String codeVal = code.getText().trim();
                final BigDecimal finalPrice = priceVal;

                if (isEdit) {
                    Util.runAsync(() -> ApiService.updateService(existing.id, codeVal, nameVal, finalPrice),
                            s -> reload.run());
                } else {
                    Util.runAsync(() -> ApiService.createService(codeVal, nameVal, finalPrice),
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
