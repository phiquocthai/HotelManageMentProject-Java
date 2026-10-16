package com.example.hotel_management_project.DTO.Request;


package com.example.hotel_management_project.Models.Entity;

import com.example.hotel_management_project.Models.Entity.BaseEntity;
import com.example.hotel_management_project.Models.Entity.Invoice;
import com.example.hotel_management_project.Models.Enums.DiscountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PromotionRequest {


    @NotBlank(message = "mã Code không được trống")
    @Size(max = 30,message = "mã code không được quá 30 kí tự ")
    private String code;

    @NotBlank(message = "mã Code không được trống")
    @Size(max = 30,message = "mã code không được quá 30 kí tự ")
    private String name;


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
