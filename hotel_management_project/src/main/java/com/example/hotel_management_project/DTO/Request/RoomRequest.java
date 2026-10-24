package com.example.hotel_management_project.DTO.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoomRequest {

    @NotNull(message = "Mã loại phòng không được để trống")
    private Long roomTypeId;

    @NotBlank(message = "Số phòng không được để trống")
    @Size(max = 5, message = "Số phòng không được vượt quá 5 ký tự")
    private String roomNumber;

    @NotNull(message = "Tầng không được để trống")
    @Min(value = 1, message = "Tầng phải từ 1 trở lên")
    private Integer floor;

    @NotBlank(message = "Trạng thái phòng không được để trống")
    private String status;

    @Size(max = 100, message = "Đường dẫn ảnh không được vượt quá 100 ký tự")
    private String imagePath;

    private String note;
}
