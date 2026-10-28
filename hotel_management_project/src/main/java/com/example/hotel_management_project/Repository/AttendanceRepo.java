package com.example.hotel_management_project.Repository;

import com.example.hotel_management_project.Models.Entity.Attendance;
import com.example.hotel_management_project.Models.Enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, Long> {
    boolean existsByShiftAssignmentId(Long shiftAssignmentId);
    Optional<Attendance> findByShiftAssignmentId(Long shiftAssignmentId);

    @Query(value = "SELECT a FROM Attendance a " +
            "JOIN a.shiftAssignment sa " +
            "JOIN sa.staff s " +
            "JOIN sa.workShift ws " +
            "WHERE (:staffName IS NULL OR :staffName = '' OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :staffName, '%'))) " +
            "AND (:workShiftId IS NULL OR ws.id = :workShiftId) " +
            "AND (:workDate IS NULL OR sa.workDate = :workDate) " +
            "AND (:status IS NULL OR a.status = :status)",
            countQuery = "SELECT COUNT(a) FROM Attendance a " +
            "JOIN a.shiftAssignment sa " +
            "JOIN sa.staff s " +
            "JOIN sa.workShift ws " +
            "WHERE (:staffName IS NULL OR :staffName = '' OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :staffName, '%'))) " +
            "AND (:workShiftId IS NULL OR ws.id = :workShiftId) " +
            "AND (:workDate IS NULL OR sa.workDate = :workDate) " +
            "AND (:status IS NULL OR a.status = :status)")
    Page<Attendance> searchAttendances(
            @Param("staffName") String staffName,
            @Param("workShiftId") Long workShiftId,
            @Param("workDate") LocalDate workDate,
            @Param("status") AttendanceStatus status,
            Pageable pageable);
}
