package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Customer;
import com.lotusstay.javafx.net.ApiService;
import com.lotusstay.javafx.net.PageResponse;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.stream.Collectors;

public class CustomersView {

    private static final int DEFAULT_PAGE_SIZE = 10;

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: transparent;");

        VBox card = new VBox(16);
        card.getStyleClass().add("panel");
        VBox.setVgrow(card, Priority.ALWAYS);

        // 1. Header info
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Quản lý khách hàng");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 700;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        headerRow.getChildren().addAll(title, topSpacer);

        // 2. Toolbar
        TextField search = new TextField();
        search.setPromptText("Tìm theo ID, họ tên, số CCCD, SĐT, email...");
        search.setPrefWidth(360);
        search.setMinWidth(280);
        search.setPrefHeight(38);

        Button searchBtn = new Button("🔍 Tìm kiếm");
        searchBtn.getStyleClass().add("btn-outline");

        Button refreshBtn = new Button("🔄 Làm mới");
        refreshBtn.getStyleClass().add("btn-outline");

        Button addBtn = new Button("+ Thêm khách hàng");
        addBtn.getStyleClass().add("btn-gold");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, search, searchBtn, refreshBtn, spacer, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // 3. Table
        ObservableList<Customer> tableData = FXCollections.observableArrayList();
        TableView<Customer> table = new TableView<>(tableData);
        table.getStyleClass().add("table-view");
        table.setEditable(true);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có khách hàng nào trong hệ thống."));

