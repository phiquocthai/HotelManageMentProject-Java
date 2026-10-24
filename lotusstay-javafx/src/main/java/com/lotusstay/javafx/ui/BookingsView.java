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
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;

public class BookingsView {

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

        Label title = new Label("Quản lý đặt phòng & Lưu trú");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 700;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        headerRow.getChildren().addAll(title, topSpacer);

        // Data collection
        ObservableList<Booking> masterData = FXCollections.observableArrayList();
        FilteredList<Booking> filteredData = new FilteredList<>(masterData, p -> true);

        // Top Toolbar
        TextField search = new TextField();
        search.setPromptText("Tìm theo mã ĐP, tên khách, số phòng...");
        search.setPrefWidth(260);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Tất cả trạng thái", "Đã đặt trước", "Đang lưu trú", "Đã trả phòng", "Đã hủy");
        statusFilter.setValue("Tất cả trạng thái");
        statusFilter.setPrefWidth(160);

        Button searchBtn = new Button("🔍 Tìm kiếm");
        searchBtn.getStyleClass().add("btn-outline");

        Button refreshBtn = new Button("🔄 Làm mới");
        refreshBtn.getStyleClass().add("btn-outline");

        Button addBtn = new Button("+ Đặt phòng mới");
        addBtn.getStyleClass().add("btn-gold");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, search, statusFilter, searchBtn, refreshBtn, spacer, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Filter predicate
        Runnable applyFilter = () -> {
            String q = search.getText() == null ? "" : search.getText().trim().toLowerCase();
            String st = statusFilter.getValue();

            filteredData.setPredicate(b -> {
                boolean matchSearch = q.isEmpty()
                        || (b.code != null && b.code.toLowerCase().contains(q))
                        || (b.customer != null && b.customer.fullName != null && b.customer.fullName.toLowerCase().contains(q))
                        || (b.customer != null && b.customer.phone != null && b.customer.phone.contains(q))
                        || (b.room != null && b.room.number != null && b.room.number.contains(q));

                boolean matchStatus = st == null || "Tất cả trạng thái".equals(st)
                        || b.statusLabel().equalsIgnoreCase(st);

                return matchSearch && matchStatus;
            });
        };

        search.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        searchBtn.setOnAction(e -> applyFilter.run());

        // Table
        TableView<Booking> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setItems(filteredData);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có lượt đặt phòng nào trong hệ thống."));

