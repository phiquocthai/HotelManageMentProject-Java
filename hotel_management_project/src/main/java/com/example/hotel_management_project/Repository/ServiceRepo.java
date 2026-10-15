package com.example.hotel_management_project.Repository;

import com.example.hotel_management_project.Models.Entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepo extends JpaRepository<Service, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT s FROM Service s WHERE s.active = true")
    Page<Service> findByActiveTrue(Pageable pageable);

    @Query("SELECT s FROM Service s WHERE s.active = true")
    List<Service> findByActiveTrue();
}
