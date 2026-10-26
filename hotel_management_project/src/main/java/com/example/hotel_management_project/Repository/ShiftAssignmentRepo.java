package com.example.hotel_management_project.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.hotel_management_project.Models.Entity.ShiftAssignment;

@Repository
public interface ShiftAssignmentRepo extends JpaRepository<ShiftAssignment, Long> {

    // Kiểm tra ràng buộc phân ca trùng lặp (1 nhân viên, 1 ca, 1 ngày)
    boolean existsByStaffIdAndWorkShiftIdAndWorkDate(Long staffId, Long workShiftId, LocalDate workDate);

    @Query("SELECT sa FROM ShiftAssignment sa WHERE sa.workDate = :workDate")
    Page<ShiftAssignment> findByWorkDate(@Param("workDate") LocalDate workDate, Pageable pageable);

    @Query("SELECT sa FROM ShiftAssignment sa WHERE sa.staff.id = :staffId")
    Page<ShiftAssignment> findByStaffId(@Param("staffId") Long staffId, Pageable pageable);

    List<ShiftAssignment> findByWorkDate(LocalDate workDate);
}
