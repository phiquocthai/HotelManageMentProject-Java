package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.model.Attendance;
import com.lotusstay.javafx.net.ApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Giao diện Bảng chấm công — Chuẩn Dark Luxury Theme:
 * Nền than chì #141A24, chữ trắng sáng #FFFFFF, badge trạng thái sắc nét trên nền tối.
 */
public class AttendanceView {

    public static BorderPane build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: transparent;");

        VBox card = new VBox(20);
        card.getStyleClass().add("panel");
        VBox.setVgrow(card, Priority.ALWAYS);

        // ── Header row ────────────────────────────────────────────────────
        VBox titleBox = new VBox(4);
        Label title = new Label("Bảng chấm công");
        title.getStyleClass().add("page-heading");

        Label sub = new Label("Ghi nhận thời gian vào/ra thực tế, đối soát giờ làm và quản lý tình trạng đủ công, đi trễ, vắng mặt");
        sub.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(title, sub);

        // ── Table ─────────────────────────────────────────────────────────
        ObservableList<Attendance> data = FXCollections.observableArrayList();

        TableView<Attendance> table = new TableView<>(data);
        table.getStyleClass().add("table-view");
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setPlaceholder(new Label("Chưa có dữ liệu chấm công nào."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Attendance, String> idCol = new TableColumn<>("MÃ CC");
        idCol.setMinWidth(90);
        idCol.prefWidthProperty().bind(table.widthProperty().multiply(0.09));
        idCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().id != null ? String.format("CC%03d", d.getValue().id) : "—"));
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

        TableColumn<Attendance, String> staffCol = new TableColumn<>("NHÂN VIÊN");
        staffCol.setCellValueFactory(d -> {
            if (d.getValue().shiftAssignment != null && d.getValue().shiftAssignment.staff != null)
                return new SimpleStringProperty(d.getValue().shiftAssignment.staff.fullName);
            return new SimpleStringProperty("—");
        });
        staffCol.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
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

        TableColumn<Attendance, String> shiftCol = new TableColumn<>("CA LÀM");
        shiftCol.setCellValueFactory(d -> {
            if (d.getValue().shiftAssignment != null && d.getValue().shiftAssignment.workShift != null)
                return new SimpleStringProperty(d.getValue().shiftAssignment.workShift.shiftName);
            return new SimpleStringProperty("—");
        });
        shiftCol.prefWidthProperty().bind(table.widthProperty().multiply(0.16));
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

