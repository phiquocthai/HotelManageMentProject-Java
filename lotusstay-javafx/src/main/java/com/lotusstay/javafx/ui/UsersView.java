package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.UserAccount;
import com.lotusstay.javafx.net.ApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class UsersView {

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));

        TableView<UserAccount> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<UserAccount, String> userCol = new TableColumn<>("Tên đăng nhập");
        userCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().username));

        TableColumn<UserAccount, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().email != null ? d.getValue().email : "—"));

        TableColumn<UserAccount, String> phoneCol = new TableColumn<>("SĐT");
        phoneCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().phone != null ? d.getValue().phone : "—"));

        TableColumn<UserAccount, String> roleCol = new TableColumn<>("Vai trò");
        roleCol.setCellValueFactory(d -> new SimpleStringProperty(roleLabel(d.getValue().role)));

        TableColumn<UserAccount, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(
                (d.getValue().status != null && d.getValue().status.equals("ACTIVE")) ? "Đang hoạt động" : "Đã khóa"));

        table.getColumns().addAll(userCol, emailCol, phoneCol, roleCol, statusCol);
        table.setPlaceholder(new Label("Chưa có tài khoản nào."));

        Runnable reload = () -> Util.runAsync(ApiService::users, table.getItems()::setAll);
        reload.run();

        Button toggleBtn = new Button("Khóa / Mở khóa");
        toggleBtn.getStyleClass().add("btn-outline");
        toggleBtn.setOnAction(e -> {
            UserAccount sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) Util.runAsync(() -> ApiService.toggleUserStatus(sel.id), u -> reload.run());
            else Util.info("Vui lòng chọn một tài khoản để khóa/mở khóa.");
        });

        Button addBtn = new Button("+ Thêm tài khoản");
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setOnAction(e -> openAddDialog(reload));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox toolbar = new HBox(10, toggleBtn, spacer, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 16, 0));

        root.setTop(toolbar);
        root.setCenter(table);
        return root;
    }

    private static String roleLabel(String role) {
        if (role == null) return "";
        return switch (role) {
            case "ADMIN" -> "Quản trị viên";
            case "MANAGER" -> "Quản lý";
            case "RECEPTION" -> "Lễ tân";
            default -> role;
        };
    }

    private static void openAddDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Thêm tài khoản hệ thống");
        dialog.getDialogPane().getStylesheets().add(
                UsersView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        TextField username = new TextField();
        username.setPromptText("Tên đăng nhập (ví dụ: admin)");
        PasswordField password = new PasswordField();
        password.setPromptText("Mật khẩu");
        TextField email = new TextField();
        email.setPromptText("Email");
        TextField phone = new TextField();
        phone.setPromptText("Số điện thoại");

        ComboBox<String> role = new ComboBox<>();
        role.getItems().addAll("ADMIN", "MANAGER", "RECEPTION");
        role.setValue("RECEPTION");
        role.setMaxWidth(Double.MAX_VALUE);

        VBox form = new VBox(10,
                labeledBox("Tên đăng nhập", username),
                labeledBox("Mật khẩu", password),
                labeledBox("Email", email),
                labeledBox("Số điện thoại", phone),
                labeledBox("Vai trò", role)
        );
        form.setPadding(new Insets(16));
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK && !username.getText().isBlank() && !email.getText().isBlank()) {
                Util.runAsync(() -> ApiService.createUser(
                        username.getText().trim(), password.getText(), email.getText().trim(), phone.getText().trim(), role.getValue()
                ), u -> reload.run());
            } else if (bt == ButtonType.OK) {
                Util.error("Tên đăng nhập và Email không được để trống.");
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
