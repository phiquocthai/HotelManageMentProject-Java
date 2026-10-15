package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Booking;
import com.lotusstay.javafx.model.Customer;
import com.lotusstay.javafx.model.Room;
import com.lotusstay.javafx.net.ApiService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RoomsView {

    public static ScrollPane build(MainShell shell) {
        VBox root = new VBox(14);
        root.setPadding(new Insets(24));

        HBox legend = new HBox(10);
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.getChildren().addAll(
                legendBadge("● Trống (Sẵn sàng)", "badge-trong"),
                legendBadge("● Đang ở", "badge-dang_o"),
                legendBadge("● Đang dọn", "badge-dang_don"),
                legendBadge("● Bảo trì", "badge-bao_tri")
        );

        FlowPane grid = new FlowPane(16, 16);
        root.getChildren().addAll(legend, grid);

        Label loading = new Label("Đang tải danh sách phòng…");
        loading.getStyleClass().add("hint-text");
        grid.getChildren().add(loading);

        Util.runAsync(ApiService::rooms, rooms -> {
            grid.getChildren().clear();
            for (Room r : rooms) {
                grid.getChildren().add(buildRoomCard(r, shell));
            }
            if (rooms.isEmpty()) {
                Label empty = new Label("Chưa có phòng nào — vào mục \"Loại phòng\" để thêm loại phòng trước, sau đó quay lại đây thêm phòng.");
                empty.getStyleClass().add("hint-text");
                grid.getChildren().add(empty);
            }
        });

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    private static VBox buildRoomCard(Room r, MainShell shell) {
        VBox card = new VBox(6);
        card.getStyleClass().addAll("room-card", "status-" + r.status.toLowerCase());
        card.setPrefWidth(170);

        Label number = new Label(r.number);
        number.getStyleClass().add("room-number");
        Label type = new Label(r.roomType != null ? r.roomType.name : "—");
        type.getStyleClass().add("room-sub");
        Label badge = new Label(r.statusLabel());
        badge.getStyleClass().addAll("badge", "badge-" + r.status.toLowerCase());

        card.getChildren().addAll(number, type, badge);
        card.setOnMouseClicked(e -> openRoomDialog(r, shell));
        return card;
    }

    private static void openRoomDialog(Room r, MainShell shell) {
        switch (r.status) {
            case "TRONG" -> openCheckInDialog(r, shell);
            case "DANG_O" -> openCheckOutDialog(r, shell);
            case "DANG_DON" -> confirmAndSetStatus(r, "TRONG", "Xác nhận đã dọn xong phòng " + r.number + "?", shell);
            case "BAO_TRI" -> confirmAndSetStatus(r, "TRONG", "Xác nhận hoàn tất bảo trì phòng " + r.number + "?", shell);
            default -> {}
        }
    }

    private static void confirmAndSetStatus(Room r, String newStatus, String question, MainShell shell) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, question);
        confirm.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt ->
                Util.runAsync(() -> ApiService.updateRoomStatus(r.id, newStatus), room -> shell.goTo("rooms")));
    }

    // ---------- NHẬN PHÒNG ----------
    private static void openCheckInDialog(Room r, MainShell shell) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nhận phòng — " + r.number);
        dialog.getDialogPane().getStylesheets().add(
                RoomsView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        ComboBox<Customer> customerBox = new ComboBox<>();
        customerBox.setPromptText("— Chọn khách hàng có sẵn (hoặc bỏ trống để nhập khách mới) —");
        customerBox.setMaxWidth(Double.MAX_VALUE);
        Util.runAsync(() -> ApiService.customers(null), customerBox.getItems()::setAll);

        TextField newName = new TextField();
        newName.setPromptText("Tên khách mới (nếu không chọn ở trên)");
        TextField newPhone = new TextField();
        newPhone.setPromptText("Số điện thoại");

        DatePicker checkIn = new DatePicker(LocalDate.now());
        DatePicker checkOut = new DatePicker(LocalDate.now().plusDays(1));

        VBox form = new VBox(10,
                new Label("Khách hàng"), customerBox,
                new Label("Hoặc nhập khách mới"), newName, newPhone,
                new HBox(10, labeledBox("Ngày nhận", checkIn), labeledBox("Dự kiến trả", checkOut))
        );
        form.setPadding(new Insets(16));
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Long customerId = customerBox.getValue() != null ? customerBox.getValue().id : null;
                Util.runAsync(() -> ApiService.createBooking(
                        customerId, newName.getText(), newPhone.getText(),
                        r.id, checkIn.getValue().toString(), checkOut.getValue().toString(), true
                ), b -> shell.goTo("rooms"));
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ---------- TRẢ PHÒNG ----------
    private static void openCheckOutDialog(Room r, MainShell shell) {
        Util.runAsync(ApiService::bookings, bookings -> {
            Optional<Booking> active = bookings.stream()
                    .filter(b -> "DANG_O".equals(b.status) && b.room != null && b.room.id.equals(r.id))
                    .findFirst();

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Trả phòng — " + r.number);
            dialog.getDialogPane().getStylesheets().add(
                    RoomsView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

            Label guestInfo = new Label(active.map(b -> "Khách: " + b.customer.fullName).orElse("Không tìm thấy lượt lưu trú đang mở — sẽ lập hóa đơn thủ công."));
            guestInfo.getStyleClass().add("hint-text");

            TextField roomFee = new TextField(r.roomType != null ? r.roomType.basePrice.toPlainString() : "0");
            TextField penaltyFee = new TextField("0");
            DatePicker checkOutDate = new DatePicker(LocalDate.now());

            VBox form = new VBox(10, guestInfo,
                    labeledBox("Tiền phòng", roomFee),
                    labeledBox("Tiền phạt (nếu có)", penaltyFee),
                    labeledBox("Ngày trả phòng", checkOutDate));
            form.setPadding(new Insets(16));
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
                    ), inv -> shell.goTo("rooms"));
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
        VBox box = new VBox(4, l, field);
        return box;
    }

    private static Label legendBadge(String text, String badgeClass) {
        Label l = new Label(text);
        l.getStyleClass().addAll("badge", badgeClass);
        return l;
    }
}
