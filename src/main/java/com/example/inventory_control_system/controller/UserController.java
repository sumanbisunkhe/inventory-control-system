package com.example.inventory_control_system.controller;

import com.example.inventory_control_system.dto.UserDto;
import com.example.inventory_control_system.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Register a new user
     * @param userDto User details for registration
     * @param bindingResult Validation errors if any
     * @return JSON response with status and message
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        UserDto registeredUser = userService.registerUser(userDto);
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "User registered successfully");
        response.put("user", registeredUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update user details
     * @param userId User ID to be updated
     * @param userDto Updated user details
     * @param bindingResult Validation errors if any
     * @return JSON response with status and updated user details
     */
    @PutMapping("/update/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long userId,
                                                          @Valid @RequestBody UserDto userDto,
                                                          BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        UserDto updatedUser = userService.updateUser(userId, userDto);
        response.put("status", HttpStatus.OK.value());
        response.put("message", "User updated successfully");
        response.put("user", updatedUser);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user details by ID
     * @param userId User ID to fetch details
     * @return JSON response with status and user details or error message
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDto userDto = userService.getUserById(userId);
            response.put("status", HttpStatus.OK.value());
            response.put("user", userDto);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", String.format("User not found with ID: %d", userId));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get all users
     * @return JSON response with status and list of users
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        List<UserDto> users = userService.getAllUsers();
        response.put("status", HttpStatus.OK.value());
        response.put("users", users);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete user by ID
     * @param userId User ID to delete
     * @return JSON response with status and message
     */
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.deleteUser(userId);
            response.put("status", HttpStatus.OK.value());
            response.put("message", String.format("User with ID: %d deleted successfully.", userId));
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", String.format("User not found with ID: %d. Unable to delete non-existent user.", userId));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
