package com.example.hotel_management_project.Models.Entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loai_phong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RoomType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_loai_phong")
    private Long id;

    @Column(name = "ten_loai_phong", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "gia_co_ban", nullable = false, precision = 12, scale = 0)
    private BigDecimal basePrice;

    @Column(name = "so_nguoi_toi_da", nullable = false)
    private Integer maxOccupancy;

    @Column(name = "duong_dan_anh", length = 100)
    private String imagePath;

    @Column(name = "dang_hoat_dong", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "roomType", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();
}