package com.example.inventory_control_system.service;

import com.example.inventory_control_system.dto.UserDto;
import com.example.inventory_control_system.enums.RoleName;
import com.example.inventory_control_system.model.Role;
import com.example.inventory_control_system.model.User;
import com.example.inventory_control_system.repo.RoleRepo;
import com.example.inventory_control_system.repo.UserRepo;
import com.example.inventory_control_system.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepo userRepository;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EmailService emailService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User user;
    private UserDto userDto;
    private Role ownerRole;

    @BeforeEach
    void setUp() {
        // Set up SecurityContext mocking only if used in tests
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

        // Initialize User and UserDto objects
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPassword("encodedPassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(new HashSet<>());

        userDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .password("plainPassword")
                .build();

        // Initialize Role
        ownerRole = new Role();
        ownerRole.setId(1L);
        ownerRole.setName(RoleName.OWNER.name());
    }

    @Test
    void registerUser_Success() {
        // Stubbing methods used in this test
        when(roleRepo.findByName(RoleName.OWNER.name())).thenReturn(Optional.of(ownerRole));
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto registeredUser = userService.registerUser(userDto);

        assertNotNull(registeredUser);
        verify(emailService, times(1)).sendEmail(any());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_RoleNotFound() {
        when(roleRepo.findByName(RoleName.OWNER.name())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.registerUser(userDto));

        assertEquals("Role OWNER not found", exception.getMessage());
    }

    @Test
    void updateUser_Success() {
        user.getRoles().add(ownerRole);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto updatedUser = userService.updateUser(1L, userDto);

        assertNotNull(updatedUser);
        assertEquals("testuser", updatedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(1L, userDto));

        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    void getUserById_Success() {
        user.getRoles().add(ownerRole);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto fetchedUser = userService.getUserById(1L);

        assertNotNull(fetchedUser);
        assertEquals("testuser", fetchedUser.getUsername());
    }

    @Test
    void getUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(1L));

        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    void deleteUser_Success() {
        user.getRoles().add(ownerRole);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.deleteUser(1L));

        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    void getAllUsers_Success() {
        user.getRoles().add(ownerRole);
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        List<UserDto> users = userService.getAllUsers();

        assertEquals(1, users.size());
        verify(userRepository, times(1)).findAll();
    }
}
