package com.example.hotel_management_project.Models.Entity;


import com.example.hotel_management_project.Models.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hoa_don")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Invoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_hoa_don")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dat_phong", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khuyen_mai")
    private Promotion promotion;

    @Column(name = "tien_phong", nullable = false, precision = 12, scale = 0)
    private BigDecimal roomAmount = BigDecimal.ZERO;

    @Column(name = "tien_dich_vu", nullable = false, precision = 12, scale = 0)
    private BigDecimal serviceAmount = BigDecimal.ZERO;

    @Column(name = "tien_phat", nullable = false, precision = 12, scale = 0)
    private BigDecimal penaltyAmount = BigDecimal.ZERO;

    @Column(name = "tien_giam", nullable = false, precision = 12, scale = 0)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tong_tien", nullable = false, precision = 12, scale = 0)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_thanh_toan", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "ngay_lap", nullable = false)
    private LocalDate invoiceDate;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();
}