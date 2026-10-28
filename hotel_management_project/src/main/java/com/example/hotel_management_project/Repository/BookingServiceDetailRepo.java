package com.example.hotel_management_project.Repository;

import com.example.hotel_management_project.Models.Entity.BookingServiceDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingServiceDetailRepo extends JpaRepository<BookingServiceDetail, Long> {

    List<BookingServiceDetail> findByBookingId(Long bookingId);

    Page<BookingServiceDetail> findByBookingId(Long bookingId, Pageable pageable);

    List<BookingServiceDetail> findByServiceId(Long serviceId);

    @Query("SELECT d FROM BookingServiceDetail d WHERE (:bookingId IS NULL OR d.booking.id = :bookingId)")
    Page<BookingServiceDetail> searchDetails(@Param("bookingId") Long bookingId, Pageable pageable);
}
