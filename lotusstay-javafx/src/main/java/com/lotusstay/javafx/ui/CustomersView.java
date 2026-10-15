package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Customer;
import com.lotusstay.javafx.net.ApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CustomersView {

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));

        TableView<Customer> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<Customer, String> nameCol = new TableColumn<>("Họ và tên");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fullName));
        
        TableColumn<Customer, String> cccdCol = new TableColumn<>("CCCD/Hộ chiếu");
        cccdCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().idNumber != null ? d.getValue().idNumber : "—"));
        
        TableColumn<Customer, String> phoneCol = new TableColumn<>("Số điện thoại");
        phoneCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().phone != null ? d.getValue().phone : "—"));
        
        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().email != null ? d.getValue().email : "—"));
        
        table.getColumns().addAll(nameCol, cccdCol, phoneCol, emailCol);
        table.setPlaceholder(new Label("Chưa có khách hàng nào."));

        TextField search = new TextField();
        search.setPromptText("Tìm theo tên, email, CCCD, sđt…");
        search.setPrefWidth(280);

        Runnable reload = () -> Util.runAsync(() -> ApiService.customers(search.getText()), table.getItems()::setAll);
        reload.run();
        search.setOnAction(e -> reload.run());

        Button addBtn = new Button("+ Thêm khách hàng");
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setOnAction(e -> openAddDialog(reload));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox toolbar = new HBox(10, search, spacer, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 16, 0));

        root.setTop(toolbar);
        root.setCenter(table);
        return root;
    }

    private static void openAddDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Thêm khách hàng");
        dialog.getDialogPane().getStylesheets().add(
                CustomersView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        TextField name = new TextField();
        name.setPromptText("Họ và tên");
        
        TextField idNumber = new TextField();
        idNumber.setPromptText("Căn cước công dân / Hộ chiếu");
        
        TextField email = new TextField();
        email.setPromptText("Email");
        
        TextField phone = new TextField();
        phone.setPromptText("Số điện thoại");

        VBox form = new VBox(10, 
                labeledBox("Họ và tên", name),
                labeledBox("Số CCCD", idNumber),
                labeledBox("Số điện thoại", phone),
                labeledBox("Email", email)
        );
        form.setPadding(new Insets(16));
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK && !name.getText().isBlank()) {
                Util.runAsync(() -> ApiService.createCustomer(
                        name.getText().trim(), phone.getText().trim(), email.getText().trim(), idNumber.getText().trim()
                ), c -> reload.run());
            } else if (bt == ButtonType.OK) {
                Util.error("Tên khách hàng không được để trống.");
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
