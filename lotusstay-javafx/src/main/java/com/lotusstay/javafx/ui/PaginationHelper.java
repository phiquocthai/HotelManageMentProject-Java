package com.lotusstay.javafx.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.function.Consumer;

/**
 * Component và tiện ích phân trang chuẩn giao diện LotusStay Dark Theme.
 * Cung cấp:
 *  - Lựa chọn kích thước trang: 5, 10, 15, 20
 *  - Nhãn đếm: "Hiển thị 1 – 10 trên tổng 14 ..."
 *  - Bộ nút chuyển trang: [‹] [1] [2] ... [›] với màu cam (#D97706) cho trang hiện tại.
 */
public class PaginationHelper {

    public static final List<Integer> PAGE_SIZES = List.of(5, 10, 15, 20);
    public static final int DEFAULT_PAGE_SIZE = 10;

    public static class PaginationBar extends HBox {
        private final Label recordInfoLabel = new Label();
        private final ComboBox<Integer> pageSizeCombo = new ComboBox<>(FXCollections.observableArrayList(PAGE_SIZES));
        private final HBox pageButtonsBox = new HBox(6);
        private final String entityName;

        public PaginationBar(String entityName) {
            this.entityName = entityName;
            setAlignment(Pos.CENTER_LEFT);
            setSpacing(16);
            setPadding(new Insets(12, 0, 4, 0));

            recordInfoLabel.setText("Hiển thị 0 " + entityName);
            recordInfoLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 13px; -fx-text-fill: #94A3B8; -fx-font-weight: 600;");

            Label sizeLabel = new Label("Số bản ghi / trang:");
            sizeLabel.setStyle("-fx-font-family: 'Inter', sans-serif; -fx-font-size: 12.5px; -fx-text-fill: #94A3B8; -fx-font-weight: 500;");

            pageSizeCombo.setValue(DEFAULT_PAGE_SIZE);
            pageSizeCombo.setStyle(
                    "-fx-background-color: #1E293B; -fx-border-color: rgba(148, 163, 184, 0.3); " +
                    "-fx-border-radius: 6; -fx-background-radius: 6; -fx-pref-height: 28px; -fx-font-size: 12px; -fx-cursor: hand;"
            );

            HBox leftBox = new HBox(16, recordInfoLabel, sizeLabel, pageSizeCombo);
            leftBox.setAlignment(Pos.CENTER_LEFT);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            pageButtonsBox.setAlignment(Pos.CENTER_RIGHT);

            getChildren().addAll(leftBox, spacer, pageButtonsBox);
        }

        public Label getRecordInfoLabel() {
            return recordInfoLabel;
        }

        public ComboBox<Integer> getPageSizeCombo() {
            return pageSizeCombo;
        }

        public HBox getPageButtonsBox() {
            return pageButtonsBox;
        }

        public void update(int currentPage, int pageSize, int totalElements, Consumer<Integer> onPageSelect) {
            update(currentPage, pageSize, (long) totalElements, onPageSelect);
        }

        public void update(int currentPage, int pageSize, long totalElements, Consumer<Integer> onPageSelect) {
            int totalPages = Math.max(1, (int) Math.ceil((double) totalElements / pageSize));
            int safePage = Math.min(Math.max(1, currentPage), totalPages);

            if (totalElements == 0) {
                recordInfoLabel.setText("Hiển thị 0 " + entityName);
            } else {
                long from = (safePage - 1) * pageSize + 1;
                long to = Math.min((long) safePage * pageSize, totalElements);
                recordInfoLabel.setText("Hiển thị " + from + " – " + to + " trên tổng " + totalElements + " " + entityName);
            }

            renderPageButtons(pageButtonsBox, safePage, totalPages, onPageSelect);
        }
    }

    public static void renderPageButtons(HBox box, int cur, int total, Consumer<Integer> onSelect) {
        box.getChildren().clear();

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

        Button next = new Button("›");
        next.setStyle(pageButtonStyle(false));
        next.setDisable(cur >= total);
        next.setOnAction(e -> onSelect.accept(cur + 1));
        box.getChildren().add(next);
    }

    private static Button createNumBtn(int page, boolean active, Consumer<Integer> onSelect) {
        Button b = new Button(String.valueOf(page));
        b.setStyle(pageButtonStyle(active));
        b.setOnAction(e -> {
            if (!active) onSelect.accept(page);
        });
        return b;
    }

    private static Label createEllipsis() {
        Label l = new Label("…");
        l.setStyle("-fx-text-fill: #64748B; -fx-padding: 0 4; -fx-font-weight: 700;");
        return l;
    }

    public static String pageButtonStyle(boolean active) {
        if (active) {
            return "-fx-background-color: #D97706; -fx-text-fill: #FFFFFF; " +
                    "-fx-font-weight: 700; -fx-min-width: 30px; -fx-min-height: 30px; " +
                    "-fx-background-radius: 6; -fx-font-size: 12px; -fx-cursor: hand;";
        }
        return "-fx-background-color: #1E293B; -fx-text-fill: #94A3B8; " +
                "-fx-border-color: rgba(148, 163, 184, 0.25); -fx-border-radius: 6; " +
                "-fx-background-radius: 6; -fx-min-width: 30px; -fx-min-height: 30px; " +
                "-fx-font-size: 12px; -fx-cursor: hand;";
    }
}
