package com.example.hotel_management_project.Models.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(
        name = "phan_ca",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"ma_nhan_vien", "ma_ca", "ngay_lam_viec"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ShiftAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_phan_ca")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_nhan_vien", nullable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_ca", nullable = false)
    private WorkShift workShift;

    @Column(name = "ngay_lam_viec", nullable = false)
    private LocalDate workDate;

    @OneToOne(mappedBy = "shiftAssignment", fetch = FetchType.EAGER)
    private Attendance attendance;
}