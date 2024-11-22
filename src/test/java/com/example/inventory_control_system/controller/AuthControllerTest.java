package com.example.inventory_control_system.controller;

import com.example.inventory_control_system.security.AuthenticationRequest;
import com.example.inventory_control_system.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    private AuthenticationRequest validRequest;
    private UserDetails userDetails;
    private static final String TEST_JWT_TOKEN = "mockJwtToken";

    @BeforeEach
    void setUp() {
        validRequest = new AuthenticationRequest("testuser", "password123");
        userDetails = new User(
                "testuser",
                "password123",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void login_ValidCredentials_ReturnsSuccessfulResponse() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(userDetails);

        when(jwtUtil.generateToken(eq("testuser"), any(List.class)))
                .thenReturn(TEST_JWT_TOKEN);

        // Act
        ResponseEntity<Map<String, Object>> response = authController.login(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK.value(), responseBody.get("status"));
        assertEquals("Login successful", responseBody.get("message"));
        assertEquals(TEST_JWT_TOKEN, responseBody.get("token"));

        // Verify interactions
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(validRequest.getIdentifier(), validRequest.getPassword())
        );
        verify(userDetailsService).loadUserByUsername(validRequest.getIdentifier());
        verify(jwtUtil).generateToken(eq("testuser"), any(List.class));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorizedResponse() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        ResponseEntity<Map<String, Object>> response = authController.login(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), responseBody.get("status"));
        assertEquals("Invalid credentials", responseBody.get("message"));
        assertNull(responseBody.get("token"));

        // Verify interactions
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(validRequest.getIdentifier(), validRequest.getPassword())
        );
        verifyNoInteractions(userDetailsService, jwtUtil);
    }
}