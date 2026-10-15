package com.lotusstay.javafx.ui;

import com.lotusstay.javafx.net.ApiClient;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Util {

    public static void error(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message);
        a.setHeaderText("Có lỗi xảy ra");
        a.showAndWait();
    }

    public static void info(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, message);
        a.setHeaderText(null);
        a.showAndWait();
    }

    /** Gọi API ở một luồng nền, trả kết quả về luồng JavaFX qua onSuccess; lỗi thì hiện Alert. */
    public static <T> void runAsync(Supplier<T> apiCall, Consumer<T> onSuccess) {
        new Thread(() -> {
            try {
                T result = apiCall.get();
                Platform.runLater(() -> onSuccess.accept(result));
            } catch (ApiClient.ApiException ex) {
                Platform.runLater(() -> error(ex.getMessage()));
            } catch (Exception ex) {
                Platform.runLater(() -> error("Lỗi không xác định: " + ex.getMessage()));
            }
        }).start();
    }

    /** Dùng cho các action không trả dữ liệu (ví dụ delete) nhưng vẫn cần chạy nền + callback khi xong. */
    public static void runAsyncVoid(Runnable apiCall, Runnable onSuccess) {
        new Thread(() -> {
            try {
                apiCall.run();
                Platform.runLater(onSuccess);
            } catch (ApiClient.ApiException ex) {
                Platform.runLater(() -> error(ex.getMessage()));
            } catch (Exception ex) {
                Platform.runLater(() -> error("Lỗi không xác định: " + ex.getMessage()));
            }
        }).start();
    }

    public static String money(BigDecimal v) {
        if (v == null) return "0 ₫";
        return String.format("%,.0f ₫", v);
    }
}
