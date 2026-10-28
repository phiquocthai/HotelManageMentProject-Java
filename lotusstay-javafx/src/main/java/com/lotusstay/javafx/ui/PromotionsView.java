package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Promotion;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PromotionsView {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

        Label title = new Label("Quản lý chương trình khuyến mãi & Voucher");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 700;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        headerRow.getChildren().addAll(title, topSpacer);

        // Data collection
        ObservableList<Promotion> masterData = FXCollections.observableArrayList();
        ObservableList<Promotion> pagedData = FXCollections.observableArrayList();

        // Toolbar
        TextField search = new TextField();
        search.setPromptText("🔍 Tìm theo ID, mã voucher, tên chương trình...");
        search.setPrefWidth(310);
        search.setMinWidth(250);
        search.setPrefHeight(38);

        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.getItems().addAll("Tất cả hình thức", "Phần trăm (%)", "Số tiền (VNĐ)");
        typeFilter.setValue("Tất cả hình thức");
        typeFilter.setMinWidth(180);
        typeFilter.setPrefWidth(180);
        typeFilter.setPrefHeight(38);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Tất cả trạng thái", "Đang áp dụng", "Sắp diễn ra", "Đã hết hạn", "Tạm ngưng");
        statusFilter.setValue("Tất cả trạng thái");
        statusFilter.setMinWidth(180);
        statusFilter.setPrefWidth(180);
        statusFilter.setPrefHeight(38);

        Button searchBtn = new Button("Tìm kiếm");
        searchBtn.getStyleClass().add("btn-outline");
        searchBtn.setGraphic(IconUtils.icon("search", 13, Color.web("#94A3B8")));
        searchBtn.setMinWidth(Region.USE_PREF_SIZE);

        Button refreshBtn = new Button("Làm mới");
        refreshBtn.getStyleClass().add("btn-outline");
        refreshBtn.setGraphic(IconUtils.icon("refresh", 13, Color.web("#94A3B8")));
        refreshBtn.setMinWidth(Region.USE_PREF_SIZE);

        Button addBtn = new Button("Thêm khuyến mãi");
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setGraphic(IconUtils.icon("plus", 14, Color.web("#0A0D14")));
        addBtn.setMinWidth(Region.USE_PREF_SIZE);
        addBtn.setPrefWidth(165);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(8, search, typeFilter, statusFilter, searchBtn, refreshBtn, spacer, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Table
        TableView<Promotion> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setItems(pagedData);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có chương trình khuyến mãi nào trong hệ thống."));

        // ── Pagination Bar & Logic ────────────────────────────────────────────
        int[] currentPage = new int[]{1};
        int[] currentPageSize = new int[]{PaginationHelper.DEFAULT_PAGE_SIZE};
        PaginationHelper.PaginationBar paginationBar = new PaginationHelper.PaginationBar("khuyến mãi");

        final Runnable[] applyFilterRef = new Runnable[1];
        applyFilterRef[0] = () -> {
            String q = search.getText() == null ? "" : search.getText().trim().toLowerCase();
            String typeVal = typeFilter.getValue();
            String stVal = statusFilter.getValue();

            List<Promotion> res = masterData.stream().filter(p -> {
                boolean matchSearch = q.isEmpty()
                        || (p.id != null && String.valueOf(p.id).contains(q))
                        || (p.code != null && p.code.toLowerCase().contains(q))
                        || (p.name != null && p.name.toLowerCase().contains(q));

                boolean matchType = true;
                if ("Phần trăm (%)".equals(typeVal)) {
                    matchType = "PHAN_TRAM".equalsIgnoreCase(p.discountType);
                } else if ("Số tiền (VNĐ)".equals(typeVal)) {
                    matchType = "SO_TIEN".equalsIgnoreCase(p.discountType);
                }

                boolean matchStatus = true;
                if (stVal != null && !"Tất cả trạng thái".equals(stVal)) {
                    matchStatus = stVal.equalsIgnoreCase(p.statusDisplay());
                }

                return matchSearch && matchType && matchStatus;
            }).collect(Collectors.toList());

            int totalElements = res.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalElements / currentPageSize[0]));
            if (currentPage[0] > totalPages) currentPage[0] = totalPages;
            if (currentPage[0] < 1) currentPage[0] = 1;

            int from = totalElements == 0 ? 0 : (currentPage[0] - 1) * currentPageSize[0];
            int to = Math.min(from + currentPageSize[0], totalElements);

            pagedData.setAll(totalElements > 0 ? res.subList(from, to) : List.of());
            table.scrollTo(0);

            paginationBar.update(currentPage[0], currentPageSize[0], totalElements, newPage -> {
                currentPage[0] = newPage;
                applyFilterRef[0].run();
            });
        };
        Runnable applyFilter = () -> applyFilterRef[0].run();

        Runnable onFilterChange = () -> {
            currentPage[0] = 1;
            applyFilter.run();
        };

        search.textProperty().addListener((obs, oldVal, newVal) -> onFilterChange.run());
        typeFilter.valueProperty().addListener((obs, oldVal, newVal) -> onFilterChange.run());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> onFilterChange.run());
        searchBtn.setOnAction(e -> onFilterChange.run());

        paginationBar.getPageSizeCombo().setOnAction(e -> {
            Integer selectedSize = paginationBar.getPageSizeCombo().getValue();
            if (selectedSize != null && selectedSize != currentPageSize[0]) {
                currentPageSize[0] = selectedSize;
                currentPage[0] = 1;
                applyFilter.run();
            }
        });

        TableColumn<Promotion, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(70);
        idCol.setPrefWidth(80);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().id)));
        idCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER_LEFT; -fx-font-size: 13.5px; -fx-text-fill: #FFFFFF;");

        TableColumn<Promotion, String> codeCol = new TableColumn<>("Mã voucher");
        codeCol.setMinWidth(130);
        codeCol.setPrefWidth(140);
        codeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().code != null ? d.getValue().code : "—"));
        codeCol.setStyle("-fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 13.5px;");

        TableColumn<Promotion, String> nameCol = new TableColumn<>("Tên chương trình");
        nameCol.setMinWidth(220);
        nameCol.setPrefWidth(240);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name));
        nameCol.setStyle("-fx-font-weight: 800; -fx-font-size: 13.5px;");

        TableColumn<Promotion, String> typeCol = new TableColumn<>("Hình thức");
        typeCol.setMinWidth(130);
        typeCol.setPrefWidth(140);
        typeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().discountTypeDisplay()));
        typeCol.setStyle("-fx-font-weight: 600; -fx-text-fill: #CBD5E1;");

        TableColumn<Promotion, String> valCol = new TableColumn<>("Mức giảm");
        valCol.setMinWidth(120);
        valCol.setPrefWidth(130);
        valCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().discountValueDisplay()));
        valCol.setStyle("-fx-font-weight: 800; -fx-text-fill: #10B981; -fx-font-size: 13.5px;");

        TableColumn<Promotion, String> startCol = new TableColumn<>("Ngày bắt đầu");
        startCol.setMinWidth(115);
        startCol.setPrefWidth(125);
        startCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().startDate != null ? d.getValue().startDate.format(DATE_FMT) : "—"));
        startCol.setStyle("-fx-font-size: 13.5px;");

        TableColumn<Promotion, String> endCol = new TableColumn<>("Ngày kết thúc");
        endCol.setMinWidth(115);
        endCol.setPrefWidth(125);
        endCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().endDate != null ? d.getValue().endDate.format(DATE_FMT) : "—"));
        endCol.setStyle("-fx-font-size: 13.5px;");

        TableColumn<Promotion, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setMinWidth(135);
        statusCol.setPrefWidth(145);
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().statusDisplay()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Promotion p = getTableView().getItems().get(getIndex());
                    Label badge = new Label(item);
                    badge.getStyleClass().addAll("badge", p.badgeClass());
                    badge.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 12;");
                    setGraphic(badge);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Promotion, Void> actionCol = new TableColumn<>("Thao tác");
        actionCol.setMinWidth(180);
        actionCol.setPrefWidth(190);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Sửa");
            private final Button delBtn = new Button("Xóa");
            private final HBox box = new HBox(8, editBtn, delBtn);

            {
                editBtn.getStyleClass().add("btn-action-edit");
                editBtn.setGraphic(IconUtils.icon("edit", 12, Color.WHITE));
                editBtn.setGraphicTextGap(6);
                editBtn.setMinWidth(68);
                editBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");

                delBtn.getStyleClass().add("btn-danger");
                delBtn.setGraphic(IconUtils.icon("trash", 12, Color.WHITE));
                delBtn.setGraphicTextGap(6);
                delBtn.setMinWidth(68);
                delBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");

                box.setAlignment(Pos.CENTER_LEFT);
                setAlignment(Pos.CENTER_LEFT);

                editBtn.setOnAction(e -> {
                    Promotion p = getTableView().getItems().get(getIndex());
                    openPromotionDialog(p, () -> reload(masterData, applyFilter));
                });

                delBtn.setOnAction(e -> {
                    Promotion p = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận xóa khuyến mãi");
                    alert.setHeaderText("Xóa khuyến mãi " + p.name + " (" + p.code + ")?");
                    alert.setContentText("Hành động này sẽ ngưng áp dụng chương trình khuyến mãi.");
                    alert.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt -> {
                        Util.runAsyncVoid(() -> ApiService.deletePromotion(p.id), () -> reload(masterData, applyFilter));
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(idCol, codeCol, nameCol, typeCol, valCol, startCol, endCol, statusCol, actionCol);

        // Reload logic
        Runnable loadAction = () -> reload(masterData, applyFilter);
        loadAction.run();

        refreshBtn.setOnAction(e -> {
            search.clear();
            typeFilter.setValue("Tất cả hình thức");
            statusFilter.setValue("Tất cả trạng thái");
            loadAction.run();
        });

        addBtn.setOnAction(e -> openPromotionDialog(null, loadAction));

        card.getChildren().addAll(headerRow, toolbar, table, paginationBar);
        root.setCenter(card);
        return root;
    }

    private static void reload(ObservableList<Promotion> masterData, Runnable applyFilter) {
        Util.runAsync(ApiService::promotions, list -> {
            masterData.setAll(list);
            applyFilter.run();
        });
    }

    private static void openPromotionDialog(Promotion existing, Runnable reload) {
        boolean isEdit = existing != null;
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Cập nhật chương trình khuyến mãi" : "Thêm mới chương trình khuyến mãi");
        dialog.getDialogPane().getStylesheets().add(
                PromotionsView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());

        TextField code = new TextField(isEdit ? existing.code : "");
        code.setPromptText("Mã voucher áp dụng (VD: SUMMER2026, VIP15)");

        TextField name = new TextField(isEdit ? existing.name : "");
        name.setPromptText("Tên chương trình khuyến mãi (VD: Chào hè rực rỡ)");

        ComboBox<String> discountTypeBox = new ComboBox<>();
        discountTypeBox.getItems().addAll("Phần trăm (%)", "Số tiền (VNĐ)");
        discountTypeBox.setValue(isEdit && "SO_TIEN".equalsIgnoreCase(existing.discountType) ? "Số tiền (VNĐ)" : "Phần trăm (%)");
        discountTypeBox.setMaxWidth(Double.MAX_VALUE);

        TextField discountValue = new TextField(isEdit && existing.discountValue != null ? existing.discountValue.stripTrailingZeros().toPlainString() : "10");
        discountValue.setPromptText("Giá trị giảm (VD: 10 cho 10%, hoặc 100000 cho 100.000đ)");

        DatePicker startDate = new DatePicker(isEdit && existing.startDate != null ? existing.startDate : LocalDate.now());
        DatePicker endDate = new DatePicker(isEdit && existing.endDate != null ? existing.endDate : LocalDate.now().plusMonths(1));
        startDate.setMaxWidth(Double.MAX_VALUE);
        endDate.setMaxWidth(Double.MAX_VALUE);

        CheckBox activeCb = new CheckBox("Kích hoạt áp dụng ngay");
        activeCb.setSelected(!isEdit || (existing.active != null && existing.active));
        activeCb.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: 600;");

        VBox form = new VBox(10,
                labeledBox("Mã voucher (*)", code),
                labeledBox("Tên chương trình khuyến mãi (*)", name),
                new HBox(12,
                        wrapFlex(labeledBox("Hình thức giảm (*)", discountTypeBox)),
                        wrapFlex(labeledBox("Mức giảm (*)", discountValue))
                ),
                new HBox(12,
                        wrapFlex(labeledBox("Ngày bắt đầu (*)", startDate)),
                        wrapFlex(labeledBox("Ngày kết thúc (*)", endDate))
                ),
                activeCb
        );
        form.setPadding(new Insets(16));
        form.setPrefWidth(460);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String codeVal = code.getText().trim().toUpperCase();
                if (codeVal.isBlank()) {
                    Util.error("Mã voucher không được để trống.");
                    return null;
                }
                String nameVal = name.getText().trim();
                if (nameVal.isBlank()) {
                    Util.error("Tên chương trình khuyến mãi không được để trống.");
                    return null;
                }
                BigDecimal val;
                try {
                    val = new BigDecimal(discountValue.getText().trim().replaceAll("[^0-9.]", ""));
                    if (val.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException();
                } catch (Exception ex) {
                    Util.error("Mức giảm phải là số dương hợp lệ.");
                    return null;
                }

                String typeVal = "Số tiền (VNĐ)".equals(discountTypeBox.getValue()) ? "SO_TIEN" : "PHAN_TRAM";
                if ("PHAN_TRAM".equals(typeVal) && val.compareTo(BigDecimal.valueOf(100)) > 0) {
                    Util.error("Giảm theo phần trăm không được vượt quá 100%.");
                    return null;
                }

                LocalDate startVal = startDate.getValue();
                LocalDate endVal = endDate.getValue();
                if (startVal == null || endVal == null) {
                    Util.error("Vui lòng chọn ngày bắt đầu và kết thúc.");
                    return null;
                }
                if (endVal.isBefore(startVal)) {
                    Util.error("Ngày kết thúc không được trước ngày bắt đầu.");
                    return null;
                }

                boolean act = activeCb.isSelected();

                if (isEdit) {
                    Util.runAsync(() -> ApiService.updatePromotion(existing.id, codeVal, nameVal, typeVal, val, startVal, endVal, act),
                            p -> reload.run());
                } else {
                    Util.runAsync(() -> ApiService.createPromotion(codeVal, nameVal, typeVal, val, startVal, endVal, act),
                            p -> reload.run());
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

    private static VBox wrapFlex(VBox box) {
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }
}
