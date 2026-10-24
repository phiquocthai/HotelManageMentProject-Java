package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.ShiftAssignment;
import com.lotusstay.javafx.model.Staff;
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

import java.util.List;

/**
 * Giao diện Phân ca làm việc — Chuẩn Dark Luxury Theme theo đúng ảnh thiết kế:
 * Nền đen than chì (#0A0D14 & #141A24), chữ trắng sáng tương phản cao (#FFFFFF),
 * Nút '+ Phân ca mới' màu vàng kim (#F59E0B), nút 'Chấm công' và 'Xóa' đồng bộ.
 */
public class ShiftAssignmentView {

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
        Label title = new Label("Phân ca làm việc");
        title.getStyleClass().add("page-heading");

        Label sub = new Label("Lịch phân công nhân sự trực ca theo ngày, điều phối ca sáng, chiều, đêm linh hoạt");
        sub.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(title, sub);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button addBtn = new Button("Phân ca mới");
        addBtn.setGraphic(IconUtils.icon("plus", 14, Color.web("#0A0D14")));
        addBtn.setGraphicTextGap(8);
        addBtn.getStyleClass().add("btn-gold");
        addBtn.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 13.5px; -fx-font-weight: 800; -fx-padding: 9 22; -fx-background-color: #F59E0B; -fx-text-fill: #0A0D14; -fx-background-radius: 8; -fx-cursor: hand;");

        headerRow.getChildren().addAll(titleBox, headerSpacer, addBtn);

        // ── Table ─────────────────────────────────────────────────────────
        ObservableList<ShiftAssignment> data = FXCollections.observableArrayList();

