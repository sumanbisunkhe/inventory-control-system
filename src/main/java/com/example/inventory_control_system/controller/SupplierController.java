package com.example.inventory_control_system.controller;

import com.example.inventory_control_system.dto.SupplierDto;
import com.example.inventory_control_system.model.Supplier;
import com.example.inventory_control_system.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    /**
     * Create a new supplier
     * @param supplierDto Supplier details for creation
     * @param bindingResult Validation errors if any
     * @return JSON response with status and created supplier
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createSupplier(@Valid @RequestBody SupplierDto supplierDto, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(java.util.stream.Collectors.toList());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        Supplier createdSupplier = supplierService.createSupplier(supplierDto);
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "Supplier created successfully");
        response.put("supplier", createdSupplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing supplier
     * @param id Supplier ID to be updated
     * @param supplierDto Updated supplier details
     * @param bindingResult Validation errors if any
     * @return JSON response with status and updated supplier or error message
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateSupplier(@PathVariable Long id,
                                                              @Valid @RequestBody SupplierDto supplierDto,
                                                              BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(java.util.stream.Collectors.toList());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Supplier updatedSupplier = supplierService.updateSupplier(id, supplierDto);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Supplier updated successfully");
            response.put("supplier", updatedSupplier);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Supplier not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get a supplier by ID
     * @param id Supplier ID to fetch details
     * @return JSON response with status and supplier details or error message
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSupplierById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Supplier> supplier = supplierService.getSupplierById(id);

        if (supplier.isPresent()) {
            response.put("status", HttpStatus.OK.value());
            response.put("supplier", supplier.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Supplier not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get all suppliers
     * @return JSON response with status and list of suppliers
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllSuppliers() {
        Map<String, Object> response = new HashMap<>();
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        response.put("status", HttpStatus.OK.value());
        response.put("suppliers", suppliers);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete supplier by ID
     * @param id Supplier ID to delete
     * @return JSON response with status and message
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteSupplier(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            supplierService.deleteSupplier(id);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Supplier deleted successfully with ID: " + id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Supplier not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
