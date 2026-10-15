package com.example.hotel_management_project.Repository;

import com.example.hotel_management_project.Models.Entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepo extends JpaRepository<Promotion, Long> {
    boolean existsByCode(String code);
    Optional<Promotion> findByCode(String code);

    @Query("SELECT p FROM Promotion p WHERE p.active = true")
    Page<Promotion> findByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Promotion p WHERE p.active = true")
    List<Promotion> findByActiveTrue();
}