        TableColumn<Booking, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(75);
        idCol.setPrefWidth(85);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().id)));
        idCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER; -fx-font-size: 13.5px; -fx-text-fill: #FFFFFF;");

        TableColumn<Booking, String> codeCol = new TableColumn<>("Mã ĐP");
        codeCol.setMinWidth(115);
        codeCol.setPrefWidth(125);
        codeCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().code != null ? d.getValue().code : String.format("DP%03d", d.getValue().id)));
        codeCol.setStyle("-fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 13.5px;");

        TableColumn<Booking, String> nameCol = new TableColumn<>("Khách hàng");
        nameCol.setMinWidth(190);
        nameCol.setPrefWidth(220);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().customer != null ? d.getValue().customer.fullName + " (" + (d.getValue().customer.phone != null ? d.getValue().customer.phone : "") + ")" : "—"));
        nameCol.setStyle("-fx-font-weight: 800;");

        TableColumn<Booking, String> roomCol = new TableColumn<>("Phòng");
        roomCol.setMinWidth(135);
        roomCol.setPrefWidth(155);
        roomCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().room != null ? "P." + d.getValue().room.number + (d.getValue().room.roomType != null ? " (" + d.getValue().room.roomType.name + ")" : "") : "—"));
        roomCol.setStyle("-fx-font-weight: 700;");

        // Cột Số khách (Entity field: so_nguoi_o / numberOfGuests)
        TableColumn<Booking, String> guestsCol = new TableColumn<>("Số khách");
        guestsCol.setMinWidth(95);
        guestsCol.setPrefWidth(105);
        guestsCol.setCellValueFactory(d -> new SimpleStringProperty(
                (d.getValue().numberOfGuests != null ? d.getValue().numberOfGuests : 1) + " người"));
        guestsCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: 600;");

        TableColumn<Booking, String> inCol = new TableColumn<>("Ngày nhận");
        inCol.setMinWidth(120);
        inCol.setPrefWidth(130);
        inCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().checkIn != null ? d.getValue().checkIn : "—"));

        TableColumn<Booking, String> outCol = new TableColumn<>("Dự kiến trả");
        outCol.setMinWidth(120);
        outCol.setPrefWidth(130);
        outCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().checkOutPlanned != null ? d.getValue().checkOutPlanned : "—"));

        TableColumn<Booking, String> actInCol = new TableColumn<>("Nhận thực tế");
        actInCol.setMinWidth(130);
        actInCol.setPrefWidth(140);
        actInCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().actualCheckIn != null ? d.getValue().actualCheckIn : "—"));

        TableColumn<Booking, String> actOutCol = new TableColumn<>("Trả thực tế");
        actOutCol.setMinWidth(130);
        actOutCol.setPrefWidth(140);
        actOutCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().actualCheckOut != null ? d.getValue().actualCheckOut : "—"));

        TableColumn<Booking, String> statusCol = new TableColumn<>("Trạng thái");
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
                    badge.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 12;");
                    setGraphic(badge);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Booking, String> noteCol = new TableColumn<>("Ghi chú");
        noteCol.setPrefWidth(140);
        noteCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().notes != null ? d.getValue().notes : "—"));

        // Column Thao tác
        TableColumn<Booking, Void> actionCol = new TableColumn<>("Thao tác");
        actionCol.setMinWidth(210);
        actionCol.setPrefWidth(220);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button checkInBtn = new Button("Nhận phòng");
            private final Button cancelBtn = new Button("Hủy đặt");
            private final HBox box = new HBox(8, checkInBtn, cancelBtn);

            {
                checkInBtn.getStyleClass().add("btn-gold");
                checkInBtn.setGraphic(IconUtils.icon("check", 12, Color.web("#0A0D14")));
                checkInBtn.setGraphicTextGap(6);
                checkInBtn.setMinWidth(95);
                checkInBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 700; -fx-min-width: 95px; -fx-background-color: #F59E0B; -fx-text-fill: #0A0D14;");

                cancelBtn.getStyleClass().add("btn-danger");
                cancelBtn.setGraphic(IconUtils.icon("cancel", 12, Color.web("#F87171")));
                cancelBtn.setGraphicTextGap(6);
                cancelBtn.setMinWidth(75);
                cancelBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 75px;");

                box.setAlignment(Pos.CENTER);

                checkInBtn.setOnAction(e -> {
                    Booking b = getTableView().getItems().get(getIndex());
                    Util.runAsync(() -> ApiService.checkIn(b.id), res -> reload(masterData, applyFilter));
                });

                cancelBtn.setOnAction(e -> {
                    Booking b = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận hủy đặt phòng");
                    alert.setHeaderText("Hủy đặt phòng " + b.code + "?");
                    alert.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt -> {
                        Util.runAsync(() -> ApiService.cancelBooking(b.id), res -> reload(masterData, applyFilter));
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking b = getTableView().getItems().get(getIndex());
                    boolean canCheckIn = "DAT_TRUOC".equalsIgnoreCase(b.status) || "CHO_XAC_NHAN".equalsIgnoreCase(b.status);
                    boolean canCancel = canCheckIn || "DANG_O".equalsIgnoreCase(b.status);
                    checkInBtn.setDisable(!canCheckIn);
                    cancelBtn.setDisable(!canCancel);
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(idCol, codeCol, nameCol, roomCol, guestsCol, inCol, outCol, actInCol, actOutCol, statusCol, noteCol, actionCol);

        // Reload logic
        Runnable loadAction = () -> reload(masterData, applyFilter);
        loadAction.run();

        refreshBtn.setOnAction(e -> {
            search.clear();
            statusFilter.setValue("Tất cả trạng thái");
            loadAction.run();
        });

        addBtn.setOnAction(e -> openAddDialog(loadAction));

        card.getChildren().addAll(headerRow, toolbar, table);
        root.setCenter(card);
        return root;
    }

    private static void reload(ObservableList<Booking> masterData, Runnable applyFilter) {
        Util.runAsync(ApiService::bookings, list -> {
            masterData.setAll(list);
            applyFilter.run();
        });
    }

    private static void openAddDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Đặt phòng mới");
        dialog.getDialogPane().getStylesheets().add(
                BookingsView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        ComboBox<Customer> customerBox = new ComboBox<>();
        customerBox.setPromptText("— Chọn khách hàng có sẵn —");
        customerBox.setMaxWidth(Double.MAX_VALUE);
        Util.runAsync(() -> ApiService.customers(null), customerBox.getItems()::setAll);

        TextField newName = new TextField();
        newName.setPromptText("Hoặc nhập tên khách hàng mới");
        TextField newPhone = new TextField();
        newPhone.setPromptText("Số điện thoại khách mới");

        ComboBox<Room> roomBox = new ComboBox<>();
        roomBox.setPromptText("Chọn phòng (đang trống)");
        roomBox.setMaxWidth(Double.MAX_VALUE);
        Util.runAsync(ApiService::rooms, rooms -> rooms.stream()
                .filter(r -> "TRONG".equals(r.status))
                .forEach(roomBox.getItems()::add));

        DatePicker checkIn = new DatePicker(LocalDate.now());
        DatePicker checkOut = new DatePicker(LocalDate.now().plusDays(1));
        CheckBox checkInNow = new CheckBox("Nhận phòng ngay (khách đến nhận phòng trực tiếp)");
        checkInNow.setSelected(true);

        VBox form = new VBox(10,
                new Label("Khách hàng"), customerBox, newName, newPhone,
                new Label("Phòng"), roomBox,
                new HBox(10, labeledBox("Ngày nhận", checkIn), labeledBox("Dự kiến trả", checkOut)),
                checkInNow);
        form.setPadding(new Insets(16));
        form.setPrefWidth(420);
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                if (roomBox.getValue() == null) {
                    Util.error("Vui lòng chọn phòng trống.");
                    return null;
                }
                Long customerId = customerBox.getValue() != null ? customerBox.getValue().id : null;
                Util.runAsync(() -> ApiService.createBooking(
                        customerId, newName.getText(), newPhone.getText(),
                        roomBox.getValue().id, checkIn.getValue().toString(), checkOut.getValue().toString(),
                        checkInNow.isSelected()
                ), b -> reload.run());
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
