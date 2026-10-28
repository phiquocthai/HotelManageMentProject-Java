package com.example.hotel_management_project.Repository;

import com.example.hotel_management_project.Models.Entity.ShiftAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShiftAssignmentRepo extends JpaRepository<ShiftAssignment, Long> {
    boolean existsByStaffIdAndWorkShiftIdAndWorkDate(Long staffId, Long workShiftId, LocalDate workDate);
    List<ShiftAssignment> findByStaffId(Long staffId);
    List<ShiftAssignment> findByWorkDate(LocalDate workDate);

    @Query(value = "SELECT sa FROM ShiftAssignment sa " +
            "JOIN sa.staff s " +
            "JOIN sa.workShift ws " +
            "WHERE (:staffName IS NULL OR :staffName = '' OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :staffName, '%'))) " +
            "AND (:workShiftId IS NULL OR ws.id = :workShiftId) " +
            "AND (:workDate IS NULL OR sa.workDate = :workDate)",
            countQuery = "SELECT COUNT(sa) FROM ShiftAssignment sa " +
            "JOIN sa.staff s " +
            "JOIN sa.workShift ws " +
            "WHERE (:staffName IS NULL OR :staffName = '' OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :staffName, '%'))) " +
            "AND (:workShiftId IS NULL OR ws.id = :workShiftId) " +
            "AND (:workDate IS NULL OR sa.workDate = :workDate)")
    Page<ShiftAssignment> searchShiftAssignments(
            @Param("staffName") String staffName,
            @Param("workShiftId") Long workShiftId,
            @Param("workDate") LocalDate workDate,
            Pageable pageable);
}
