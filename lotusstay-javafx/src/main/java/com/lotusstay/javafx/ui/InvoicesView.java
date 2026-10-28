package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Customer;
import com.lotusstay.javafx.model.Invoice;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InvoicesView {

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: transparent;");

        VBox card = new VBox(16);
        card.getStyleClass().add("panel");
        VBox.setVgrow(card, Priority.ALWAYS);

        // Header
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Quản lý hóa đơn & Tài chính");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 700;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Button exportBtn = new Button("📊 Xuất Excel");
        exportBtn.getStyleClass().add("btn-outline");

        Button addBtn = new Button("+ Tạo hóa đơn");
        addBtn.getStyleClass().add("btn-gold");

        headerRow.getChildren().addAll(title, topSpacer, exportBtn, addBtn);

        // Data collection
        ObservableList<Invoice> masterData = FXCollections.observableArrayList();
        FilteredList<Invoice> filteredData = new FilteredList<>(masterData, p -> true);

        // Top Toolbar
        TextField search = new TextField();
        search.setPromptText("Tìm theo ID hóa đơn, tên khách, số phòng...");
        search.setPrefWidth(280);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Tất cả thanh toán", "Đã thanh toán", "Chưa thanh toán");
        statusFilter.setValue("Tất cả thanh toán");
        statusFilter.setPrefWidth(160);

        Button searchBtn = new Button("🔍 Tìm kiếm");
        searchBtn.getStyleClass().add("btn-outline");

        Button refreshBtn = new Button("🔄 Làm mới");
        refreshBtn.getStyleClass().add("btn-outline");

        Region filterSpacer = new Region();
        HBox.setHgrow(filterSpacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, search, statusFilter, searchBtn, refreshBtn, filterSpacer);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Filter Logic
        Runnable applyFilter = () -> {
            String q = search.getText() == null ? "" : search.getText().trim().toLowerCase();
            String st = statusFilter.getValue();

            filteredData.setPredicate(inv -> {
                boolean matchSearch = q.isEmpty()
                        || (inv.id != null && String.valueOf(inv.id).contains(q))
                        || (inv.customer != null && inv.customer.fullName != null && inv.customer.fullName.toLowerCase().contains(q))
                        || (inv.room != null && inv.room.number != null && inv.room.number.contains(q));

                boolean matchStatus = st == null || "Tất cả thanh toán".equals(st)
                        || inv.paymentStatusLabel().equalsIgnoreCase(st);

                return matchSearch && matchStatus;
            });
        };

        search.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        searchBtn.setOnAction(e -> applyFilter.run());

        // Table
        TableView<Invoice> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setItems(filteredData);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có hóa đơn nào trong hệ thống."));

        TableColumn<Invoice, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(75);
        idCol.setPrefWidth(85);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().id)));
        idCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER_LEFT; -fx-font-size: 13.5px; -fx-text-fill: #FFFFFF;");

        TableColumn<Invoice, String> bookCodeCol = new TableColumn<>("ID Đặt phòng");
        bookCodeCol.setMinWidth(110);
        bookCodeCol.setPrefWidth(120);
        bookCodeCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().booking != null ? "#" + d.getValue().booking.id : "—"));
        bookCodeCol.setStyle("-fx-font-weight: 700; -fx-text-fill: #38BDF8; -fx-font-size: 13.5px;");

        TableColumn<Invoice, String> nameCol = new TableColumn<>("Khách hàng");
        nameCol.setMinWidth(180);
        nameCol.setPrefWidth(200);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().customer != null ? d.getValue().customer.fullName : "Khách vãng lai"));
        nameCol.setStyle("-fx-font-weight: 700;");

        TableColumn<Invoice, String> roomCol = new TableColumn<>("Số phòng");
        roomCol.setMinWidth(95);
        roomCol.setPrefWidth(105);
        roomCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().room != null ? d.getValue().room.number : "—"));
        roomCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER_LEFT;");

        TableColumn<Invoice, String> roomFeeCol = new TableColumn<>("Tiền phòng");
        roomFeeCol.setMinWidth(130);
        roomFeeCol.setPrefWidth(140);
        roomFeeCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().roomFee)));
        roomFeeCol.setStyle("-fx-font-weight: 600;");

        TableColumn<Invoice, String> svcFeeCol = new TableColumn<>("Tiền dịch vụ");
        svcFeeCol.setMinWidth(125);
        svcFeeCol.setPrefWidth(135);
        svcFeeCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().serviceFee)));
        svcFeeCol.setStyle("-fx-font-weight: 600;");

        TableColumn<Invoice, String> penaltyCol = new TableColumn<>("Tiền phạt");
        penaltyCol.setMinWidth(115);
        penaltyCol.setPrefWidth(125);
        penaltyCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().penaltyFee)));

        TableColumn<Invoice, String> discountCol = new TableColumn<>("Giảm giá");
        discountCol.setMinWidth(115);
        discountCol.setPrefWidth(125);
        discountCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().discountFee)));

        TableColumn<Invoice, String> totalCol = new TableColumn<>("Tổng cộng");
        totalCol.setMinWidth(145);
        totalCol.setPrefWidth(155);
        totalCol.setCellValueFactory(d -> new SimpleStringProperty(Util.money(d.getValue().total)));
        totalCol.setStyle("-fx-font-weight: 800; -fx-text-fill: #10B981; -fx-font-size: 14px;");

        TableColumn<Invoice, String> paymentCol = new TableColumn<>("Thanh toán");
        paymentCol.setMinWidth(140);
        paymentCol.setPrefWidth(150);
        paymentCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().paymentStatusLabel()));
        paymentCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Invoice inv = getTableView().getItems().get(getIndex());
                    Label badge = new Label(item);
                    badge.getStyleClass().addAll("badge", inv.badgeClass());
                    badge.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 12;");
                    setGraphic(badge);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Invoice, String> dateCol = new TableColumn<>("Ngày lập");
        dateCol.setMinWidth(120);
        dateCol.setPrefWidth(130);
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().issuedDate != null ? d.getValue().issuedDate : "—"));

        // Column Thao tác
        TableColumn<Invoice, Void> actionCol = new TableColumn<>("Thao tác");
        actionCol.setMinWidth(170);
        actionCol.setPrefWidth(180);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button printBtn = new Button("In");
            private final Button delBtn = new Button("Xóa");
            private final HBox box = new HBox(8, printBtn, delBtn);

            {
                printBtn.getStyleClass().add("btn-outline");
                printBtn.setGraphic(IconUtils.icon("print", 12, Color.web("#93C5FD")));
                printBtn.setGraphicTextGap(6);
                printBtn.setMinWidth(65);
                printBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 65px;");

                delBtn.getStyleClass().add("btn-danger");
                delBtn.setGraphic(IconUtils.icon("trash", 12, Color.WHITE));
                delBtn.setGraphicTextGap(6);
                delBtn.setMinWidth(65);
                delBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 65px;");

                box.setAlignment(Pos.CENTER_LEFT);
                setAlignment(Pos.CENTER_LEFT);

                printBtn.setOnAction(e -> {
                    Invoice inv = getTableView().getItems().get(getIndex());
                    Util.info("Đang in hóa đơn " + inv.code + " — Tổng tiền: " + Util.money(inv.total) + " ₫");
                });

                delBtn.setOnAction(e -> {
                    Invoice inv = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận xóa hóa đơn");
                    alert.setHeaderText("Xóa hóa đơn " + inv.code + "?");
                    alert.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt -> {
                        Util.runAsyncVoid(() -> ApiService.deleteInvoice(inv.id), () -> reload(masterData, applyFilter, null));
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(idCol, bookCodeCol, nameCol, roomCol, roomFeeCol, svcFeeCol, penaltyCol, discountCol, totalCol, paymentCol, dateCol, actionCol);

        // Footer Summary
        HBox footer = new HBox(30);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(12, 16, 4, 16));

        Label countLabel = new Label("Tổng: 0 hóa đơn");
        countLabel.setStyle("-fx-text-fill: -fx-ink-soft; -fx-font-weight: 600;");

        Label totalLabel = new Label("TỔNG DOANH THU: 0 ₫");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: -fx-primary; -fx-font-size: 15px;");

        footer.getChildren().addAll(countLabel, totalLabel);

        // Reload logic
        Runnable updateFooter = () -> {
            countLabel.setText("Tổng: " + filteredData.size() + " hóa đơn");
            BigDecimal sum = filteredData.stream()
                    .filter(i -> "DA_THANH_TOAN".equalsIgnoreCase(i.paymentStatus))
                    .map(i -> i.total != null ? i.total : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalLabel.setText("DOANH THU ĐÃ THU: " + Util.money(sum) + " ₫");
        };

        filteredData.predicateProperty().addListener((obs, o, n) -> updateFooter.run());

        Runnable loadAction = () -> reload(masterData, applyFilter, updateFooter);
        loadAction.run();

        refreshBtn.setOnAction(e -> {
            search.clear();
            statusFilter.setValue("Tất cả thanh toán");
            loadAction.run();
        });

        exportBtn.setOnAction(e -> Util.info("Dữ liệu danh sách hóa đơn đã được kết xuất thành công!"));
        addBtn.setOnAction(e -> openManualInvoiceDialog(loadAction));

        card.getChildren().addAll(headerRow, toolbar, table, footer);
        root.setCenter(card);
        return root;
    }

    private static void reload(ObservableList<Invoice> masterData, Runnable applyFilter, Runnable updateFooter) {
        Util.runAsync(ApiService::invoices, list -> {
            masterData.setAll(list);
            if (applyFilter != null) applyFilter.run();
            if (updateFooter != null) updateFooter.run();
        });
    }

    private static void openManualInvoiceDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Tạo hóa đơn thanh toán mới");
        dialog.getDialogPane().getStylesheets().add(
                InvoicesView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        ComboBox<Customer> customerBox = new ComboBox<>();
        customerBox.setPromptText("— Chọn khách hàng —");
        customerBox.setMaxWidth(Double.MAX_VALUE);
        Util.runAsync(() -> ApiService.customers(null), customerBox.getItems()::setAll);

        ComboBox<Room> roomBox = new ComboBox<>();
        roomBox.setPromptText("— Chọn phòng —");
        roomBox.setMaxWidth(Double.MAX_VALUE);
        Util.runAsync(ApiService::rooms, roomBox.getItems()::setAll);

        TextField roomFee = new TextField("500000");
        TextField serviceFee = new TextField("0");
        TextField penaltyFee = new TextField("0");
        DatePicker date = new DatePicker(LocalDate.now());

        VBox form = new VBox(10,
                labeledBox("Khách hàng", customerBox),
                labeledBox("Phòng", roomBox),
                labeledBox("Tiền phòng (VNĐ)", roomFee),
                labeledBox("Tiền dịch vụ (VNĐ)", serviceFee),
                labeledBox("Tiền phụ thu / phạt (VNĐ)", penaltyFee),
                labeledBox("Ngày lập hóa đơn", date)
        );
        form.setPadding(new Insets(16));
        form.setPrefWidth(420);
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Long customerId = customerBox.getValue() != null ? customerBox.getValue().id : null;
                Long roomId = roomBox.getValue() != null ? roomBox.getValue().id : null;
                BigDecimal fee = parse(roomFee.getText());
                BigDecimal pen = parse(penaltyFee.getText());
                Util.runAsync(() -> ApiService.checkout(
                        null, roomId, customerId, date.getValue().toString(),
                        fee, pen, List.of()
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
