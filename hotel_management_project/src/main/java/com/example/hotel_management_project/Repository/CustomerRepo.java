package com.example.hotel_management_project.Repository;

import com.example.hotel_management_project.DTO.Request.CustomerRequest;
import com.example.hotel_management_project.Models.Entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
    boolean existsByCitizenId(String citizenId);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT c FROM Customer c WHERE c.active = true")
    Page<Customer> findByActiveTrue(Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.active = true")
    List<Customer> findByActiveTrue();

    @Query(value = "SELECT c FROM Customer c WHERE " +
            "(:fullName IS NULL OR :fullName = '' OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND " +
            "(:citizenId IS NULL OR :citizenId = '' OR c.citizenId LIKE CONCAT('%', :citizenId, '%')) AND " +
            "(:email IS NULL OR :email = '' OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:phoneNumber IS NULL OR :phoneNumber = '' OR c.phoneNumber LIKE CONCAT('%', :phoneNumber, '%')) AND " +
            "(c.active = true)",
            countQuery = "SELECT COUNT(c) FROM Customer c WHERE " +
            "(:fullName IS NULL OR :fullName = '' OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND " +
            "(:citizenId IS NULL OR :citizenId = '' OR c.citizenId LIKE CONCAT('%', :citizenId, '%')) AND " +
            "(:email IS NULL OR :email = '' OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:phoneNumber IS NULL OR :phoneNumber = '' OR c.phoneNumber LIKE CONCAT('%', :phoneNumber, '%')) AND " +
            "(c.active = true)")
    Page<Customer> searchCustomers(
            @Param("fullName") String fullName,
            @Param("citizenId") String citizenId,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            Pageable pageable
    );
}
