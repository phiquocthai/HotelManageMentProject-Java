package com.example.hotel_management_project.Models.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "khach_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_khach_hang")
    private Long id;

    @Column(name = "ho_ten", nullable = false, length = 30)
    private String fullName;

    @Column(name = "so_CCCD", length = 12)
    private String citizenId;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "SDT", length = 10)
    private String phoneNumber;

    @Column(name = "duong_dan_anh", length = 255)
    private String imagePath;

    @Column(name = "dang_hoat_dong", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();
}