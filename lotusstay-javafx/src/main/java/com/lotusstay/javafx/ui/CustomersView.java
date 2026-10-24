package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Customer;
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

public class CustomersView {

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

        Label title = new Label("Quản lý khách hàng");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 700;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        headerRow.getChildren().addAll(title, topSpacer);

        // Data collection
        ObservableList<Customer> masterData = FXCollections.observableArrayList();
        FilteredList<Customer> filteredData = new FilteredList<>(masterData, p -> true);

        // Top Toolbar (Matching Wireframe Image 2)
        TextField search = new TextField();
        search.setPromptText("Tìm theo họ tên, số CCCD, SĐT, email...");
        search.setPrefWidth(320);

        Button searchBtn = new Button("🔍 Tìm kiếm");
        searchBtn.getStyleClass().add("btn-outline");

        Button refreshBtn = new Button("🔄 Làm mới");
        refreshBtn.getStyleClass().add("btn-outline");

        Button deleteSelectedBtn = new Button("🗑️ Xóa mục đã chọn");
        deleteSelectedBtn.getStyleClass().add("btn-danger");

        Button addBtn = new Button("+ Thêm khách hàng");
        addBtn.getStyleClass().add("btn-gold");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, search, searchBtn, refreshBtn, spacer, deleteSelectedBtn, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Filter predicate
        Runnable applyFilter = () -> {
            String q = search.getText() == null ? "" : search.getText().trim().toLowerCase();
            filteredData.setPredicate(c -> {
                if (q.isEmpty()) return true;
                boolean matchName = c.fullName != null && c.fullName.toLowerCase().contains(q);
                boolean matchCode = c.code != null && c.code.toLowerCase().contains(q);
                boolean matchCitizen = c.citizenId != null && c.citizenId.toLowerCase().contains(q);
                boolean matchEmail = c.email != null && c.email.toLowerCase().contains(q);
                boolean matchPhone = c.phone != null && c.phone.toLowerCase().contains(q);
                return matchName || matchCode || matchCitizen || matchEmail || matchPhone;
            });
        };

        search.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        searchBtn.setOnAction(e -> applyFilter.run());

        // Table
        TableView<Customer> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setItems(filteredData);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có khách hàng nào trong hệ thống."));

