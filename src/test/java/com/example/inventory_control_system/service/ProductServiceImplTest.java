package com.example.inventory_control_system.service;

import com.example.inventory_control_system.dto.ProductDto;
import com.example.inventory_control_system.enums.Category;
import com.example.inventory_control_system.enums.Status;
import com.example.inventory_control_system.exceptions.ProductNotFoundException;
import com.example.inventory_control_system.model.Product;
import com.example.inventory_control_system.model.Supplier;
import com.example.inventory_control_system.repo.ProductRepo;
import com.example.inventory_control_system.repo.SupplierRepo;
import com.example.inventory_control_system.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private SupplierRepo supplierRepo;

    @Mock
    private EmailService emailService;

    private Product product;
    private ProductDto productDto;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Test Supplier");
        supplier.setEmail("supplier@example.com");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSku("SKU123");
        product.setPrice(BigDecimal.valueOf(100.00));
        product.setQuantity(10);
        product.setMinStockLevel(5);
        product.setCategory(Category.ELECTRONICS);
        product.setStatus(Status.ACTIVE);
        product.setSupplier(supplier);
        product.setCreatedAt(LocalDateTime.now());

        productDto = new ProductDto();
        productDto.setName("Test Product");
        productDto.setSku("SKU123");
        productDto.setPrice(BigDecimal.valueOf(100.00));
        productDto.setQuantity(10);
        productDto.setMinStockLevel(5);
        productDto.setCategory(Category.ELECTRONICS);
        productDto.setSupplierId(1L);
    }

    @Test
    void createProduct_Success() {
        when(supplierRepo.findById(productDto.getSupplierId())).thenReturn(Optional.of(supplier));
        when(productRepo.save(any(Product.class))).thenReturn(product);

        Product createdProduct = productService.createProduct(productDto);

        assertNotNull(createdProduct);
        assertEquals(productDto.getName(), createdProduct.getName());
        assertEquals(productDto.getSku(), createdProduct.getSku());
        verify(supplierRepo, times(1)).findById(productDto.getSupplierId());
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_SupplierNotFound() {
        when(supplierRepo.findById(productDto.getSupplierId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> productService.createProduct(productDto));

        assertEquals("Supplier not found", exception.getMessage());
        verify(supplierRepo, times(1)).findById(productDto.getSupplierId());
        verify(productRepo, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_Success() {
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(supplierRepo.findById(productDto.getSupplierId())).thenReturn(Optional.of(supplier));
        when(productRepo.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updateProduct(product.getId(), productDto);

        assertNotNull(updatedProduct);
        assertEquals(productDto.getName(), updatedProduct.getName());
        verify(productRepo, times(1)).findById(product.getId());
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_NotFound() {
        when(productRepo.findById(product.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> productService.updateProduct(product.getId(), productDto));

        assertEquals("Product not found", exception.getMessage());
        verify(productRepo, times(1)).findById(product.getId());
        verify(productRepo, never()).save(any(Product.class));
    }

    @Test
    void getProductById_Success() {
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));

        Optional<Product> foundProduct = productService.getProductById(product.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals(product.getName(), foundProduct.get().getName());
        verify(productRepo, times(1)).findById(product.getId());
    }

    @Test
    void getProductById_NotFound() {
        when(productRepo.findById(product.getId())).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.getProductById(product.getId());

        assertTrue(foundProduct.isEmpty());
        verify(productRepo, times(1)).findById(product.getId());
    }

    @Test
    void getAllProducts_Success() {
        Product anotherProduct = new Product();
        anotherProduct.setId(2L);
        anotherProduct.setName("Another Product");
        anotherProduct.setSku("SKU456");
        when(productRepo.findAll()).thenReturn(Arrays.asList(product, anotherProduct));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        verify(productRepo, times(1)).findAll();
    }

    @Test
    void deleteProduct_Success() {
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));

        productService.deleteProduct(product.getId());

        verify(productRepo, times(1)).findById(product.getId());
        verify(productRepo, times(1)).deleteById(product.getId());
    }

    @Test
    void deleteProduct_NotFound() {
        when(productRepo.findById(product.getId())).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> productService.deleteProduct(product.getId()));

        assertEquals("Product not found with ID: " + product.getId(), exception.getMessage());
        verify(productRepo, times(1)).findById(product.getId());
        verify(productRepo, never()).deleteById(product.getId());
    }

    @Test
    void existsBySku_Success() {
        when(productRepo.findBySku(product.getSku())).thenReturn(Optional.of(product));

        boolean exists = productService.existsBySku(product.getSku());

        assertTrue(exists);
        verify(productRepo, times(1)).findBySku(product.getSku());
    }

    @Test
    void existsBySku_NotFound() {
        when(productRepo.findBySku(product.getSku())).thenReturn(Optional.empty());

        boolean exists = productService.existsBySku(product.getSku());

        assertFalse(exists);
        verify(productRepo, times(1)).findBySku(product.getSku());
    }
}
