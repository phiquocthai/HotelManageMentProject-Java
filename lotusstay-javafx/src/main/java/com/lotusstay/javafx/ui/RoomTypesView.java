package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.RoomType;
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

import java.math.BigDecimal;

public class RoomTypesView {

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));

        TableView<RoomType> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<RoomType, String> nameCol = new TableColumn<>("Loại phòng");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name));
        
        TableColumn<RoomType, String> priceCol = new TableColumn<>("Giá cơ bản (VNĐ)");
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().basePrice)));

        TableColumn<RoomType, String> guestsCol = new TableColumn<>("Số người tối đa");
        guestsCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().maxGuests != null ? String.valueOf(d.getValue().maxGuests) : "2"));

        table.getColumns().addAll(nameCol, priceCol, guestsCol);
        table.setPlaceholder(new Label("Chưa có loại phòng nào — bấm \"+ Thêm loại phòng\" để bắt đầu."));

        Runnable reload = () -> Util.runAsync(ApiService::roomTypes, table.getItems()::setAll);
        reload.run();

        Button addBtn = new Button("+ Thêm loại phòng");
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setOnAction(e -> openAddDialog(reload));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox toolbar = new HBox(10, spacer, addBtn);
        toolbar.setAlignment(Pos.CENTER_RIGHT);
        toolbar.setPadding(new Insets(0, 0, 16, 0));

        root.setTop(toolbar);
        root.setCenter(table);
        return root;
    }

    private static void openAddDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Thêm loại phòng");
        dialog.getDialogPane().getStylesheets().add(
                RoomTypesView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        TextField name = new TextField();
        name.setPromptText("Tên loại phòng (VD: Phòng Đôi)");
        
        TextField price = new TextField();
        price.setPromptText("Giá cơ bản (VNĐ)");

        TextField maxGuests = new TextField();
        maxGuests.setPromptText("Số người tối đa (VD: 2)");
        maxGuests.setText("2");

        VBox form = new VBox(10, 
                labeledBox("Tên loại phòng", name), 
                labeledBox("Giá cơ bản (VNĐ)", price),
                labeledBox("Số người tối đa", maxGuests)
        );
        form.setPadding(new Insets(16));
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK && !name.getText().isBlank()) {
                BigDecimal p;
                try { p = new BigDecimal(price.getText().trim()); } catch (Exception e) { p = BigDecimal.ZERO; }
                
                int maxG = 2;
                try { maxG = Integer.parseInt(maxGuests.getText().trim()); } catch(Exception e) { maxG = 2; }
                
                final BigDecimal priceVal = p;
                final Integer mGuests = maxG;
                Util.runAsync(() -> ApiService.createRoomType(name.getText().trim(), priceVal, mGuests), rt -> reload.run());
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
