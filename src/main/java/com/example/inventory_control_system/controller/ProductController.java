package com.example.inventory_control_system.controller;

import com.example.inventory_control_system.dto.ProductDto;
import com.example.inventory_control_system.exceptions.ProductNotFoundException;
import com.example.inventory_control_system.model.Product;
import com.example.inventory_control_system.service.ProductService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Create a new product
     * @param productDto Product details for creation
     * @param bindingResult Validation errors if any
     * @return JSON response with status and message
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody ProductDto productDto, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        Product createdProduct = productService.createProduct(productDto);
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "Product created successfully");
        response.put("product", createdProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing product
     * @param id Product ID to be updated
     * @param productDto Updated product details
     * @param bindingResult Validation errors if any
     * @return JSON response with status and updated product details
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id,
                                                             @Valid @RequestBody ProductDto productDto,
                                                             BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Product updatedProduct = productService.updateProduct(id, productDto);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Product updated successfully");
            response.put("product", updatedProduct);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Product not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get product details by ID
     * @param id Product ID to fetch details
     * @return JSON response with status and product details or error message
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Product> productDto = productService.getProductById(id);

        if (productDto.isPresent()) {
            response.put("status", HttpStatus.OK.value());
            response.put("product", productDto);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Product not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    /**
     * Get all products
     * @return JSON response with status and list of products
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productService.getAllProducts();
        response.put("status", HttpStatus.OK.value());
        response.put("products", products);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete product by ID
     * @param id Product ID to delete
     * @return JSON response with status and message
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            productService.deleteProduct(id);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Product deleted successfully with ID: " + id);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Product not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
