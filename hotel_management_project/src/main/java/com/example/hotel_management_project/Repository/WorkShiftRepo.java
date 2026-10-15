package com.example.hotel_management_project.Repository;

import com.example.hotel_management_project.Models.Entity.WorkShift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface WorkShiftRepo extends JpaRepository<WorkShift, Long> {
    boolean existsByShiftName(String shiftName);
    boolean existsByStartTimeAndEndTime(LocalTime startTime, LocalTime endTime);

    @Query("SELECT w FROM WorkShift w WHERE w.active = true")
    Page<WorkShift> findByActiveTrue(Pageable pageable);

    @Query("SELECT w FROM WorkShift w WHERE w.active = true")
    List<WorkShift> findByActiveTrue();
}

