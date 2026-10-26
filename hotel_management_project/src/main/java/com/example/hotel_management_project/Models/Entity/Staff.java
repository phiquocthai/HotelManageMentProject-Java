package com.example.hotel_management_project.Models.Entity;


import com.example.hotel_management_project.Models.Enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nhan_vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Staff extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nhan_vien")
    private Long id;

    // Login information
    @Column(name = "ten_dang_nhap", nullable = false, unique = true, length = 30)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "mat_khau", nullable = false, length = 255)
    private String password;

    @Column(name = "SDT", length = 10)
    private String phoneNumber;

    // Staff information
    @Column(name = "ho_ten", nullable = false, length = 30)
    private String fullName;

//    @Column(name = "chuc_danh", nullable = false, length = 50)
//    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false)
    private Role role;

    @Column(name = "duong_dan_anh", length = 255)
    private String profileImage;

    // Account status
    @Column(name = "dang_hoat_dong", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ShiftAssignment> shiftAssignments = new ArrayList<>();
}