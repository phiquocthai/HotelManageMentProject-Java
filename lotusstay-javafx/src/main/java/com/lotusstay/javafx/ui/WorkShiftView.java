package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.WorkShift;
import com.lotusstay.javafx.net.ApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Giao diện Ca làm việc — Chuẩn Dark Luxury Theme (Nền đen - than chì, chữ trắng sáng, điểm nhấn vàng kim):
 * Montserrat (Tiêu đề, Heading) & Inter (Bảng biểu, Dữ liệu).
 * Nút Sửa / Xóa tinh tế, chuẩn nét tương phản cao.
 */
public class WorkShiftView {

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: transparent;");

        VBox card = new VBox(20);
        card.getStyleClass().add("panel");
        VBox.setVgrow(card, Priority.ALWAYS);

        // ── Header row ────────────────────────────────────────────────────
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Ca làm việc");
        title.getStyleClass().add("page-heading");

        Label sub = new Label("Quản lý danh mục các ca trực định kỳ, quy định giờ bắt đầu và kết thúc trong khách sạn");
        sub.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(title, sub);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button addBtn = new Button("Thêm ca mới");
        addBtn.setGraphic(IconUtils.icon("plus", 14, Color.web("#0A0D14")));
        addBtn.setGraphicTextGap(8);
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setMinWidth(Region.USE_PREF_SIZE);
        addBtn.setPrefWidth(160);
        addBtn.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 13.5px; -fx-font-weight: 800; -fx-padding: 9 22; -fx-background-color: #F59E0B; -fx-text-fill: #0A0D14; -fx-background-radius: 8; -fx-cursor: hand;");

        headerRow.getChildren().addAll(titleBox, headerSpacer, addBtn);

        // ── Table ─────────────────────────────────────────────────────────
        ObservableList<WorkShift> data = FXCollections.observableArrayList();

        TableView<WorkShift> table = new TableView<>(data);
        table.getStyleClass().add("table-view");
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có ca làm việc nào."));

