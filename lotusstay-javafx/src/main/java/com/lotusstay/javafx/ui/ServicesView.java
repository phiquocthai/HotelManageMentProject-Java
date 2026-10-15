package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.ServiceItem;
import com.lotusstay.javafx.net.ApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;

public class ServicesView {

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));

        TableView<ServiceItem> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<ServiceItem, String> nameCol = new TableColumn<>("Tên dịch vụ");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name));
        
        TableColumn<ServiceItem, String> priceCol = new TableColumn<>("Đơn giá");
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().price)));
        
        table.getColumns().addAll(nameCol, priceCol);
        table.setPlaceholder(new Label("Chưa có dịch vụ nào."));

        Runnable reload = () -> Util.runAsync(ApiService::services, table.getItems()::setAll);
        reload.run();

        Button deleteBtn = new Button("Xóa dịch vụ đã chọn");
        deleteBtn.getStyleClass().add("btn-danger");
        deleteBtn.setOnAction(e -> {
            ServiceItem sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                Util.runAsyncVoid(() -> ApiService.deleteService(sel.id), reload);
            }
        });

        Button addBtn = new Button("+ Thêm dịch vụ");
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setOnAction(e -> openAddDialog(reload));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox toolbar = new HBox(10, deleteBtn, spacer, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 16, 0));

        root.setTop(toolbar);
        root.setCenter(table);
        return root;
    }

    private static void openAddDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Thêm dịch vụ");
        dialog.getDialogPane().getStylesheets().add(
                ServicesView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        TextField name = new TextField();
        name.setPromptText("Tên dịch vụ (VD: Ăn sáng)");
        TextField price = new TextField();
        price.setPromptText("Đơn giá (VNĐ)");

        VBox form = new VBox(10, 
                labeledBox("Tên dịch vụ", name), 
                labeledBox("Đơn giá", price)
        );
        form.setPadding(new Insets(16));
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK && !name.getText().isBlank()) {
                BigDecimal p;
                try { p = new BigDecimal(price.getText().trim()); } catch (Exception e) { p = BigDecimal.ZERO; }
                final BigDecimal priceVal = p;
                Util.runAsync(() -> ApiService.createService(name.getText().trim(), priceVal), s -> reload.run());
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
