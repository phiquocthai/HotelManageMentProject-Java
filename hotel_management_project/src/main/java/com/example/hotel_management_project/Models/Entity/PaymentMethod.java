package com.example.hotel_management_project.Models.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "phuong_thuc_thanh_toan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentMethod extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_phuong_thuc")
    private Long id;

    @Column(name = "ten_phuong_thuc", nullable = false, length = 50)
    private String name;

    @Column(name = "anh_qr", length = 255)
    private String qrImage;

    @Column(name = "ten_ngan_hang", length = 100)
    private String bankName;

    @Column(name = "so_tai_khoan", length = 30)
    private String accountNumber;

    @Column(name = "ten_chu_tai_khoan", length = 100)
    private String accountHolderName;

    @Column(name = "dang_hoat_dong", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "paymentMethod", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();
}
