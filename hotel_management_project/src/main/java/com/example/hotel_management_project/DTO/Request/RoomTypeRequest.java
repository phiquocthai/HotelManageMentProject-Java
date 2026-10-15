package com.example.hotel_management_project.DTO.Request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomTypeRequest {
    @NotBlank(message = "Tên loại phòng không được để trống")
    @Size(max = 50, message = "Tên loại phòng không được vượt quá 50 ký tự")
    private String name;

    @NotNull(message = "Giá cơ bản không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá cơ bản phải lớn hơn 0")
    private BigDecimal basePrice;

    @NotNull(message = "Số người tối đa không được để trống")
    @Min(value = 1, message = "Số người tối đa phải từ 1 trở lên")
    private Integer maxOccupancy;

    @Size(max = 100, message = "Đường dẫn ảnh không được vượt quá 100 ký tự")
    private String imagePath;
}
