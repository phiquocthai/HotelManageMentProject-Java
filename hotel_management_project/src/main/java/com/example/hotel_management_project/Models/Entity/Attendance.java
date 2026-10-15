package com.example.hotel_management_project.Models.Entity;


import com.example.hotel_management_project.Models.Enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "cham_cong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_cham_cong")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phan_ca", nullable = false, unique = true)
    private ShiftAssignment shiftAssignment;

    @Column(name = "thoi_gian_vao")
    private LocalDateTime checkInTime;

    @Column(name = "thoi_gian_ra")
    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private AttendanceStatus status;

    @Column(name = "ghi_chu", length = 255)
    private String note;
}