        TableColumn<Customer, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(70);
        idCol.setPrefWidth(80);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().id != null ? String.valueOf(d.getValue().id) : "—"));
        idCol.setStyle("-fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 13px; -fx-alignment: CENTER_LEFT;");

        TableColumn<Customer, String> nameCol = new TableColumn<>("Họ và tên");
        nameCol.setMinWidth(170);
        nameCol.setPrefWidth(190);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().fullName != null ? d.getValue().fullName : "—"));
        nameCol.setStyle("-fx-font-weight: 700;");

        TableColumn<Customer, String> cccdCol = new TableColumn<>("Số CCCD");
        cccdCol.setMinWidth(140);
        cccdCol.setPrefWidth(150);
        cccdCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().citizenId != null && !d.getValue().citizenId.isBlank() ? d.getValue().citizenId : "—"));
        cccdCol.setStyle("-fx-font-weight: 500;");

        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        emailCol.setMinWidth(190);
        emailCol.setPrefWidth(210);
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().email != null && !d.getValue().email.isBlank() ? d.getValue().email : "—"));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("SĐT");
        phoneCol.setMinWidth(120);
        phoneCol.setPrefWidth(130);
        phoneCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().phone != null && !d.getValue().phone.isBlank() ? d.getValue().phone : "—"));
        phoneCol.setStyle("-fx-font-weight: 500;");

        // 4. Thao tác: Sửa, Xóa (Khớp giao diện)
        TableColumn<Customer, Void> actionCol = new TableColumn<>("Thao tác");
        actionCol.setMinWidth(160);
        actionCol.setPrefWidth(170);

        table.getColumns().addAll(idCol, nameCol, cccdCol, emailCol, phoneCol, actionCol);

        // 5. Thanh phân trang Footer (Khớp chuẩn backend & ảnh mẫu)
        HBox paginationBar = new HBox(16);
        paginationBar.setAlignment(Pos.CENTER_LEFT);
        paginationBar.setPadding(new Insets(12, 0, 4, 0));

        Label recordInfoLabel = new Label("Hiển thị 0 khách hàng");
        recordInfoLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px; -fx-text-fill: #94A3B8; -fx-font-weight: 600;");

        // Bộ chọn số lượng bản ghi trên một trang (Page Size) theo đúng backend
        Label sizeLabel = new Label("Số bản ghi / trang:");
        sizeLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-text-fill: #94A3B8; -fx-font-weight: 500;");

        ComboBox<Integer> pageSizeCombo = new ComboBox<>(FXCollections.observableArrayList(5, 10, 15, 20));
        pageSizeCombo.setValue(DEFAULT_PAGE_SIZE);
        pageSizeCombo.setStyle(
                "-fx-background-color: #1E293B; -fx-border-color: rgba(148, 163, 184, 0.3); " +
                "-fx-border-radius: 6; -fx-background-radius: 6; -fx-pref-height: 28px; -fx-font-size: 12px; -fx-cursor: hand;"
        );

        HBox leftPagingBox = new HBox(12, recordInfoLabel, sizeLabel, pageSizeCombo);
        leftPagingBox.setAlignment(Pos.CENTER_LEFT);

        Region pagSpacer = new Region();
        HBox.setHgrow(pagSpacer, Priority.ALWAYS);

        HBox pageButtonsBox = new HBox(6);
        pageButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        paginationBar.getChildren().addAll(leftPagingBox, pagSpacer, pageButtonsBox);

        // State phân trang
        int[] currentPage = new int[]{1};
        int[] currentPageSize = new int[]{DEFAULT_PAGE_SIZE};

        // Hàm tải trang chuyên nghiệp
        @SuppressWarnings("unchecked")
        java.util.function.Consumer<Integer>[] loadPageHolder = new java.util.function.Consumer[1];

        loadPageHolder[0] = targetPage -> {
            currentPage[0] = Math.max(1, targetPage);
            String q = search.getText() == null ? "" : search.getText().trim();
            Util.runAsync(() -> ApiService.customerPage(currentPage[0], currentPageSize[0], q), (PageResponse<Customer> page) -> {
                tableData.setAll(page.getContent() != null ? page.getContent() : List.of());
                table.scrollTo(0);

                int totalPages = Math.max(1, page.totalPages);
                long totalElements = page.totalElements;
                currentPage[0] = page.pageNo;

                // Cập nhật nhãn thông tin bản ghi theo dữ liệu thực từ backend
                if (totalElements == 0) {
                    recordInfoLabel.setText("Hiển thị 0 khách hàng");
                } else {
                    int from = (page.pageNo - 1) * page.pageSize + 1;
                    int to = (int) Math.min((long) page.pageNo * page.pageSize, totalElements);
                    recordInfoLabel.setText("Hiển thị " + from + " – " + to + " trên tổng " + totalElements + " khách hàng");
                }

                // Render các nút phân trang
                renderPageButtons(pageButtonsBox, currentPage[0], totalPages, newPage -> loadPageHolder[0].accept(newPage));
            });
        };

        Runnable loadCurrentPage = () -> loadPageHolder[0].accept(currentPage[0]);

        pageSizeCombo.setOnAction(e -> {
            Integer selectedSize = pageSizeCombo.getValue();
            if (selectedSize != null && selectedSize != currentPageSize[0]) {
                currentPageSize[0] = selectedSize;
                loadPageHolder[0].accept(1); // đổi pageSize quay về trang đầu
            }
        });

        // Gán hành động cho actionCol
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Sửa");
            private final Button delBtn = new Button("Xóa");
            private final HBox box = new HBox(8, editBtn, delBtn);

            {
                editBtn.getStyleClass().add("btn-action-edit");
                editBtn.setGraphic(IconUtils.icon("edit", 12, Color.WHITE));
                editBtn.setGraphicTextGap(6);
                editBtn.setMinWidth(64);
                editBtn.setStyle("-fx-padding: 3 10; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 64px;");

                delBtn.getStyleClass().add("btn-danger");
                delBtn.setGraphic(IconUtils.icon("trash", 12, Color.WHITE));
                delBtn.setGraphicTextGap(6);
                delBtn.setMinWidth(64);
                delBtn.setStyle("-fx-padding: 3 10; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 64px;");

                box.setAlignment(Pos.CENTER_LEFT);
                setAlignment(Pos.CENTER_LEFT);

                editBtn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());
                    openCustomerDialog(c, loadCurrentPage);
                });

                delBtn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());
                    confirmDelete(c, loadCurrentPage);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // Tìm kiếm & Làm mới
        searchBtn.setOnAction(e -> {
            currentPage[0] = 1;
            loadCurrentPage.run();
        });
        search.setOnAction(e -> {
            currentPage[0] = 1;
            loadCurrentPage.run();
        });
        refreshBtn.setOnAction(e -> {
            search.clear();
            currentPage[0] = 1;
            loadCurrentPage.run();
        });

        addBtn.setOnAction(e -> openCustomerDialog(null, loadCurrentPage));

        // Load trang đầu tiên
        loadCurrentPage.run();

        card.getChildren().addAll(headerRow, toolbar, table, paginationBar);
        root.setCenter(card);
        return root;
    }

    /**
     * Vẽ thanh nút phân trang kiểu [ < ] [ 1 ] [ 2 ] [ 3 ] [ 4 ] [ ... ] [ > ]
     */
    private static void renderPageButtons(HBox box, int cur, int total, java.util.function.Consumer<Integer> onSelect) {
        box.getChildren().clear();

        // Nút Previous [ < ]
        Button prev = new Button("‹");
        prev.setStyle(pageButtonStyle(false));
        prev.setDisable(cur <= 1);
        prev.setOnAction(e -> onSelect.accept(cur - 1));
        box.getChildren().add(prev);

        if (total <= 7) {
            for (int i = 1; i <= total; i++) {
                box.getChildren().add(createNumBtn(i, i == cur, onSelect));
            }
        } else {
            // Hiển thị dạng 1 2 3 4 ... total như trong ảnh
            if (cur <= 4) {
                for (int i = 1; i <= 4; i++) {
                    box.getChildren().add(createNumBtn(i, i == cur, onSelect));
                }
                box.getChildren().add(createEllipsis());
                box.getChildren().add(createNumBtn(total, total == cur, onSelect));
            } else if (cur >= total - 3) {
                box.getChildren().add(createNumBtn(1, 1 == cur, onSelect));
                box.getChildren().add(createEllipsis());
                for (int i = total - 3; i <= total; i++) {
                    box.getChildren().add(createNumBtn(i, i == cur, onSelect));
                }
            } else {
                box.getChildren().add(createNumBtn(1, 1 == cur, onSelect));
                box.getChildren().add(createEllipsis());
                box.getChildren().add(createNumBtn(cur - 1, false, onSelect));
                box.getChildren().add(createNumBtn(cur, true, onSelect));
                box.getChildren().add(createNumBtn(cur + 1, false, onSelect));
                box.getChildren().add(createEllipsis());
                box.getChildren().add(createNumBtn(total, false, onSelect));
            }
        }

        // Nút Next [ > ]
        Button next = new Button("›");
        next.setStyle(pageButtonStyle(false));
        next.setDisable(cur >= total);
        next.setOnAction(e -> onSelect.accept(cur + 1));
        box.getChildren().add(next);
    }

    private static Button createNumBtn(int page, boolean active, java.util.function.Consumer<Integer> onSelect) {
        Button b = new Button(String.valueOf(page));
        b.setStyle(pageButtonStyle(active));
        b.setOnAction(e -> {
            if (!active) onSelect.accept(page);
        });
        return b;
    }

    private static Label createEllipsis() {
        Label l = new Label("...");
        l.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-font-weight: 700; -fx-padding: 0 4; -fx-alignment: CENTER;");
        return l;
    }

    private static String pageButtonStyle(boolean active) {
        if (active) {
            // Nền đen đậm / vàng gold nổi bật khớp ảnh
            return "-fx-background-color: #0F172A; -fx-text-fill: #FFFFFF; -fx-font-weight: 800; " +
                    "-fx-background-radius: 6; -fx-border-color: #38BDF8; -fx-border-radius: 6; " +
                    "-fx-border-width: 1.5; -fx-min-width: 32px; -fx-pref-height: 30px; -fx-font-size: 12.5px; -fx-cursor: hand;";
        }
        return "-fx-background-color: rgba(30, 41, 59, 0.45); -fx-text-fill: #CBD5E1; -fx-font-weight: 600; " +
                "-fx-background-radius: 6; -fx-border-color: rgba(148, 163, 184, 0.25); -fx-border-radius: 6; " +
                "-fx-border-width: 1; -fx-min-width: 32px; -fx-pref-height: 30px; -fx-font-size: 12.5px; -fx-cursor: hand;";
    }

    private static void confirmDelete(Customer c, Runnable reload) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa khách hàng");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa khách hàng " + c.fullName + " (ID: #" + c.id + ")?");
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

        TextField name = new TextField(isEdit ? existing.fullName : "");
        name.setPromptText("Họ và tên khách hàng");

        TextField phone = new TextField(isEdit ? existing.phone : "");
        phone.setPromptText("Số điện thoại (10 chữ số)");

        TextField email = new TextField(isEdit ? existing.email : "");
        email.setPromptText("Địa chỉ email");

        TextField cccd = new TextField(isEdit ? existing.citizenId : "");
        cccd.setPromptText("Số CCCD (12 chữ số)");

        GridPane gp = new GridPane();
        gp.setHgap(12);
        gp.setVgap(12);
        gp.setPadding(new Insets(20));

        int row = 0;
        if (isEdit) {
            TextField idField = new TextField("#" + existing.id);
            idField.setDisable(true);
            gp.add(new Label("ID:"), 0, row); gp.add(idField, 1, row++);
        }
        gp.add(new Label("Họ và tên:"), 0, row); gp.add(name, 1, row++);
        gp.add(new Label("Số điện thoại:"), 0, row); gp.add(phone, 1, row++);
        gp.add(new Label("Email:"), 0, row); gp.add(email, 1, row++);
        gp.add(new Label("Số CCCD:"), 0, row); gp.add(cccd, 1, row++);

        dialog.getDialogPane().setContent(gp);
        ButtonType saveBtn = new ButtonType(isEdit ? "Cập nhật" : "Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == saveBtn) {
                String fullName = name.getText().trim();
                String phoneVal = phone.getText().trim();
                String emailVal = email.getText().trim();
                String cccdVal = cccd.getText().trim();

                if (fullName.isEmpty()) {
                    Util.error("Họ và tên không được để trống.");
                    return null;
                }

                if (isEdit) {
                    Util.runAsync(() -> ApiService.updateCustomer(existing.id, null, fullName, phoneVal, emailVal, cccdVal),
                            c -> reload.run());
                } else {
                    Util.runAsync(() -> ApiService.createCustomer(fullName, phoneVal, emailVal, cccdVal),
                            c -> reload.run());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}
