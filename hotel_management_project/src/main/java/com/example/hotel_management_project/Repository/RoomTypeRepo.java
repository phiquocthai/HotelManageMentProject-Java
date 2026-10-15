package com.example.hotel_management_project.Repository;

import com.example.hotel_management_project.Models.Entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeRepo extends JpaRepository<RoomType, Long> {
    boolean existsByName(String name);

    @Query("SELECT r FROM RoomType r WHERE r.active = true")
    Page<RoomType> findByActiveTrue(Pageable pageable);

    @Query("SELECT r FROM RoomType r WHERE r.active = true")
    List<RoomType> findByActiveTrue();
}
