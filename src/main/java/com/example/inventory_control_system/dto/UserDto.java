package com.example.inventory_control_system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    private Set<String> roles;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Past(message = "Date of birth must be a past date")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^[+]?\\d{10,15}$", message = "Phone number should be valid and between 10-15 digits")
    private String phoneNumber;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    private Boolean isActive;

    @JsonFormat(pattern = "EEEE, MMMM d, yyyy, h:mm a")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "EEEE, MMMM d, yyyy, h:mm a")
    private LocalDateTime updatedAt;
}
