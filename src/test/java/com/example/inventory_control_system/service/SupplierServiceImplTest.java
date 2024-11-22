package com.example.inventory_control_system.service;

import com.example.inventory_control_system.dto.SupplierDto;
import com.example.inventory_control_system.exceptions.SupplierNotFoundException;
import com.example.inventory_control_system.model.Supplier;
import com.example.inventory_control_system.repo.SupplierRepo;
import com.example.inventory_control_system.service.impl.SupplierServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierServiceImplTest {

    @InjectMocks
    private SupplierServiceImpl supplierService;

    @Mock
    private SupplierRepo supplierRepo;

    private Supplier supplier;
    private SupplierDto supplierDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Test Supplier");
        supplier.setEmail("supplier@example.com");
        supplier.setPhoneNumber("1234567890");
        supplier.setAddress("Test Address");
        supplier.setCompanyName("Test Company");

        supplierDto = new SupplierDto();
        supplierDto.setName("Test Supplier");
        supplierDto.setEmail("supplier@example.com");
        supplierDto.setPhoneNumber("1234567890");
        supplierDto.setAddress("Test Address");
        supplierDto.setCompanyName("Test Company");
    }

    @Test
    void createSupplier_Success() {
        // Arrange
        when(supplierRepo.existsByEmail(supplierDto.getEmail())).thenReturn(false);
        when(supplierRepo.save(any(Supplier.class))).thenReturn(supplier);

        // Act
        Supplier createdSupplier = supplierService.createSupplier(supplierDto);

        // Assert
        assertNotNull(createdSupplier);
        assertEquals(supplier.getName(), createdSupplier.getName());
        verify(supplierRepo, times(1)).existsByEmail(supplierDto.getEmail());
        verify(supplierRepo, times(1)).save(any(Supplier.class));
    }

    @Test
    void createSupplier_EmailAlreadyExists() {
        // Arrange
        when(supplierRepo.existsByEmail(supplierDto.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> supplierService.createSupplier(supplierDto));
        assertEquals("Supplier with this email already exists", exception.getMessage());
        verify(supplierRepo, times(1)).existsByEmail(supplierDto.getEmail());
        verify(supplierRepo, never()).save(any(Supplier.class));
    }

    @Test
    void updateSupplier_Success() {
        // Arrange
        when(supplierRepo.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepo.save(any(Supplier.class))).thenReturn(supplier);

        // Act
        Supplier updatedSupplier = supplierService.updateSupplier(1L, supplierDto);

        // Assert
        assertNotNull(updatedSupplier);
        assertEquals(supplierDto.getName(), updatedSupplier.getName());
        verify(supplierRepo, times(1)).findById(1L);
        verify(supplierRepo, times(1)).save(any(Supplier.class));
    }

    @Test
    void updateSupplier_NotFound() {
        // Arrange
        when(supplierRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> supplierService.updateSupplier(1L, supplierDto));
        assertEquals("Supplier not found", exception.getMessage());
        verify(supplierRepo, times(1)).findById(1L);
        verify(supplierRepo, never()).save(any(Supplier.class));
    }

    @Test
    void getSupplierById_Success() {
        // Arrange
        when(supplierRepo.findById(1L)).thenReturn(Optional.of(supplier));

        // Act
        Optional<Supplier> foundSupplier = supplierService.getSupplierById(1L);

        // Assert
        assertTrue(foundSupplier.isPresent());
        assertEquals(supplier.getName(), foundSupplier.get().getName());
        verify(supplierRepo, times(1)).findById(1L);
    }

    @Test
    void getSupplierById_NotFound() {
        // Arrange
        when(supplierRepo.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Supplier> foundSupplier = supplierService.getSupplierById(1L);

        // Assert
        assertTrue(foundSupplier.isEmpty());
        verify(supplierRepo, times(1)).findById(1L);
    }

    @Test
    void getAllSuppliers_Success() {
        // Arrange
        Supplier anotherSupplier = new Supplier();
        anotherSupplier.setId(2L);
        anotherSupplier.setName("Another Supplier");
        when(supplierRepo.findAll()).thenReturn(Arrays.asList(supplier, anotherSupplier));

        // Act
        List<Supplier> suppliers = supplierService.getAllSuppliers();

        // Assert
        assertEquals(2, suppliers.size());
        verify(supplierRepo, times(1)).findAll();
    }

    @Test
    void deleteSupplier_Success() {
        // Arrange
        when(supplierRepo.findById(1L)).thenReturn(Optional.of(supplier));

        // Act
        supplierService.deleteSupplier(1L);

        // Assert
        verify(supplierRepo, times(1)).findById(1L);
        verify(supplierRepo, times(1)).deleteById(1L);
    }

    @Test
    void deleteSupplier_NotFound() {
        // Arrange
        when(supplierRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        SupplierNotFoundException exception = assertThrows(SupplierNotFoundException.class,
                () -> supplierService.deleteSupplier(1L));
        assertEquals("Supplier not found with ID: 1", exception.getMessage());
        verify(supplierRepo, times(1)).findById(1L);
        verify(supplierRepo, never()).deleteById(1L);
    }
}