        TableColumn<Attendance, String> dateCol = new TableColumn<>("NGÀY");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().shiftAssignment != null ? d.getValue().shiftAssignment.workDate : "—"));
        dateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        dateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 14px; -fx-text-fill: #CBD5E1; -fx-font-weight: 600; -fx-padding: 11 14;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Attendance, String> inCol = new TableColumn<>("GIỜ VÀO");
        inCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().checkInTime != null ? d.getValue().checkInTime : "—"));
        inCol.prefWidthProperty().bind(table.widthProperty().multiply(0.12));
        inCol.setCellFactory(col -> new TableCell<>() {
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

        TableColumn<Attendance, String> outCol = new TableColumn<>("GIỜ RA");
        outCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().checkOutTime != null ? d.getValue().checkOutTime : "—"));
        outCol.prefWidthProperty().bind(table.widthProperty().multiply(0.12));
        outCol.setCellFactory(col -> new TableCell<>() {
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

        TableColumn<Attendance, String> statusCol = new TableColumn<>("TRẠNG THÁI");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status));
        statusCol.prefWidthProperty().bind(table.widthProperty().multiply(0.11));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                Label badge = new Label(statusLabel(item));
                badge.getStyleClass().add("badge");
                badge.getStyleClass().add(statusBadgeClass(item));
                badge.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 12;");
                setGraphic(badge);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setStyle("-fx-padding: 6 14;");
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        // Cột Ghi chú (Entity field: ghi_chu / note)
        TableColumn<Attendance, String> noteCol = new TableColumn<>("GHI CHÚ");
        noteCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));
        noteCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().note != null && !d.getValue().note.isBlank() ? d.getValue().note : "—"));
        noteCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13.5px; -fx-text-fill: #94A3B8; -fx-padding: 11 14;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Attendance, Void> actionCol = new TableColumn<>("THAO TÁC");
        actionCol.setMinWidth(120);
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.12));

        table.getColumns().addAll(idCol, staffCol, shiftCol, dateCol, inCol, outCol, statusCol, noteCol, actionCol);

        // ── Reload ────────────────────────────────────────────────────────
        Runnable reload = () -> Util.runAsync(ApiService::attendances, data::setAll);

        // ── Action cell ───────────────────────────────────────────────────
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Sửa");

            {
                editBtn.getStyleClass().add("btn-action-edit");
                editBtn.setGraphic(IconUtils.icon("edit", 12, javafx.scene.paint.Color.WHITE));
                editBtn.setGraphicTextGap(6);
                editBtn.setMinWidth(68);
                editBtn.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 4 11; -fx-background-color: #1E2532; -fx-border-color: #334155; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #FFFFFF; -fx-cursor: hand; -fx-min-width: 68px;");
                setStyle("-fx-padding: 6 14;");
                setAlignment(Pos.CENTER_LEFT);

                editBtn.setOnAction(e -> {
                    Attendance att = getTableView().getItems().get(getIndex());
                    openEditDialog(att, reload);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editBtn);
            }
        });

        reload.run();

        card.getChildren().addAll(titleBox, table);
        root.setCenter(card);
        return root;
    }

    // ── Dialog sửa chấm công (Dark Modal) ─────────────────────────────────
    private static void openEditDialog(Attendance att, Runnable reload) {
        String staffName  = (att.shiftAssignment != null && att.shiftAssignment.staff != null)
                ? att.shiftAssignment.staff.fullName : "—";
        String shiftName  = (att.shiftAssignment != null && att.shiftAssignment.workShift != null)
                ? att.shiftAssignment.workShift.shiftName : "—";
        String workDate   = att.shiftAssignment != null ? att.shiftAssignment.workDate : "—";

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("LotusStay — Cập nhật chấm công");
        applyTheme(dialog);

        TextField checkInField  = new TextField(att.checkInTime != null  ? att.checkInTime  : "");
        checkInField.setPromptText("HH:mm (VD: 06:00)");
        checkInField.setPrefHeight(38);

        TextField checkOutField = new TextField(att.checkOutTime != null ? att.checkOutTime : "");
        checkOutField.setPromptText("HH:mm hoặc để trống");
        checkOutField.setPrefHeight(38);

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("DU_CONG", "DI_TRE", "VE_SOM", "VANG_MAT");
        statusBox.setValue(att.status != null ? att.status : "DU_CONG");
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

        TextField noteField = new TextField(att.note != null ? att.note : "");
        noteField.setPromptText("Ghi chú chấm công...");
        noteField.setPrefHeight(38);

        VBox modalContent = new VBox(16);
        modalContent.setPadding(new Insets(24));
        modalContent.setPrefWidth(480);
        modalContent.setStyle("-fx-background-color: #141A24;");

        HBox modalHeader = new HBox(12);
        modalHeader.setAlignment(Pos.CENTER_LEFT);

        Label iconBadge = new Label("⏱️");
        iconBadge.setStyle("-fx-font-size: 20px; -fx-background-color: rgba(245, 158, 11, 0.18); -fx-padding: 8 12; -fx-background-radius: 10;");

        VBox titleArea = new VBox(3);
        Label hTitle = new Label("Cập nhật: " + staffName);
        hTitle.setStyle("-fx-font-family: 'Montserrat', sans-serif; -fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");
        Label hSub = new Label(shiftName + " • Ngày: " + workDate);
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
                String ci   = checkInField.getText().trim();
                String co   = checkOutField.getText().trim().isEmpty() ? null : checkOutField.getText().trim();
                String stat = statusBox.getValue();
                String note = noteField.getText().trim();
                Util.runAsync(() -> ApiService.updateAttendance(att.id, ci, co, stat, note), a -> reload.run());
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static String statusLabel(String s) {
        if (s == null) return "—";
        return switch (s) {
            case "DU_CONG"  -> "ĐỦ CÔNG";
            case "DI_TRE"   -> "ĐI TRỄ";
            case "VE_SOM"   -> "VỀ SỚM";
            case "VANG_MAT" -> "VẮNG MẶT";
            default -> s;
        };
    }

    private static String statusBadgeClass(String s) {
        if (s == null) return "badge-trong";
        return switch (s) {
            case "DU_CONG"  -> "badge-dang_o";
            case "DI_TRE"   -> "badge-orange";
            case "VE_SOM"   -> "badge-bao_tri";
            case "VANG_MAT" -> "badge-huy";
            default -> "badge-trong";
        };
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
                AttendanceView.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());
    }
}
