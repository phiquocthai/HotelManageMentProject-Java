package com.example.hotel_management_project.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private int pageNo;          // Trang hiện tại
    private int pageSize;        // Kích thước trang (số phần tử/trang)
    private long totalElements;  // Tổng số bản ghi trong DB
    private int totalPages;      // Tổng số trang tính ra được
    private boolean last;        // Có phải trang cuối cùng chưa
    private List<T> content;     // Danh sách dữ liệu thực tế (VD: List<RoomResponse>)
}
