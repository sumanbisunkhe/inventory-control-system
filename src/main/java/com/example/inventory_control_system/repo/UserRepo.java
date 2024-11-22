package com.example.inventory_control_system.repo;


import com.example.inventory_control_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {
    // Method to find a user by username
    Optional<User> findByUsername(String username);

    // Method to find a user by email
    Optional<User> findByEmail(String email);

    // Method to find a user by phone number
    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phone);

    Optional<User> findByUsernameOrEmail(String identifier, String identifier1);

    Optional<User>  findById(long id);

    // Update this method to return List<User>
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :role")
    List<User> findAllByRole(String role);
}