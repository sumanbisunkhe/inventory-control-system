package com.example.inventory_control_system.controller;

import com.example.inventory_control_system.dto.SupplierDto;
import com.example.inventory_control_system.model.Supplier;
import com.example.inventory_control_system.service.SupplierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SupplierControllerTest {

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private SupplierController supplierController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(supplierController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void createSupplier_ValidRequest_ReturnsCreatedSupplier() throws Exception {
        // Given
        SupplierDto supplierDto = new SupplierDto();
        supplierDto.setName("Test Supplier");
        supplierDto.setEmail("test@example.com");
        supplierDto.setPhoneNumber("123456789");
        supplierDto.setAddress("123 Street");
        supplierDto.setCompanyName("Test Company");

        Supplier supplier = new Supplier(1L, "Test Supplier", "test@example.com", "123456789", "123 Street", "Test Company", null);

        when(supplierService.createSupplier(any(SupplierDto.class))).thenReturn(supplier);

        // When & Then
        mockMvc.perform(post("/api/suppliers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value("Supplier created successfully"))
                .andExpect(jsonPath("$.supplier.id").value(1))
                .andExpect(jsonPath("$.supplier.name").value("Test Supplier"))
                .andExpect(jsonPath("$.supplier.email").value("test@example.com"));
    }

    @Test
    public void updateSupplier_ValidRequest_ReturnsUpdatedSupplier() throws Exception {
        // Given
        SupplierDto supplierDto = new SupplierDto();
        supplierDto.setName("Updated Supplier");
        supplierDto.setEmail("updated@example.com");
        supplierDto.setPhoneNumber("987654321");
        supplierDto.setAddress("456 Avenue");
        supplierDto.setCompanyName("Updated Company");

        Supplier supplier = new Supplier(1L, "Updated Supplier", "updated@example.com", "987654321", "456 Avenue", "Updated Company", null);

        when(supplierService.updateSupplier(anyLong(), any(SupplierDto.class))).thenReturn(supplier);

        // When & Then
        mockMvc.perform(put("/api/suppliers/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Supplier updated successfully"))
                .andExpect(jsonPath("$.supplier.id").value(1))
                .andExpect(jsonPath("$.supplier.name").value("Updated Supplier"))
                .andExpect(jsonPath("$.supplier.email").value("updated@example.com"));
    }

    @Test
    public void getSupplierById_SupplierExists_ReturnsSupplier() throws Exception {
        // Given
        Supplier supplier = new Supplier(1L, "Test Supplier", "test@example.com", "123456789", "123 Street", "Test Company", null);
        when(supplierService.getSupplierById(anyLong())).thenReturn(Optional.of(supplier));

        // When & Then
        mockMvc.perform(get("/api/suppliers/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.supplier.id").value(1))
                .andExpect(jsonPath("$.supplier.name").value("Test Supplier"));
    }

    @Test
    public void getSupplierById_SupplierNotFound_ReturnsNotFound() throws Exception {
        // Given
        when(supplierService.getSupplierById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/suppliers/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Supplier not found"));
    }

    @Test
    public void getAllSuppliers_ReturnsListOfSuppliers() throws Exception {
        // Given
        Supplier supplier1 = new Supplier(1L, "Supplier 1", "supplier1@example.com", "123", "Address 1", "Company 1", null);
        Supplier supplier2 = new Supplier(2L, "Supplier 2", "supplier2@example.com", "456", "Address 2", "Company 2", null);
        when(supplierService.getAllSuppliers()).thenReturn(Arrays.asList(supplier1, supplier2));

        // When & Then
        mockMvc.perform(get("/api/suppliers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.suppliers[0].id").value(1))
                .andExpect(jsonPath("$.suppliers[0].name").value("Supplier 1"))
                .andExpect(jsonPath("$.suppliers[1].id").value(2))
                .andExpect(jsonPath("$.suppliers[1].name").value("Supplier 2"));
    }

    @Test
    public void deleteSupplier_SupplierExists_ReturnsSuccessMessage() throws Exception {
        // Given
        doNothing().when(supplierService).deleteSupplier(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/suppliers/delete/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Supplier deleted successfully with ID: 1"));
    }

    @Test
    public void deleteSupplier_SupplierNotFound_ReturnsNotFound() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Supplier not found")).when(supplierService).deleteSupplier(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/suppliers/delete/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Supplier not found with ID: 1"));
    }
}