        TableView<ShiftAssignment> table = new TableView<>(data);
        table.getStyleClass().add("table-view");
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có dữ liệu phân ca nào."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ShiftAssignment, String> idCol = new TableColumn<>("MÃ PC");
        idCol.setMinWidth(90);
        idCol.prefWidthProperty().bind(table.widthProperty().multiply(0.12));
        idCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().id != null ? String.format("PC%03d", d.getValue().id) : "—"));
        idCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-weight: 800; -fx-text-fill: #F59E0B; -fx-font-size: 14px; -fx-padding: 11 14;");
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<ShiftAssignment, String> staffCol = new TableColumn<>("NHÂN VIÊN");
        staffCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().staff != null ? d.getValue().staff.fullName : "—"));
        staffCol.prefWidthProperty().bind(table.widthProperty().multiply(0.28));
        staffCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-weight: 800; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 11 14;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<ShiftAssignment, String> shiftCol = new TableColumn<>("CA LÀM");
        shiftCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().workShift != null ? d.getValue().workShift.shiftName : "—"));
        shiftCol.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        shiftCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 14px; -fx-text-fill: #F59E0B; -fx-font-weight: 700; -fx-padding: 11 14;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<ShiftAssignment, String> dateCol = new TableColumn<>("NGÀY LÀM VIỆC");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().workDate));
        dateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        dateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 14px; -fx-text-fill: #F1F5F9; -fx-font-weight: 600; -fx-padding: 11 14;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<ShiftAssignment, Void> actionCol = new TableColumn<>("THAO TÁC");
        actionCol.setMinWidth(215);
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.24));

        table.getColumns().addAll(idCol, staffCol, shiftCol, dateCol, actionCol);

        // ── Reload ────────────────────────────────────────────────────────
        Runnable reload = () -> Util.runAsync(ApiService::shiftAssignments, data::setAll);

        // ── Action cell ───────────────────────────────────────────────────
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button chamCongBtn = new Button("Chấm công");
            private final Button delBtn      = new Button("Xóa");
            private final HBox box = new HBox(8, chamCongBtn, delBtn);

            {
                chamCongBtn.getStyleClass().add("btn-action-edit");
                delBtn.getStyleClass().add("btn-action-del");

                chamCongBtn.setGraphic(IconUtils.icon("attendance", 12, Color.WHITE));
                chamCongBtn.setGraphicTextGap(6);
                chamCongBtn.setMinWidth(108);
                chamCongBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 108px;");

                delBtn.setGraphic(IconUtils.icon("trash", 12, Color.web("#F87171")));
                delBtn.setGraphicTextGap(6);
                delBtn.setMinWidth(68);
                delBtn.setStyle("-fx-padding: 4 11; -fx-font-size: 12px; -fx-font-weight: 600; -fx-min-width: 68px;");

                box.setAlignment(Pos.CENTER_LEFT);
                setStyle("-fx-padding: 6 14;");
                setAlignment(Pos.CENTER_LEFT);

                chamCongBtn.setOnAction(e -> {
                    ShiftAssignment sa = getTableView().getItems().get(getIndex());
                    openAttendanceDialog(sa, reload);
                });

                delBtn.setOnAction(e -> {
                    ShiftAssignment sa = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Xóa phân ca của nhân viên \"" + (sa.staff != null ? sa.staff.fullName : "")
                                    + "\" vào ngày " + sa.workDate + "?");
                    confirm.setHeaderText("Xác nhận xóa phân ca");
                    confirm.setTitle("LotusStay — Xác nhận");
                    applyTheme(confirm);
                    confirm.showAndWait()
                            .filter(bt -> bt == ButtonType.OK)
                            .ifPresent(bt -> Util.runAsyncVoid(
                                    () -> ApiService.deleteShiftAssignment(sa.id), reload));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        addBtn.setOnAction(e -> openAddDialog(reload));
        reload.run();

        card.getChildren().addAll(headerRow, table);
        root.setCenter(card);
        return root;
    }

    // ── Dialog phân ca mới (Dark Modal) ──────────────────────────────────
    private static void openAddDialog(Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("LotusStay — Phân ca làm việc");
        applyTheme(dialog);

        List<Staff> staffList = ApiService.staff();
        List<WorkShift> shiftList = ApiService.workShifts();

        ComboBox<Staff> staffBox = new ComboBox<>();
        staffBox.getItems().addAll(staffList);
        staffBox.setPrefHeight(38);
        staffBox.setMaxWidth(Double.MAX_VALUE);
        staffBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Staff s) { return s == null ? "" : s.fullName + " (" + s.username + ")"; }
            public Staff fromString(String s) { return null; }
        });
        if (!staffList.isEmpty()) staffBox.setValue(staffList.get(0));

        ComboBox<WorkShift> shiftBox = new ComboBox<>();
        shiftBox.getItems().addAll(shiftList);
        shiftBox.setPrefHeight(38);
        shiftBox.setMaxWidth(Double.MAX_VALUE);
        shiftBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(WorkShift w) { return w == null ? "" : w.shiftName + " (" + w.startTime + " - " + w.endTime + ")"; }
            public WorkShift fromString(String s) { return null; }
        });
        if (!shiftList.isEmpty()) shiftBox.setValue(shiftList.get(0));

        TextField dateField = new TextField();
        dateField.setPromptText("dd/MM/yyyy (VD: 22/10/2026)");
        dateField.setPrefHeight(38);
        dateField.setText(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        VBox modalContent = new VBox(16);
        modalContent.setPadding(new Insets(24));
        modalContent.setPrefWidth(480);
        modalContent.setStyle("-fx-background-color: #141A24;");

        HBox modalHeader = new HBox(12);
        modalHeader.setAlignment(Pos.CENTER_LEFT);

        Label iconBadge = new Label("📋");
        iconBadge.setStyle("-fx-font-size: 20px; -fx-background-color: rgba(245, 158, 11, 0.18); -fx-padding: 8 12; -fx-background-radius: 10;");

        VBox titleArea = new VBox(3);
        Label hTitle = new Label("Phân ca làm việc mới");
        hTitle.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");
        Label hSub = new Label("Gán ca trực và ngày làm việc cho nhân viên");
        hSub.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-text-fill: #94A3B8;");
        titleArea.getChildren().addAll(hTitle, hSub);
        modalHeader.getChildren().addAll(iconBadge, titleArea);

        VBox formBody = new VBox(14);
        formBody.getChildren().addAll(
                buildFormField("Nhân viên phụ trách (*)", staffBox),
                buildFormField("Ca làm việc (*)", shiftBox),
                buildFormField("Ngày làm việc (*)", dateField)
        );

        modalContent.getChildren().addAll(modalHeader, new Separator(), formBody);
        dialog.getDialogPane().setContent(modalContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okBtn != null) {
            okBtn.setText("Lưu phân ca");
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
                Staff s = staffBox.getValue();
                WorkShift w = shiftBox.getValue();
                String date = dateField.getText().trim();
                if (s == null || w == null || date.isEmpty()) {
                    Util.info("Vui lòng chọn đầy đủ nhân viên, ca làm và ngày làm việc.");
                    return null;
                }
                Util.runAsync(() -> ApiService.createShiftAssignment(s.id, w.id, date), sa -> reload.run());
            }
            return null;
        });
        dialog.showAndWait();
    }

    // ── Dialog Chấm công nhanh (Dark Modal) ───────────────────────────────
    private static void openAttendanceDialog(ShiftAssignment sa, Runnable reload) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("LotusStay — Chấm công ca trực");
        applyTheme(dialog);

        TextField checkInField  = new TextField();
        checkInField.setPromptText("HH:mm (VD: 06:00)");
        checkInField.setPrefHeight(38);

        TextField checkOutField = new TextField();
        checkOutField.setPromptText("HH:mm hoặc để trống nếu chưa ra");
        checkOutField.setPrefHeight(38);

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("DU_CONG", "DI_TRE", "VE_SOM", "VANG_MAT");
        statusBox.setValue("DU_CONG");
        statusBox.setPrefHeight(38);
        statusBox.setMaxWidth(Double.MAX_VALUE);
        statusBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(String s) {
                if (s == null) return "";
                return switch (s) {
                    case "DU_CONG"  -> "Đủ công";
                    case "DI_TRE"   -> "Đi trễ";
                    case "VE_SOM"   -> "Về sớm";
                    case "VANG_MAT" -> "Vắng mặt";
                    default -> s;
                };
            }
            public String fromString(String s) { return s; }
        });

        TextField noteField = new TextField();
        noteField.setPromptText("Ghi chú chấm công (tuỳ chọn)");
        noteField.setPrefHeight(38);

        if (sa.attendance != null) {
            if (sa.attendance.checkInTime != null)  checkInField.setText(sa.attendance.checkInTime);
            if (sa.attendance.checkOutTime != null) checkOutField.setText(sa.attendance.checkOutTime);
            if (sa.attendance.status != null) statusBox.setValue(sa.attendance.status);
            if (sa.attendance.note != null) noteField.setText(sa.attendance.note);
        } else if (sa.workShift != null) {
            checkInField.setText(sa.workShift.startTime);
            checkOutField.setText(sa.workShift.endTime);
        }

        VBox modalContent = new VBox(16);
        modalContent.setPadding(new Insets(24));
        modalContent.setPrefWidth(500);
        modalContent.setStyle("-fx-background-color: #141A24;");

        HBox modalHeader = new HBox(12);
        modalHeader.setAlignment(Pos.CENTER_LEFT);

        Label iconBadge = new Label("⏱️");
        iconBadge.setStyle("-fx-font-size: 20px; -fx-background-color: rgba(245, 158, 11, 0.18); -fx-padding: 8 12; -fx-background-radius: 10;");

        VBox titleArea = new VBox(3);
        String empName = sa.staff != null ? sa.staff.fullName : "Nhân viên";
        String shiftName = sa.workShift != null ? sa.workShift.shiftName : "Ca làm";
        Label hTitle = new Label("Chấm công: " + empName);
        hTitle.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");
        Label hSub = new Label(shiftName + " • Ngày: " + sa.workDate);
        hSub.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px; -fx-text-fill: #F59E0B; -fx-font-weight: 600;");
        titleArea.getChildren().addAll(hTitle, hSub);
        modalHeader.getChildren().addAll(iconBadge, titleArea);

        VBox formBody = new VBox(12);
        formBody.getChildren().addAll(
                buildFormField("Giờ vào thực tế (*)", checkInField),
                buildFormField("Giờ ra thực tế", checkOutField),
                buildFormField("Trạng thái công (*)", statusBox),
                buildFormField("Ghi chú", noteField)
        );

        modalContent.getChildren().addAll(modalHeader, new Separator(), formBody);
        dialog.getDialogPane().setContent(modalContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okBtn != null) {
            okBtn.setText("Xác nhận chấm công");
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
                String ci   = checkInField.getText().trim();
                String co   = checkOutField.getText().trim().isEmpty() ? null : checkOutField.getText().trim();
                String stat = statusBox.getValue();
                String note = noteField.getText().trim();
                if (sa.attendance != null) {
                    Util.runAsync(() -> ApiService.updateAttendance(sa.attendance.id, ci, co, stat, note), a -> reload.run());
                } else {
                    Util.runAsync(() -> ApiService.createAttendance(sa.id, ci, co, stat, note), a -> reload.run());
                }
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
                ShiftAssignmentView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());
    }
}
