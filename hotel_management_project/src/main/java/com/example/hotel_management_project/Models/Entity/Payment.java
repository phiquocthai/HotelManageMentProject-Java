package com.example.hotel_management_project.Models.Entity;

import com.example.hotel_management_project.Models.Enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_toan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thanh_toan")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_hoa_don", nullable = false)
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "phuong_thuc", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "so_tien", nullable = false, precision = 12, scale = 0)
    private BigDecimal amount;

    @Column(name = "ma_giao_dich", length = 100)
    private String transactionCode;

    @Column(name = "thoi_gian_thanh_toan")
    private LocalDateTime paymentTime;
}