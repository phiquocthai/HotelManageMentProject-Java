package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Booking;
import com.lotusstay.javafx.model.Customer;
import com.lotusstay.javafx.model.Room;
import com.lotusstay.javafx.net.ApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class BookingsView {

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));

        TableView<Booking> table = new TableView<>();
        TableColumn<Booking, String> nameCol = new TableColumn<>("Khách hàng");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().customer != null ? d.getValue().customer.fullName : "—"));
        TableColumn<Booking, String> roomCol = new TableColumn<>("Phòng");
        roomCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().room != null ? d.getValue().room.number : "—"));
        TableColumn<Booking, String> inCol = new TableColumn<>("Nhận phòng");
        inCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().checkIn));
        TableColumn<Booking, String> outCol = new TableColumn<>("Trả dự kiến");
        outCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().checkOutPlanned));
        TableColumn<Booking, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().statusLabel()));
        table.getColumns().addAll(nameCol, roomCol, inCol, outCol, statusCol);
        table.setPlaceholder(new Label("Chưa có đặt phòng nào."));

        Runnable reload = () -> Util.runAsync(ApiService::bookings, table.getItems()::setAll);
        reload.run();

        HBox actionRow = new HBox(10);
        Button checkInBtn = new Button("Nhận phòng");
        checkInBtn.getStyleClass().add("btn-outline");
        checkInBtn.setOnAction(e -> {
            Booking sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && "DAT_TRUOC".equals(sel.status)) {
                Util.runAsync(() -> ApiService.checkIn(sel.id), b -> reload.run());
            } else {
                Util.info("Chọn một đặt phòng đang ở trạng thái \"Đã đặt trước\" để nhận phòng.");
            }
        });
        Button cancelBtn = new Button("Hủy đặt phòng");
        cancelBtn.getStyleClass().add("btn-danger");
        cancelBtn.setOnAction(e -> {
            Booking sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && "DAT_TRUOC".equals(sel.status)) {
                Util.runAsync(() -> ApiService.cancelBooking(sel.id), b -> reload.run());
            } else {
                Util.info("Chọn một đặt phòng đang ở trạng thái \"Đã đặt trước\" để hủy.");
            }
        });
        actionRow.getChildren().addAll(checkInBtn, cancelBtn);

        Button addBtn = new Button("+ Đặt phòng mới");
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setOnAction(e -> openAddDialog(reload));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox toolbar = new HBox(10, actionRow, spacer, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 12, 0));

        root.setTop(toolbar);
        root.setCenter(table);
        return root;
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
        newName.setPromptText("Hoặc nhập tên khách mới");
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
        CheckBox checkInNow = new CheckBox("Nhận phòng ngay (khách đến trực tiếp)");
        checkInNow.setSelected(true);

        VBox form = new VBox(10,
                new Label("Khách hàng"), customerBox, newName, newPhone,
                new Label("Phòng"), roomBox,
                new HBox(10, labeledBox("Ngày nhận", checkIn), labeledBox("Dự kiến trả", checkOut)),
                checkInNow);
        form.setPadding(new Insets(16));
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                if (roomBox.getValue() == null) { Util.error("Vui lòng chọn phòng."); return null; }
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
