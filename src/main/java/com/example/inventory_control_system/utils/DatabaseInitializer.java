package com.example.inventory_control_system.utils;


import com.example.inventory_control_system.enums.RoleName;
import com.example.inventory_control_system.model.Role;
import com.example.inventory_control_system.model.User;
import com.example.inventory_control_system.repo.RoleRepo;
import com.example.inventory_control_system.repo.UserRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

@Service
public class DatabaseInitializer {

    private final UserRepo userRepository;
    private final RoleRepo roleRepo; // Add RoleRepo here

    @Autowired
    public DatabaseInitializer(UserRepo userRepository, RoleRepo roleRepo) {
        this.userRepository = userRepository;
        this.roleRepo = roleRepo; // Initialize RoleRepo
    }
    @PostConstruct
    public void init() {
        // Create roles first
        Role adminRole = createRoleIfNotExists(RoleName.ADMIN);
        if (!userRepository.existsByEmail("sumanbisunkhe304@gmail.com")) {
            User user = new User();
            user.setUsername("Suman");
            user.setPassword("$2a$12$CgeWqCls7y1lOl4U7umNEeBNoSUExhG2dgfJseWY27O.jlHnCKt8e");
            user.setEmail("sumanbisunkhe304@gmail.com");
            user.setFullName("Suman Bisunkhe");
            user.setDateOfBirth(LocalDate.of(2004, 2, 25));
            user.setPhoneNumber("9840948274");
            user.setAddress("Kathmandu, Nepal");

            // Setting roles
            HashSet<Role> roles = new HashSet<>();
            roles.add(adminRole);
            user.setRoles(roles);

            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user);
        }
    }

    private Role createRoleIfNotExists(RoleName roleName) {
        if (!roleRepo.existsByName(roleName.toString())) {
            Role role = new Role();
            role.setName(roleName.toString());
            roleRepo.save(role);
            return role; // Return the newly created role
        } else {
            return roleRepo.findByName(roleName.toString()).orElse(null); // Return null if not found (ideally shouldn't happen)
        }
    }


}
