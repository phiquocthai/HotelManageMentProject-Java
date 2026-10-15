
package com.example.hotel_management_project.Models.Entity;

import com.example.hotel_management_project.Models.Enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "khuyen_mai")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Promotion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_khuyen_mai")
    private Long id;

    @Column(name = "ma_khuyen_mai_code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "ten_khuyen_mai", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giam", nullable = false)
    private DiscountType discountType;

    @Column(name = "gia_tri_giam", nullable = false, precision = 12, scale = 0)
    private BigDecimal discountValue;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate startDate;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDate endDate;

    @Column(name = "dang_hoat_dong", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Invoice> invoices = new ArrayList<>();
}