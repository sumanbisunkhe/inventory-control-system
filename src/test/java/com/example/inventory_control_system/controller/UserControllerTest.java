package com.example.inventory_control_system.controller;

import com.example.inventory_control_system.dto.UserDto;
import com.example.inventory_control_system.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDto = UserDto.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .fullName("Test User")
                .password("password123")
                .roles(Set.of("OWNER"))
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .phoneNumber("+1234567890")
                .address("123 Test Street")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void registerUser_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.registerUser(any(UserDto.class))).thenReturn(userDto);

        ResponseEntity<Map<String, Object>> response = userController.registerUser(userDto, bindingResult);

        assertEquals(CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User registered successfully", response.getBody().get("message"));
        verify(userService, times(1)).registerUser(any(UserDto.class));
    }

    @Test
    void registerUser_ValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<Map<String, Object>> response = userController.registerUser(userDto, bindingResult);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        verify(userService, never()).registerUser(any(UserDto.class));
    }

    @Test
    void updateUser_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(userDto);

        ResponseEntity<Map<String, Object>> response = userController.updateUser(1L, userDto, bindingResult);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User updated successfully", response.getBody().get("message"));
        verify(userService, times(1)).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    void updateUser_ValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<Map<String, Object>> response = userController.updateUser(1L, userDto, bindingResult);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        verify(userService, never()).updateUser(anyLong(), any(UserDto.class));
    }

    @Test
    void getUserById_Success() {
        when(userService.getUserById(1L)).thenReturn(userDto);

        ResponseEntity<Map<String, Object>> response = userController.getUserById(1L);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userDto, response.getBody().get("user"));
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_NotFound() {
        when(userService.getUserById(1L)).thenThrow(new EntityNotFoundException("User not found"));

        ResponseEntity<Map<String, Object>> response = userController.getUserById(1L);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found with ID: 1", response.getBody().get("message"));
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getAllUsers_Success() {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userDto));

        ResponseEntity<Map<String, Object>> response = userController.getAllUsers();

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, ((List<?>) response.getBody().get("users")).size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void deleteUser_Success() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Map<String, Object>> response = userController.deleteUser(1L);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User with ID: 1 deleted successfully.", response.getBody().get("message"));
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_NotFound() {
        doThrow(new EntityNotFoundException("User not found")).when(userService).deleteUser(1L);

        ResponseEntity<Map<String, Object>> response = userController.deleteUser(1L);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found with ID: 1. Unable to delete non-existent user.", response.getBody().get("message"));
        verify(userService, times(1)).deleteUser(1L);
    }
}
