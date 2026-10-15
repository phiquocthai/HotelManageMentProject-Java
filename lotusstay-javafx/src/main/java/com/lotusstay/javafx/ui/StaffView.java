package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Staff;
import com.lotusstay.javafx.model.UserAccount;
import com.lotusstay.javafx.net.ApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class StaffView {

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));

        TableView<Staff> table = new TableView<>();
        TableColumn<Staff, String> nameCol = new TableColumn<>("Họ tên");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fullName));
        TableColumn<Staff, String> jobCol = new TableColumn<>("Chức danh");
        jobCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().jobTitle));
        TableColumn<Staff, String> shiftCol = new TableColumn<>("Ca làm việc");
        shiftCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().shift));
        TableColumn<Staff, String> accCol = new TableColumn<>("Tài khoản đăng nhập");
        accCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().user != null ? "@" + d.getValue().user.username : "Chưa cấp tài khoản"));
        TableColumn<Staff, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(
                "ACTIVE".equals(d.getValue().status) ? "Đang làm" : "Nghỉ"));
        table.getColumns().addAll(nameCol, jobCol, shiftCol, accCol, statusCol);
        table.setPlaceholder(new Label("Chưa có nhân viên nào."));

        Runnable reload = () -> Util.runAsync(ApiService::staff, table.getItems()::setAll);
        reload.run();

        Button deleteBtn = new Button("Xóa nhân viên đã chọn");
        deleteBtn.getStyleClass().add("btn-danger");
        deleteBtn.setOnAction(e -> {
            Staff sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) Util.runAsyncVoid(() -> ApiService.deleteStaff(sel.id), reload);
        });

        Button addBtn = new Button("+ Thêm nhân viên");
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setOnAction(e -> openAddDialog(reload));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox toolbar = new HBox(10, deleteBtn, spacer, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 12, 0));

        root.setTop(toolbar);
        root.setCenter(table);
        return root;
    }

    private static void openAddDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Thêm nhân viên");
        dialog.getDialogPane().getStylesheets().add(
                StaffView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        TextField name = new TextField();
        name.setPromptText("Họ và tên");
        TextField jobTitle = new TextField();
        jobTitle.setPromptText("Chức danh (VD: Lễ tân, Buồng phòng, Bảo vệ…)");
        TextField shift = new TextField();
        shift.setPromptText("Ca làm việc (VD: Ca sáng 6h–14h)");
        TextField phone = new TextField();
        phone.setPromptText("Số điện thoại");

        ComboBox<UserAccount> userBox = new ComboBox<>();
        userBox.setPromptText("— Không gắn tài khoản đăng nhập —");
        userBox.setMaxWidth(Double.MAX_VALUE);
        Util.runAsync(ApiService::users, userBox.getItems()::setAll);

        VBox form = new VBox(10,
                labeledBox("Họ và tên", name),
                labeledBox("Chức danh", jobTitle),
                labeledBox("Ca làm việc", shift),
                labeledBox("Số điện thoại", phone),
                labeledBox("Tài khoản đăng nhập liên kết (nếu có)", userBox)
        );
        form.setPadding(new Insets(16));
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK && !name.getText().isBlank() && !jobTitle.getText().isBlank()) {
                Long userId = userBox.getValue() != null ? userBox.getValue().id : null;
                Util.runAsync(() -> ApiService.createStaff(
                        name.getText().trim(), jobTitle.getText().trim(), shift.getText().trim(),
                        phone.getText().trim(), userId
                ), s -> reload.run());
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
