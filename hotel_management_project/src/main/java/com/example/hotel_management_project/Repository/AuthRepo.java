package com.example.hotel_management_project.Repository;

import com.example.hotel_management_project.Models.Entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepo extends JpaRepository<Staff, Long> {
    @Query("SELECT s FROM Staff s WHERE s.username = :account OR s.email = :account OR s.phoneNumber = :account")
    Optional<Staff> findByUsernameOrEmailOrPhoneNumber(@Param("account") String account);
}
