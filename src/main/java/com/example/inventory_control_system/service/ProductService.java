package com.example.inventory_control_system.service;

import com.example.inventory_control_system.dto.ProductDto;
import com.example.inventory_control_system.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product createProduct(ProductDto productDto);

    Product updateProduct(Long id, ProductDto productDto);

    Optional<Product> getProductById(Long id);

    List<Product> getAllProducts();

    void deleteProduct(Long id);

    boolean existsBySku(String sku);
}
