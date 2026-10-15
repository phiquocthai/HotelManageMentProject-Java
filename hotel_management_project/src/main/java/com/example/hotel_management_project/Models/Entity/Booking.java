package com.example.hotel_management_project.Models.Entity;


import com.example.hotel_management_project.Models.Enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dat_phong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dat_phong")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khach_hang", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phong", nullable = false)
    private Room room;

    @Column(name = "ngay_nhan_phong", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "ngay_tra_phong_du_kien", nullable = false)
    private LocalDate expectedCheckOutDate;

    @Column(name = "thoi_gian_nhan_thuc_te")
    private LocalDateTime actualCheckInTime;

    @Column(name = "thoi_gian_tra_thuc_te")
    private LocalDateTime actualCheckOutTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private BookingStatus status;

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    @Builder.Default
    private List<BookingServiceDetail> serviceDetails = new ArrayList<>();

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Invoice> invoices = new ArrayList<>();
}
