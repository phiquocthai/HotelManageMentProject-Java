package com.example.hotel_management_project.Models.Entity;

import com.example.hotel_management_project.Models.Enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "phong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_phong")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_loai_phong", nullable = false)
    private RoomType roomType;

    @Column(name = "so_phong", nullable = false, unique = true, length = 5)
    private String roomNumber;

    @Column(name = "tang")
    private Integer floor;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private RoomStatus status;

    @Column(name = "duong_dan_anh", length = 100)
    private String imagePath;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String note;

    @Column(name = "dang_hoat_dong", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();
}