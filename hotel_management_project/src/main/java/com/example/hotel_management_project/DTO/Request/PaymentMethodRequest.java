package com.example.hotel_management_project.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodRequest {

    @NotBlank(message = "Tên phương thức thanh toán không được để trống")
    @Size(max = 50, message = "Tên phương thức thanh toán không được vượt quá 50 ký tự")
    private String name;

    @Size(max = 255, message = "Đường dẫn ảnh QR không được vượt quá 255 ký tự")
    private String qrImage;

    @Size(max = 100, message = "Tên ngân hàng không được vượt quá 100 ký tự")
    private String bankName;

    @Size(max = 30, message = "Số tài khoản không được vượt quá 30 ký tự")
    private String accountNumber;

    @Size(max = 100, message = "Tên chủ tài khoản không được vượt quá 100 ký tự")
    private String accountHolderName;

    private Boolean active;
}
