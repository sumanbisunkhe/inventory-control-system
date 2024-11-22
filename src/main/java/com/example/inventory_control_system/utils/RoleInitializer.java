package com.example.inventory_control_system.utils;



import com.example.inventory_control_system.enums.RoleName;
import com.example.inventory_control_system.model.Role;
import com.example.inventory_control_system.repo.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public void run(String... args) {
        // Create and save the default roles if they do not already exist
        createRoleIfNotExists(RoleName.ADMIN);
        createRoleIfNotExists(RoleName.OWNER);
    }

    private void createRoleIfNotExists(RoleName roleName) {
        if (!roleRepo.existsByName(roleName.toString())) { // Adjust this line based on your repo method
            Role role = new Role();
            role.setName(roleName.toString());
            roleRepo.save(role);
        }
    }
}