        TableColumn<Customer, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(75);
        idCol.setPrefWidth(85);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().id)));
        idCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER; -fx-font-size: 13.5px; -fx-text-fill: #FFFFFF;");

        TableColumn<Customer, String> codeCol = new TableColumn<>("Mã KH");
        codeCol.setMinWidth(115);
        codeCol.setPrefWidth(125);
        codeCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().code != null ? d.getValue().code : String.format("KH%03d", d.getValue().id)));
        codeCol.setStyle("-fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 13.5px;");

        TableColumn<Customer, String> nameCol = new TableColumn<>("Họ và tên");
        nameCol.setMinWidth(180);
        nameCol.setPrefWidth(200);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().fullName != null ? d.getValue().fullName : "—"));
        nameCol.setStyle("-fx-font-weight: 800;");

        TableColumn<Customer, String> cccdCol = new TableColumn<>("Số CCCD");
        cccdCol.setMinWidth(150);
        cccdCol.setPrefWidth(165);
        cccdCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().citizenId != null && !d.getValue().citizenId.isBlank() ? d.getValue().citizenId : "—"));
        cccdCol.setStyle("-fx-font-weight: 600;");

        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        emailCol.setMinWidth(200);
        emailCol.setPrefWidth(225);
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().email != null && !d.getValue().email.isBlank() ? d.getValue().email : "—"));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("SĐT");
        phoneCol.setMinWidth(130);
        phoneCol.setPrefWidth(140);
        phoneCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().phone != null && !d.getValue().phone.isBlank() ? d.getValue().phone : "—"));
        phoneCol.setStyle("-fx-font-weight: 600;");

        TableColumn<Customer, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setMinWidth(130);
        statusCol.setPrefWidth(140);
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().active ? "Hoạt động" : "Tạm khóa"));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    boolean act = "Hoạt động".equalsIgnoreCase(item);
                    Label badge = new Label(item);
                    badge.getStyleClass().addAll("badge", act ? "badge-trong" : "badge-bao_tri");
                    badge.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 12;");
                    setGraphic(badge);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Column Thao tác: Sửa, Xóa (hiển thị rõ chữ đầy đủ)
        TableColumn<Customer, Void> actionCol = new TableColumn<>("Thao tác");
        actionCol.setMinWidth(180);
        actionCol.setPrefWidth(190);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Sửa");
            private final Button delBtn = new Button("Xóa");
            private final HBox box = new HBox(8, editBtn, delBtn);

            {
                editBtn.getStyleClass().add("btn-outline");
                editBtn.setGraphic(IconUtils.icon("edit", 12, Color.web("#93C5FD")));
                editBtn.setGraphicTextGap(6);
                editBtn.setMinWidth(68);
                editBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");

                delBtn.getStyleClass().add("btn-danger");
                delBtn.setGraphic(IconUtils.icon("trash", 12, Color.web("#F87171")));
                delBtn.setGraphicTextGap(6);
                delBtn.setMinWidth(68);
                delBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");

                box.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());
                    openCustomerDialog(c, () -> reload(masterData, applyFilter));
                });

                delBtn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());
                    confirmDelete(c, () -> reload(masterData, applyFilter));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(idCol, codeCol, nameCol, cccdCol, emailCol, phoneCol, statusCol, actionCol);

        // Reload logic
        Runnable loadAction = () -> reload(masterData, applyFilter);
        loadAction.run();

        refreshBtn.setOnAction(e -> {
            search.clear();
            loadAction.run();
        });

        addBtn.setOnAction(e -> openCustomerDialog(null, loadAction));

        deleteSelectedBtn.setOnAction(e -> {
            Customer sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                Util.info("Vui lòng chọn khách hàng trong bảng để xóa.");
                return;
            }
            confirmDelete(sel, loadAction);
        });

        card.getChildren().addAll(headerRow, toolbar, table);
        root.setCenter(card);
        return root;
    }

    private static void reload(ObservableList<Customer> masterData, Runnable applyFilter) {
        Util.runAsync(() -> ApiService.customers(null), list -> {
            masterData.setAll(list);
            applyFilter.run();
        });
    }

    private static void confirmDelete(Customer c, Runnable reload) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa khách hàng");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa khách hàng " + c.fullName + " (" + c.code + ")?");
        alert.setContentText("Hành động này sẽ xóa khách hàng khỏi danh sách.");
        alert.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            Util.runAsyncVoid(() -> ApiService.deleteCustomer(c.id), reload);
        });
    }

    private static void openCustomerDialog(Customer existing, Runnable reload) {
        boolean isEdit = existing != null;
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Cập nhật thông tin khách hàng" : "Thêm mới khách hàng");
        dialog.getDialogPane().getStylesheets().add(
                CustomersView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        TextField code = new TextField(isEdit ? existing.code : "");
        code.setPromptText("Tự động (VD: KH007)");
        if (isEdit) code.setDisable(true);

        TextField name = new TextField(isEdit ? existing.fullName : "");
        name.setPromptText("Họ và tên khách hàng (tối đa 30 ký tự)");

        TextField idNumber = new TextField(isEdit && existing.citizenId != null ? existing.citizenId : "");
        idNumber.setPromptText("Số CCCD (12 số)");

        TextField phone = new TextField(isEdit && existing.phone != null ? existing.phone : "");
        phone.setPromptText("Số điện thoại liên hệ (10 số)");

        TextField email = new TextField(isEdit && existing.email != null ? existing.email : "");
        email.setPromptText("Email (VD: khachhang@gmail.com)");

        VBox form = new VBox(10,
                labeledBox("Mã khách hàng", code),
                labeledBox("Họ và tên (*)", name),
                labeledBox("Số CCCD (so_CCCD)", idNumber),
                labeledBox("Số điện thoại (SDT)", phone),
                labeledBox("Địa chỉ Email", email)
        );
        form.setPadding(new Insets(16));
        form.setPrefWidth(420);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String fullName = name.getText().trim();
                if (fullName.isBlank()) {
                    Util.error("Họ và tên khách hàng không được để trống.");
                    return null;
                }
                String cccdVal = idNumber.getText().trim();
                String phoneVal = phone.getText().trim();
                String emailVal = email.getText().trim();
                String codeVal = code.getText().trim();

                if (isEdit) {
                    Util.runAsync(() -> ApiService.updateCustomer(existing.id, codeVal, fullName, phoneVal, emailVal, cccdVal),
                            c -> reload.run());
                } else {
                    Util.runAsync(() -> ApiService.createCustomer(codeVal, fullName, phoneVal, emailVal, cccdVal),
                            c -> reload.run());
                }
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
