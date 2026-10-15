package com.lotusstay.javafx.net;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Lớp gọi REST API backend Spring Boot (xem dự án lotusstay-backend đã gửi).
 * Đổi BASE_URL nếu backend chạy ở địa chỉ khác localhost:8080.
 */
public class ApiClient {

    public static String BASE_URL = "http://localhost:8080";

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    public static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static class ApiException extends RuntimeException {
        public ApiException(String message) { super(message); }
    }

    private static HttpRequest.Builder baseRequest(String path) {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10));
        if (Session.isLoggedIn()) {
            b.header("Authorization", "Bearer " + Session.getToken());
        }
        return b;
    }

    private static String readError(HttpResponse<String> resp) {
        try {
            Map<?, ?> m = MAPPER.readValue(resp.body(), Map.class);
            Object msg = m.get("message");
            if (msg != null) return msg.toString();
        } catch (Exception ignored) { /* body không đúng định dạng lỗi chuẩn */ }
        return "Lỗi máy chủ (HTTP " + resp.statusCode() + ")";
    }

    private static HttpResponse<String> send(HttpRequest request) {
        try {
            HttpResponse<String> resp = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 400) {
                throw new ApiException(readError(resp));
            }
            return resp;
        } catch (IOException | InterruptedException e) {
            throw new ApiException("Không kết nối được tới backend (" + BASE_URL + "). Backend đã chạy chưa?");
        }
    }

    public static <T> T get(String path, TypeReference<T> type) {
        HttpRequest req = baseRequest(path).GET().build();
        HttpResponse<String> resp = send(req);
        return parse(resp.body(), type);
    }

    public static <T> T post(String path, Object body, TypeReference<T> type) {
        HttpRequest req = baseRequest(path)
                .POST(HttpRequest.BodyPublishers.ofString(toJson(body)))
                .build();
        HttpResponse<String> resp = send(req);
        return parse(resp.body(), type);
    }

    public static <T> T put(String path, Object body, TypeReference<T> type) {
        HttpRequest req = baseRequest(path)
                .PUT(HttpRequest.BodyPublishers.ofString(toJson(body)))
                .build();
        HttpResponse<String> resp = send(req);
        return parse(resp.body(), type);
    }

    public static <T> T patch(String path, Object body, TypeReference<T> type) {
        HttpRequest req = baseRequest(path)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(toJson(body)))
                .build();
        HttpResponse<String> resp = send(req);
        return parse(resp.body(), type);
    }

    public static void delete(String path) {
        HttpRequest req = baseRequest(path).DELETE().build();
        send(req);
    }

    private static String toJson(Object body) {
        try { return MAPPER.writeValueAsString(body); }
        catch (Exception e) { throw new ApiException("Lỗi tạo dữ liệu gửi đi: " + e.getMessage()); }
    }

    private static <T> T parse(String body, TypeReference<T> type) {
        try {
            if (body == null || body.isBlank()) return null;
            return MAPPER.readValue(body, type);
        } catch (Exception e) {
            throw new ApiException("Lỗi đọc dữ liệu trả về từ máy chủ: " + e.getMessage());
        }
    }
}