        TableColumn<WorkShift, String> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(70);
        idCol.setPrefWidth(80);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().id != null ? String.valueOf(d.getValue().id) : "—"));
        idCol.setStyle("-fx-font-weight: 800; -fx-alignment: CENTER_LEFT;");
        idCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 13.5px; -fx-alignment: CENTER_LEFT;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<WorkShift, String> nameCol = new TableColumn<>("Tên ca");
        nameCol.setMinWidth(200);
        nameCol.setPrefWidth(240);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().shiftName));
        nameCol.setStyle("-fx-font-weight: 700; -fx-alignment: CENTER_LEFT;");
        nameCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-weight: 800; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<WorkShift, String> startCol = new TableColumn<>("Giờ bắt đầu");
        startCol.setMinWidth(150);
        startCol.setPrefWidth(180);
        startCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().startTime));
        startCol.setStyle("-fx-alignment: CENTER_LEFT;");
        startCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 14px; -fx-text-fill: #F1F5F9; -fx-font-weight: 600; -fx-alignment: CENTER_LEFT;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<WorkShift, String> endCol = new TableColumn<>("Giờ kết thúc");
        endCol.setMinWidth(150);
        endCol.setPrefWidth(180);
        endCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().endTime));
        endCol.setStyle("-fx-alignment: CENTER_LEFT;");
        endCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 14px; -fx-text-fill: #F1F5F9; -fx-font-weight: 600; -fx-alignment: CENTER_LEFT;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        // Cột Trạng thái (Entity field: dang_hoat_dong / active)
        TableColumn<WorkShift, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setMinWidth(180);
        statusCol.setPrefWidth(200);
        statusCol.setStyle("-fx-alignment: CENTER_LEFT;");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().active ? "Đang áp dụng" : "Tạm ngưng"));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    boolean act = "Đang áp dụng".equalsIgnoreCase(item);
                    Label badge = new Label(item);
                    badge.getStyleClass().addAll("badge", act ? "badge-trong" : "badge-bao_tri");
                    badge.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 14; -fx-min-width: 120px;");
                    badge.setMinWidth(Region.USE_PREF_SIZE);
                    badge.setPrefWidth(Region.USE_COMPUTED_SIZE);
                    badge.setTextOverrun(OverrunStyle.CLIP);
                    setGraphic(badge);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<WorkShift, Void> actionCol = new TableColumn<>("Thao tác");
        actionCol.setMinWidth(180);
        actionCol.setPrefWidth(200);
        actionCol.setStyle("-fx-alignment: CENTER_LEFT;");

        table.getColumns().addAll(idCol, nameCol, startCol, endCol, statusCol, actionCol);

        // ── Reload ────────────────────────────────────────────────────────
        Runnable reload = () -> Util.runAsync(ApiService::workShifts, data::setAll);

        // ── Action cell ───────────────────────────────────────────────────
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Sửa");
            private final Button delBtn  = new Button("Xóa");
            private final HBox box = new HBox(8, editBtn, delBtn);

            {
                editBtn.getStyleClass().add("btn-action-edit");
                delBtn.getStyleClass().add("btn-action-del");

                // Thêm vector icon Bootstrap
                editBtn.setGraphic(IconUtils.icon("edit", 12, Color.WHITE));
                editBtn.setGraphicTextGap(6);
                editBtn.setMinWidth(68);
                editBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");

                delBtn.setGraphic(IconUtils.icon("trash", 12, Color.WHITE));
                delBtn.setGraphicTextGap(6);
                delBtn.setMinWidth(68);
                delBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");

                box.setAlignment(Pos.CENTER_LEFT);
                setStyle("-fx-padding: 6 14;");
                setAlignment(Pos.CENTER_LEFT);

                editBtn.setOnAction(e -> {
                    WorkShift ws = getTableView().getItems().get(getIndex());
                    openEditDialog(ws, reload);
                });

                delBtn.setOnAction(e -> {
                    WorkShift ws = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Bạn có chắc chắn muốn xóa ca \"" + ws.shiftName + "\" (" + ws.startTime + " - " + ws.endTime + ")?");
                    confirm.setHeaderText("Xác nhận xóa ca làm việc");
                    confirm.setTitle("LotusStay — Xác nhận");
                    applyTheme(confirm);
                    confirm.showAndWait().ifPresent(bt -> {
                        if (bt == ButtonType.OK) {
                            Util.runAsyncVoid(() -> ApiService.deleteWorkShift(ws.id), reload);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });

        addBtn.setOnAction(e -> openAddDialog(reload));
        reload.run();

        card.getChildren().addAll(headerRow, table);
        root.setCenter(card);
        return root;
    }

    // ── Dialog Thêm ca (Modern Dark Modal) ───────────────────────────────
    private static void openAddDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("LotusStay — Thêm ca làm việc");
        applyTheme(dialog);

        TextField nameField  = new TextField();
        nameField.setPromptText("VD: Ca sáng, Ca chiều, Ca đêm...");
        nameField.setPrefHeight(38);

        TextField startField = new TextField();
        startField.setPromptText("HH:mm (VD: 06:00)");
        startField.setPrefHeight(38);

        TextField endField   = new TextField();
        endField.setPromptText("HH:mm (VD: 14:00)");
        endField.setPrefHeight(38);

        VBox modalContent = new VBox(16);
        modalContent.setPadding(new Insets(24));
        modalContent.setPrefWidth(480);
        modalContent.setStyle("-fx-background-color: #141A24;");

        HBox modalHeader = new HBox(12);
        modalHeader.setAlignment(Pos.CENTER_LEFT);

        Label iconBadge = new Label("🕒");
        iconBadge.setStyle("-fx-font-size: 20px; -fx-background-color: rgba(245, 158, 11, 0.18); -fx-padding: 8 12; -fx-background-radius: 10;");

        VBox titleArea = new VBox(3);
        Label hTitle = new Label("Thêm ca làm việc mới");
        hTitle.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");
        Label hSub = new Label("Định nghĩa khung giờ làm việc chuẩn cho khách sạn");
        hSub.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-text-fill: #94A3B8;");
        titleArea.getChildren().addAll(hTitle, hSub);
        modalHeader.getChildren().addAll(iconBadge, titleArea);

        // Form fields
        VBox formBody = new VBox(12);
        formBody.getChildren().addAll(
                buildFormField("Tên ca làm việc (*)", nameField),
                buildFormField("Giờ bắt đầu (*)", startField),
                buildFormField("Giờ kết thúc (*)", endField)
        );

        modalContent.getChildren().addAll(modalHeader, new Separator(), formBody);
        dialog.getDialogPane().setContent(modalContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okBtn != null) {
            okBtn.setText("Lưu ca làm việc");
            okBtn.getStyleClass().add("btn-gold");
            okBtn.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-weight: 800; -fx-background-color: #F59E0B; -fx-text-fill: #0A0D14;");
        }
        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelBtn != null) {
            cancelBtn.setText("Hủy bỏ");
            cancelBtn.setStyle("-fx-text-fill: #94A3B8;");
        }

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String name  = nameField.getText().trim();
                String start = startField.getText().trim();
                String end   = endField.getText().trim();
                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Util.info("Vui lòng nhập đầy đủ tên ca, giờ bắt đầu và giờ kết thúc.");
                    return null;
                }
                Util.runAsync(() -> ApiService.createWorkShift(name, start, end), w -> reload.run());
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ── Dialog Sửa ca (Modern Dark Modal) ───────────────────────────────
    private static void openEditDialog(WorkShift ws, Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("LotusStay — Cập nhật ca làm việc");
        applyTheme(dialog);

        TextField nameField  = new TextField(ws.shiftName);
        nameField.setPrefHeight(38);
        TextField startField = new TextField(ws.startTime);
        startField.setPrefHeight(38);
        TextField endField   = new TextField(ws.endTime);
        endField.setPrefHeight(38);

        VBox modalContent = new VBox(16);
        modalContent.setPadding(new Insets(24));
        modalContent.setPrefWidth(480);
        modalContent.setStyle("-fx-background-color: #141A24;");

        HBox modalHeader = new HBox(12);
        modalHeader.setAlignment(Pos.CENTER_LEFT);

        Label iconBadge = new Label("✏️");
        iconBadge.setStyle("-fx-font-size: 20px; -fx-background-color: rgba(245, 158, 11, 0.18); -fx-padding: 8 12; -fx-background-radius: 10;");

        VBox titleArea = new VBox(3);
        Label hTitle = new Label("Chỉnh sửa ca làm việc");
        hTitle.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");
        Label hSub = new Label("Thay đổi tên hoặc thời gian làm việc của " + ws.shiftName);
        hSub.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-text-fill: #94A3B8;");
        titleArea.getChildren().addAll(hTitle, hSub);
        modalHeader.getChildren().addAll(iconBadge, titleArea);

        VBox formBody = new VBox(12);
        formBody.getChildren().addAll(
                buildFormField("Tên ca làm việc (*)", nameField),
                buildFormField("Giờ bắt đầu (*)", startField),
                buildFormField("Giờ kết thúc (*)", endField)
        );

        modalContent.getChildren().addAll(modalHeader, new Separator(), formBody);
        dialog.getDialogPane().setContent(modalContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okBtn != null) {
            okBtn.setText("Lưu thay đổi");
            okBtn.getStyleClass().add("btn-gold");
            okBtn.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-weight: 800; -fx-background-color: #F59E0B; -fx-text-fill: #0A0D14;");
        }
        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelBtn != null) {
            cancelBtn.setText("Hủy bỏ");
            cancelBtn.setStyle("-fx-text-fill: #94A3B8;");
        }

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String name  = nameField.getText().trim();
                String start = startField.getText().trim();
                String end   = endField.getText().trim();
                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Util.info("Vui lòng nhập đầy đủ thông tin.");
                    return null;
                }
                Util.runAsync(() -> ApiService.updateWorkShift(ws.id, name, start, end), w -> reload.run());
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static VBox buildFormField(String labelText, Control inputControl) {
        VBox box = new VBox(6);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #E2E8F0;");
        box.getChildren().addAll(label, inputControl);
        return box;
    }

    private static void applyTheme(Dialog<?> d) {
        d.getDialogPane().getStylesheets().add(
                WorkShiftView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());
    }
}
