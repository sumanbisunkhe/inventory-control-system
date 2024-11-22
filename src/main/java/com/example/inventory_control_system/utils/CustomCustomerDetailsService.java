package com.example.inventory_control_system.utils;

import com.example.inventory_control_system.repo.UserRepo;
import com.example.inventory_control_system.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CustomCustomerDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public CustomCustomerDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Check for user by username, email, or phone number
        Optional<User> userOptional = userRepo.findByUsername(identifier)
                .or(() -> userRepo.findByEmail(identifier))
                .or(() -> userRepo.findByPhoneNumber(identifier));

        // Throw exception if user not found
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with identifier: " + identifier));

        // Convert the User entity into Spring Security's UserDetails
        return org.springframework.security.core.userdetails.User
                .builder()  // Use the builder() method
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())  // Assuming role.getName() returns the role name
                        .toArray(String[]::new))
                .build();
    }
}
