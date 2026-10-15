package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Customer;
import com.lotusstay.javafx.model.Invoice;
import com.lotusstay.javafx.model.Room;
import com.lotusstay.javafx.net.ApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InvoicesView {

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: transparent;");

        VBox panel = new VBox(16);
        panel.getStyleClass().add("panel");
        
        // --- Header ---
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("Hóa đơn");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 16px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button exportBtn = new Button("📊 Xuất Excel");
        exportBtn.getStyleClass().add("btn-outline");
        exportBtn.setStyle("-fx-padding: 6 12;");
        
        Button printBtn = new Button("🖨️ In / PDF");
        printBtn.getStyleClass().add("btn-outline");
        printBtn.setStyle("-fx-padding: 6 12;");
        
        Button addBtn = new Button("+ Tạo hóa đơn");
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setStyle("-fx-padding: 6 16;");
        
        header.getChildren().addAll(title, spacer, exportBtn, printBtn, addBtn);
        
        // --- Table ---
        TableView<Invoice> table = new TableView<>();
        table.getStyleClass().add("table-view");
        VBox.setVgrow(table, Priority.ALWAYS);
        
        TableColumn<Invoice, String> indexCol = new TableColumn<>("#");
        indexCol.setCellFactory(col -> new TableCell<Invoice, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
        indexCol.setPrefWidth(40);
        
        TableColumn<Invoice, String> nameCol = new TableColumn<>("Khách hàng");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().customer != null ? d.getValue().customer.fullName : "Khách vãng lai"));
        nameCol.setPrefWidth(140);
                
        TableColumn<Invoice, String> roomCol = new TableColumn<>("Số phòng");
        roomCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().room != null ? d.getValue().room.number : "—"));
        roomCol.setPrefWidth(80);
        
        TableColumn<Invoice, String> roomTypeCol = new TableColumn<>("Loại phòng");
        roomTypeCol.setCellValueFactory(d -> new SimpleStringProperty(
                (d.getValue().room != null && d.getValue().room.roomType != null) ? d.getValue().room.roomType.name : "—"));
        roomTypeCol.setPrefWidth(120);
                
        TableColumn<Invoice, String> roomFeeCol = new TableColumn<>("Tiền phòng");
        roomFeeCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().roomFee) + " ₫"));
        roomFeeCol.setPrefWidth(100);
        
        TableColumn<Invoice, String> svcFeeCol = new TableColumn<>("Dịch vụ");
        svcFeeCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().serviceFee) + " ₫"));
        svcFeeCol.setPrefWidth(80);
        
        TableColumn<Invoice, String> penaltyCol = new TableColumn<>("Tiền phạt");
        penaltyCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().penaltyFee) + " ₫"));
        penaltyCol.setPrefWidth(80);
        
        TableColumn<Invoice, String> discountCol = new TableColumn<>("Giảm giá");
        discountCol.setCellValueFactory(d -> new SimpleStringProperty("0 ₫"));
        discountCol.setPrefWidth(80);
        
        TableColumn<Invoice, String> totalCol = new TableColumn<>("Tổng tiền");
        totalCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().total) + " ₫"));
        totalCol.setPrefWidth(120);
        totalCol.setCellFactory(col -> new TableCell<Invoice, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #111827;");
                }
            }
        });
        
        TableColumn<Invoice, String> paymentCol = new TableColumn<>("Thanh toán");
        paymentCol.setCellValueFactory(d -> new SimpleStringProperty(""));
        paymentCol.setPrefWidth(100);
        
        TableColumn<Invoice, String> dateCol = new TableColumn<>("Ngày");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().issuedDate));
        dateCol.setPrefWidth(100);
        
        table.getColumns().addAll(indexCol, nameCol, roomCol, roomTypeCol, roomFeeCol, svcFeeCol, penaltyCol, discountCol, totalCol, paymentCol, dateCol);
        table.setPlaceholder(new Label("Chưa có hóa đơn nào."));

        // --- Footer ---
        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(16, 24, 8, 0));
        
        Label totalTitleLabel = new Label("TỔNG DOANH THU");
        totalTitleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6B7280; -fx-font-size: 13px;");
        
        Label totalValueLabel = new Label("0 ₫");
        totalValueLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #111827; -fx-font-size: 16px;");
        
        footer.getChildren().addAll(totalTitleLabel, totalValueLabel);

        Runnable reload = () -> Util.runAsync(ApiService::invoices, list -> {
            table.getItems().setAll(list);
            BigDecimal sum = list.stream().map(i -> i.total == null ? BigDecimal.ZERO : i.total)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalValueLabel.setText(Util.money(sum) + " ₫");
        });
        reload.run();

        addBtn.setOnAction(e -> openManualInvoiceDialog(reload));

        panel.getChildren().addAll(header, table, footer);
        root.setCenter(panel);
        return root;
    }

    private static void openManualInvoiceDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Tạo hóa đơn thủ công");
        dialog.getDialogPane().getStylesheets().add(
                InvoicesView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        ComboBox<Customer> customerBox = new ComboBox<>();
        customerBox.setPromptText("Chọn khách hàng");
        customerBox.setMaxWidth(Double.MAX_VALUE);
        Util.runAsync(() -> ApiService.customers(null), customerBox.getItems()::setAll);

        ComboBox<Room> roomBox = new ComboBox<>();
        roomBox.setPromptText("Chọn phòng");
        roomBox.setMaxWidth(Double.MAX_VALUE);
        Util.runAsync(ApiService::rooms, roomBox.getItems()::setAll);

        TextField roomFee = new TextField("0");
        TextField serviceFee = new TextField("0");
        TextField penaltyFee = new TextField("0");
        DatePicker date = new DatePicker(LocalDate.now());

        VBox form = new VBox(10,
                labeledBox("Khách hàng", customerBox),
                labeledBox("Phòng", roomBox),
                labeledBox("Tiền phòng", roomFee),
                labeledBox("Phí dịch vụ khác", serviceFee),
                labeledBox("Tiền phạt", penaltyFee),
                labeledBox("Ngày lập hóa đơn", date)
        );
        form.setPadding(new Insets(16));
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Long customerId = customerBox.getValue() != null ? customerBox.getValue().id : null;
                Long roomId = roomBox.getValue() != null ? roomBox.getValue().id : null;
                BigDecimal fee = parse(roomFee.getText());
                BigDecimal svc = parse(serviceFee.getText());
                BigDecimal pen = parse(penaltyFee.getText());
                Util.runAsync(() -> ApiService.checkout(
                        null, roomId, customerId, date.getValue().toString(),
                        fee.add(svc), pen, List.of()
                ), inv -> reload.run());
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static BigDecimal parse(String s) {
        try { return new BigDecimal(s.trim().isEmpty() ? "0" : s.trim()); }
        catch (Exception e) { return BigDecimal.ZERO; }
    }
    
    private static VBox labeledBox(String label, javafx.scene.Node field) {
        Label l = new Label(label);
        l.getStyleClass().add("form-label");
        return new VBox(4, l, field);
    }
}
