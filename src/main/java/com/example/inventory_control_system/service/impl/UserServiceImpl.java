package com.example.inventory_control_system.service.impl;


import com.example.inventory_control_system.dto.UserDto;
import com.example.inventory_control_system.enums.RoleName;
import com.example.inventory_control_system.model.Role;
import com.example.inventory_control_system.model.User;
import com.example.inventory_control_system.repo.RoleRepo;
import com.example.inventory_control_system.repo.UserRepo;
import com.example.inventory_control_system.service.EmailService;
import com.example.inventory_control_system.service.UserService;
import com.example.inventory_control_system.utils.CustomEmailMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepository;
    private final RoleRepo roleRepo;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);



    @Override
    public UserDto registerUser(UserDto userDto) {
        // Encrypt the password
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Get the current logged-in user's roles
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> currentUserRoles = auth.getAuthorities();

        // Fetch OWNER role
        Role ownerRole = roleRepo.findByName(RoleName.OWNER.name())
                .orElseThrow(() -> new RuntimeException("Role OWNER not found"));

        // Map userDto to User entity and set roles
        User user = modelMapper.map(userDto, User.class);
        Set<Role> roles = new HashSet<>();
        roles.add(ownerRole);
        user.setRoles(roles);
        user.setCreatedAt(LocalDateTime.now());

        // Save the user to the repository
        user = userRepository.save(user);

        // Prepare the email message
        CustomEmailMessage emailMessage = new CustomEmailMessage();
        emailMessage.setFrom("readymade090@gmail.com");
        emailMessage.setTo(user.getEmail());
        emailMessage.setSentDate(new Date());
        emailMessage.setSubject("Welcome to the Inventory Control System");
        emailMessage.setText(String.format(
                "Hello %s,\n\n" +
                        "Thank you for joining our Inventory Control System! We’re excited to help you efficiently manage and track inventory.\n\n" +
                        "With our system, you can streamline operations, stay organized, and make data-driven decisions to enhance productivity.\n\n" +
                        "Best regards,\n" +
                        "Inventory Control Team",
                user.getFullName()
        ));

        // Send registration success email
        try {
            emailService.sendEmail(emailMessage);
        } catch (Exception e) {
            logger.error("Failed to send registration email to {}", user.getEmail(), e);
        }

        return modelMapper.map(user, UserDto.class);
    }


    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        // Fetch the user to be updated
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Check if the user to be updated has the "OWNER" role
        boolean isOwner = existingUser.getRoles().stream()
                .anyMatch(role -> "OWNER".equals(role.getName()));
        if (!isOwner) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        // Update fields
        existingUser.setFullName(userDto.getFullName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setDateOfBirth(userDto.getDateOfBirth());
        existingUser.setAddress(userDto.getAddress());
        existingUser.setPhoneNumber(userDto.getPhoneNumber());
        existingUser.setUsername(userDto.getUsername());
        if (userDto.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // Save and return updated user
        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserDto.class);
    }



    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Check if the user has the "OWNER" role
        boolean isOwner = user.getRoles().stream()
                .anyMatch(role -> "OWNER".equals(role.getName()));
        if (!isOwner) {
            throw new EntityNotFoundException("User with id: " + userId + " does not exist.");
        }

        // Map the user to a DTO
        UserDto userDto = modelMapper.map(user, UserDto.class);

        // Extract and set role names
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        userDto.setRoles(roleNames);

        return userDto;
    }



    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> "OWNER".equals(role.getName())))
                .map(user -> {
                    UserDto userDto = modelMapper.map(user, UserDto.class);

                    Set<String> roleNames = user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet());
                    userDto.setRoles(roleNames);

                    return userDto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public void deleteUser(Long userId) {
        // Fetch the user to be deleted
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Check if the user has the "OWNER" role
        boolean isOwner = user.getRoles().stream()
                .anyMatch(role -> "OWNER".equals(role.getName()));
        if (!isOwner) {
            throw new IllegalArgumentException("User not found with ID " + userId );
        }

        // Delete the user
        userRepository.delete(user);

        // Log the deletion
        logger.info("User with ID {} and username '{}' was successfully deleted", userId, user.getUsername());
    }


}
