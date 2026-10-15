package com.example.hotel_management_project.Models.Entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ca_lam_viec")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WorkShift extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ca")
    private Long id;

    @Column(name = "ten_ca", nullable = false, unique = true, length = 20)
    private String shiftName;

    @Column(name = "gio_bat_dau", nullable = false)
    private LocalTime startTime;

    @Column(name = "gio_ket_thuc", nullable = false)
    private LocalTime endTime;

    @Column(name = "dang_hoat_dong", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "workShift", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ShiftAssignment> shiftAssignments = new ArrayList<>();
}