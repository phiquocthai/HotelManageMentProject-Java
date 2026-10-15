package com.example.hotel_management_project.Models.Entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dich_vu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Service extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dich_vu")
    private Long id;

    @Column(name = "ten_dich_vu", nullable = false, length = 100)
    private String name;

    @Column(name = "don_gia", nullable = false, precision = 12, scale = 0)
    private BigDecimal price;

    @Column(name = "duong_dan_anh", length = 255)
    private String imagePath;

    @Column(name = "dang_hoat_dong", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    @Builder.Default
    private List<BookingServiceDetail> bookingServiceDetails = new ArrayList<>();
}