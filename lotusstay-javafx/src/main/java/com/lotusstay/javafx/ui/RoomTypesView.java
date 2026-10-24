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

        // Top Toolbar (Matching Wireframe Image 3)
        TextField search = new TextField();
        search.setPromptText("Tìm theo tên loại phòng, mã loại phòng...");
        search.setPrefWidth(320);

        Button searchBtn = new Button("🔍 Tìm kiếm");
        searchBtn.getStyleClass().add("btn-outline");

        Button refreshBtn = new Button("🔄 Làm mới");
        refreshBtn.getStyleClass().add("btn-outline");

        Button deleteSelectedBtn = new Button("🗑️ Xóa mục đã chọn");
        deleteSelectedBtn.getStyleClass().add("btn-danger");

        Button addBtn = new Button("+ Thêm loại phòng");
        addBtn.getStyleClass().add("btn-gold");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, search, searchBtn, refreshBtn, spacer, deleteSelectedBtn, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Filter
        Runnable applyFilter = () -> {
            String q = search.getText() == null ? "" : search.getText().trim().toLowerCase();
            filteredData.setPredicate(rt -> {
                if (q.isEmpty()) return true;
                boolean matchName = rt.name != null && rt.name.toLowerCase().contains(q);
                boolean matchCode = rt.code != null && rt.code.toLowerCase().contains(q);
                return matchName || matchCode;
            });
        };

        search.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
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
        idCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER; -fx-font-size: 13.5px; -fx-text-fill: #FFFFFF;");

        TableColumn<RoomType, String> codeCol = new TableColumn<>("Mã LP");
        codeCol.setMinWidth(115);
        codeCol.setPrefWidth(125);
        codeCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().code != null ? d.getValue().code : String.format("LP%02d", d.getValue().id)));
        codeCol.setStyle("-fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 13.5px;");

        TableColumn<RoomType, String> nameCol = new TableColumn<>("Tên loại phòng");
        nameCol.setMinWidth(220);
        nameCol.setPrefWidth(240);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().name != null ? d.getValue().name : "—"));
        nameCol.setStyle("-fx-font-weight: 800;");

        TableColumn<RoomType, String> priceCol = new TableColumn<>("Giá cơ bản (VNĐ)");
        priceCol.setMinWidth(170);
        priceCol.setPrefWidth(180);
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().basePrice) + " ₫"));
        priceCol.setStyle("-fx-font-weight: 800; -fx-text-fill: #10B981; -fx-font-size: 14px;");

        TableColumn<RoomType, String> guestsCol = new TableColumn<>("Số người tối đa");
        guestsCol.setMinWidth(135);
        guestsCol.setPrefWidth(145);
        guestsCol.setCellValueFactory(d -> new SimpleStringProperty(
                (d.getValue().maxGuests != null ? d.getValue().maxGuests : 2) + " người"));
        guestsCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: 600;");

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
                    setAlignment(Pos.CENTER);
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

        table.getColumns().addAll(idCol, codeCol, nameCol, priceCol, guestsCol, statusCol, actionCol);

        // Reload logic
        Runnable loadAction = () -> reload(masterData, applyFilter);
        loadAction.run();

        refreshBtn.setOnAction(e -> {
            search.clear();
            loadAction.run();
        });

        addBtn.setOnAction(e -> openRoomTypeDialog(null, loadAction));

        deleteSelectedBtn.setOnAction(e -> {
            RoomType sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                Util.info("Vui lòng chọn loại phòng trong bảng để xóa.");
                return;
            }
            confirmDelete(sel, loadAction);
        });

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
        alert.setHeaderText("Bạn có chắc chắn muốn xóa loại phòng " + rt.name + " (" + rt.code + ")?");
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

        TextField code = new TextField(isEdit ? existing.code : "");
        code.setPromptText("Tự động (VD: LP05)");
        if (isEdit) code.setDisable(true);

        TextField name = new TextField(isEdit ? existing.name : "");
        name.setPromptText("Tên loại phòng (VD: Phòng Deluxe Suite, tối đa 50 ký tự)");

        TextField price = new TextField(isEdit && existing.basePrice != null ? existing.basePrice.toPlainString() : "500000");
        price.setPromptText("Giá cơ bản theo ngày (VNĐ)");

        TextField maxGuests = new TextField(isEdit && existing.maxGuests != null ? String.valueOf(existing.maxGuests) : "2");
        maxGuests.setPromptText("Số người tối đa (VD: 2, 4)");

        VBox form = new VBox(10,
                labeledBox("Mã loại phòng", code),
                labeledBox("Tên loại phòng (*)", name),
                labeledBox("Giá cơ bản (VNĐ) (*)", price),
                labeledBox("Số người tối đa (so_nguoi_toi_da) (*)", maxGuests)
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

                String codeVal = code.getText().trim();
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
