package com.example.inventory_control_system.service;

import com.example.inventory_control_system.dto.SupplierDto;
import com.example.inventory_control_system.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierService {

    Supplier createSupplier(SupplierDto supplierDto);

    Supplier updateSupplier(Long id, SupplierDto supplierDto);

    Optional<Supplier> getSupplierById(Long id);

    List<Supplier> getAllSuppliers();

    void deleteSupplier(Long id);
}
