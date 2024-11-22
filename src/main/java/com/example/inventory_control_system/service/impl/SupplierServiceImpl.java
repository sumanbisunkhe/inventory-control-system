package com.example.inventory_control_system.service.impl;

import com.example.inventory_control_system.dto.SupplierDto;
import com.example.inventory_control_system.exceptions.ProductNotFoundException;
import com.example.inventory_control_system.exceptions.SupplierNotFoundException;
import com.example.inventory_control_system.model.Product;
import com.example.inventory_control_system.model.Supplier;
import com.example.inventory_control_system.repo.SupplierRepo;
import com.example.inventory_control_system.repo.UserRepo;
import com.example.inventory_control_system.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepo supplierRepo;


    @Autowired
    public SupplierServiceImpl(SupplierRepo supplierRepo) {
        this.supplierRepo = supplierRepo;
    }




    @Override
    @Transactional
    public Supplier createSupplier(SupplierDto supplierDto) {
        // Check if supplier email already exists
        if (supplierRepo.existsByEmail(supplierDto.getEmail())) {
            throw new IllegalArgumentException("Supplier with this email already exists");
        }

        // Create Supplier entity and populate from SupplierDto
        Supplier supplier = new Supplier();
        supplier.setName(supplierDto.getName());
        supplier.setEmail(supplierDto.getEmail());
        supplier.setPhoneNumber(supplierDto.getPhoneNumber());
        supplier.setAddress(supplierDto.getAddress());
        supplier.setCompanyName(supplierDto.getCompanyName());

        return supplierRepo.save(supplier);
    }

    @Override
    @Transactional
    public Supplier updateSupplier(Long id, SupplierDto supplierDto) {
        // Check if supplier exists
        Supplier existingSupplier = supplierRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Supplier not found"));

        // Update fields
        existingSupplier.setName(supplierDto.getName());
        existingSupplier.setEmail(supplierDto.getEmail());
        existingSupplier.setPhoneNumber(supplierDto.getPhoneNumber());
        existingSupplier.setAddress(supplierDto.getAddress());
        existingSupplier.setCompanyName(supplierDto.getCompanyName());

        return supplierRepo.save(existingSupplier);
    }

    @Override
    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepo.findById(id);
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepo.findAll();
    }

    @Override
    public void deleteSupplier(Long id) {
        // Check if the Supplier exists before trying to delete
        Supplier supplier = supplierRepo.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with ID: " + id));

        // Delete the product
        supplierRepo.deleteById(id);
    }
}
