package com.example.hotel_management_project.Repository;

import com.example.hotel_management_project.Models.Entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepo extends JpaRepository<Staff, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT s FROM Staff s WHERE s.active = true")
    Page<Staff> findByActiveTrue(Pageable pageable);

    @Query("SELECT s FROM Staff s WHERE s.active = true")
    List<Staff> findByActiveTrue();
}
