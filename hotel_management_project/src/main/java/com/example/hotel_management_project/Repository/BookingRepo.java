package com.example.hotel_management_project.Repository;

import com.example.hotel_management_project.Models.Entity.Booking;
import com.example.hotel_management_project.Models.Enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.room r LEFT JOIN FETCH r.roomType WHERE b.id = :id")
    Optional<Booking> findByIdWithDetails(@Param("id") Long id);

    @Query(value = "SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.room r LEFT JOIN FETCH r.roomType",
           countQuery = "SELECT COUNT(b) FROM Booking b")
    Page<Booking> findAllWithDetails(Pageable pageable);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.room r LEFT JOIN FETCH r.roomType")
    List<Booking> findAllWithDetails();

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.room r LEFT JOIN FETCH r.roomType WHERE b.status = :status")
    List<Booking> findByStatusWithDetails(@Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.customer LEFT JOIN FETCH b.room r LEFT JOIN FETCH r.roomType WHERE b.room.id = :roomId AND b.status IN :statuses")
    List<Booking> findByRoomIdAndStatusIn(@Param("roomId") Long roomId, @Param("statuses") List<BookingStatus> statuses);

    @Query(value = "SELECT b FROM Booking b LEFT JOIN FETCH b.customer c LEFT JOIN FETCH b.room r LEFT JOIN FETCH r.roomType rt WHERE " +
            "(:status IS NULL OR b.status = :status) AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "   CONCAT('', b.id) LIKE CONCAT('%', :keyword, '%') OR " +
            "   LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "   c.phoneNumber LIKE CONCAT('%', :keyword, '%') OR " +
            "   r.roomNumber LIKE CONCAT('%', :keyword, '%'))",
           countQuery = "SELECT COUNT(b) FROM Booking b LEFT JOIN b.customer c LEFT JOIN b.room r WHERE " +
            "(:status IS NULL OR b.status = :status) AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "   CONCAT('', b.id) LIKE CONCAT('%', :keyword, '%') OR " +
            "   LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "   c.phoneNumber LIKE CONCAT('%', :keyword, '%') OR " +
            "   r.roomNumber LIKE CONCAT('%', :keyword, '%'))")
    Page<Booking> searchBookings(@Param("keyword") String keyword, @Param("status") BookingStatus status, Pageable pageable);
}
