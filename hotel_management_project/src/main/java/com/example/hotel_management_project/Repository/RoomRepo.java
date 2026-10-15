package com.example.hotel_management_project.Repository;

import com.example.hotel_management_project.Models.Entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepo extends JpaRepository<Room, Long> {
    boolean existsByRoomNumber(String roomNumber);

    @Query("SELECT r FROM Room r WHERE r.active = true")
    Page<Room> findByActiveTrue(Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.active = true")
    List<Room> findByActiveTrue();
}
