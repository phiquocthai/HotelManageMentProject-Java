# LotusStay — Ứng dụng Desktop JavaFX (bản FE-only, dữ liệu mẫu)

Đây là **giao diện JavaFX thuần** cho phần mềm quản lý khách sạn LotusStay.
Hiện tại **chưa gọi backend thật** — toàn bộ dữ liệu (phòng, khách hàng, đặt
phòng, hóa đơn, dịch vụ, nhân viên, tài khoản) được lưu **trong bộ nhớ**, nạp
sẵn dữ liệu mẫu, để bạn chạy và xem giao diện ngay trong khi tự xây dựng backend.

## Chạy thử

```bash
mvn javafx:run
```

Đăng nhập bằng 1 trong 3 tài khoản mẫu:

| Username | Password | Vai trò |
|---|---|---|
| admin | admin123 | Quản trị viên — thấy đủ mọi menu |
| manager | manager123 | Quản lý |
| reception | reception123 | Lễ tân |

## Cấu trúc dự án

```
src/main/java/com/lotusstay/javafx/
├── MainApp.java              → điểm khởi chạy
├── model/                    → Room, RoomType, Customer, Booking, Invoice, ServiceItem, Staff, UserAccount...
├── net/
│   ├── ApiService.java       → ★ TOÀN BỘ "BACKEND GIẢ" NẰM Ở ĐÂY — danh sách trong bộ nhớ + dữ liệu mẫu
│   ├── ApiClient.java        → sẵn sàng gọi HTTP thật (chưa dùng tới) — xem hướng dẫn bên dưới
│   └── Session.java          → lưu phiên đăng nhập hiện tại
└── ui/                       → LoginView, MainShell, DashboardView, RoomsView, BookingsView,
                                 CustomersView, InvoicesView, ServicesView, StaffView, UsersView
```

## Khi bạn làm xong backend — nối API thật vào đây

Toàn bộ giao diện chỉ gọi vào **`ApiService`** (ví dụ `ApiService.rooms()`,
`ApiService.login(...)`) — không gọi trực tiếp danh sách dữ liệu ở đâu khác.
Vì vậy, để chuyển từ mock sang backend thật, bạn **chỉ cần sửa file
`ApiService.java`**, không cần đụng tới bất kỳ file nào trong `ui/`.

Có 2 cách:

**Cách 1 — nhanh gọn:** trong mỗi hàm của `ApiService.java`, thay phần thao tác
danh sách trong bộ nhớ bằng một dòng gọi `ApiClient` (đã viết sẵn ở
`ApiClient.java`, chỉ cần `BASE_URL` trỏ đúng địa chỉ backend). Ví dụ:

```java
// Trước (mock):
public static synchronized List<Room> rooms() {
    sleep();
    return new ArrayList<>(ROOMS);
}

// Sau (gọi API thật):
public static List<Room> rooms() {
    return ApiClient.get("/api/rooms", new TypeReference<List<Room>>() {});
}
```

**Cách 2 — giữ cả hai:** đổi tên file mock hiện tại thành `ApiServiceMock.java`
(class `ApiServiceMock`), tạo lại `ApiService.java` gọi `ApiClient` theo đúng
endpoint trong README của dự án `lotusstay-backend` đã gửi trước đó, rồi đổi
1 dòng `import` ở các file `ui/*.java` khi cần bật/tắt chế độ mock để demo.

## Lưu ý

- `ApiClient.java` dùng `java.net.http.HttpClient` + Jackson, đã có sẵn JWT
  (`Authorization: Bearer <token>`) và xử lý lỗi — chỉ cần gọi là chạy được
  với backend Spring Boot đã gửi trước đó.
- Dữ liệu mẫu nạp trong `ApiService.seed()` — sửa trực tiếp ở đó nếu muốn đổi
  kịch bản demo (thêm phòng, khách, hóa đơn...).
- Vì chưa có backend thật, đăng xuất/đăng nhập lại sẽ **mất mọi thay đổi**
  (dữ liệu chỉ tồn tại trong bộ nhớ khi ứng dụng đang chạy).
