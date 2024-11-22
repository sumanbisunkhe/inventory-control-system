package com.example.inventory_control_system.service.impl;

import com.example.inventory_control_system.dto.ProductDto;
import com.example.inventory_control_system.enums.Status;
import com.example.inventory_control_system.exceptions.ProductNotFoundException;
import com.example.inventory_control_system.model.Product;
import com.example.inventory_control_system.model.Supplier;
import com.example.inventory_control_system.repo.ProductRepo;
import com.example.inventory_control_system.repo.SupplierRepo;
import com.example.inventory_control_system.service.EmailService;
import com.example.inventory_control_system.service.ProductService;
import com.example.inventory_control_system.utils.CustomEmailMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final SupplierRepo supplierRepo;
    private final EmailService emailService;

    @Autowired
    public ProductServiceImpl(ProductRepo productRepo, SupplierRepo supplierRepo, EmailService emailService) {
        this.productRepo = productRepo;
        this.supplierRepo = supplierRepo;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public Product createProduct(ProductDto productDto) {
        // Validate supplier existence
        Supplier supplier = supplierRepo.findById(productDto.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));

        // Create new Product entity
        Product product = new Product();
        product.setName(productDto.getName());
        product.setSku(productDto.getSku());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setMinStockLevel(productDto.getMinStockLevel());
        product.setCategory(productDto.getCategory());
        product.setStatus(Status.ACTIVE);
        product.setSupplier(supplier);
        product.setCreatedAt(LocalDateTime.now());

        // Save and return the product
        return productRepo.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDto productDto) {
        // Find the existing product
        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Update fields if provided
        if (productDto.getName() != null) {
            existingProduct.setName(productDto.getName());
        }
        if (productDto.getSku() != null) {
            existingProduct.setSku(productDto.getSku());
        }
        if (productDto.getPrice() != null) {
            existingProduct.setPrice(productDto.getPrice());
        }
        if (productDto.getMinStockLevel() != null) {
            existingProduct.setMinStockLevel(productDto.getMinStockLevel());
        }
        if (productDto.getQuantity() != null) {
            existingProduct.setQuantity(productDto.getQuantity());
            // Check if stock level is below the minimum
            checkAndNotifyLowStock(existingProduct);
        }
        if (productDto.getCategory() != null) {
            existingProduct.setCategory(productDto.getCategory());
        }
        if (productDto.getStatus() != null) {
            existingProduct.setStatus(productDto.getStatus());
        }

        // Update supplier if provided
        if (productDto.getSupplierId() != null) {
            Supplier supplier = supplierRepo.findById(productDto.getSupplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
            existingProduct.setSupplier(supplier);
        }

        // Update timestamps
        existingProduct.setUpdatedAt(LocalDateTime.now());

        // Save and return the updated product
        return productRepo.save(existingProduct);
    }

    /**
     * Checks if the product's quantity is below the minimum stock level and sends a notification.
     *
     * @param product the product to check
     */
    private void checkAndNotifyLowStock(Product product) {
        if (product.getQuantity() <= product.getMinStockLevel()) {
            // Send email notification
            sendLowStockEmail(product);
        }
    }

    /**
     * Sends an email notification to the supplier about low stock levels.
     *
     * @param product the product that is low on stock
     */
    private void sendLowStockEmail(Product product) {
        String emailText = String.format(
                "Dear %s,\n\nThe stock for product '%s' (SKU: %s) is critically low. Current quantity: %d, Minimum stock level: %d.\n\nPlease restock as soon as possible.\n\nThank you.",
                product.getSupplier().getName(),
                product.getName(),
                product.getSku(),
                product.getQuantity(),
                product.getMinStockLevel()
        );

        CustomEmailMessage emailMessage = new CustomEmailMessage(
                "readymade090@gmail.com",
                product.getSupplier().getEmail(),
                new Date(),
                "Low Stock Alert: " + product.getName(),
                emailText
        );

        emailService.sendEmail(emailMessage);
    }



    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepo.findById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public void deleteProduct(Long id) {
            // Check if the product exists before trying to delete
            Product product = productRepo.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

            // Delete the product
            productRepo.deleteById(id);

    }

    @Override
    public boolean existsBySku(String sku) {
        return productRepo.findBySku(sku).isPresent();
    }
}
