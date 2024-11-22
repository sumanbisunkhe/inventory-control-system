package com.example.inventory_control_system.repo;

import com.example.inventory_control_system.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepo extends JpaRepository<Supplier, Long> {
    // Add any custom query methods if needed
    boolean existsByEmail(String email);  // To check if a supplier with the given email already exists
}
