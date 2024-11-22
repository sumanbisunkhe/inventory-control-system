package com.example.inventory_control_system.controller;

import com.example.inventory_control_system.dto.ProductDto;
import com.example.inventory_control_system.enums.Category;
import com.example.inventory_control_system.enums.Status;
import com.example.inventory_control_system.exceptions.ProductNotFoundException;
import com.example.inventory_control_system.model.Product;
import com.example.inventory_control_system.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ProductController productController;

    private ProductDto productDto;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productDto = new ProductDto();
        productDto.setName("Test Product");
        productDto.setSku("SKU123");
        productDto.setPrice(BigDecimal.valueOf(99.99));
        productDto.setQuantity(10);
        productDto.setMinStockLevel(5);
        productDto.setCategory(Category.ELECTRONICS);
        productDto.setStatus(Status.ACTIVE);
        productDto.setSupplierId(1L);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSku("SKU123");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setQuantity(10);
        product.setMinStockLevel(5);
        product.setCategory(Category.ELECTRONICS);
        product.setStatus(Status.ACTIVE);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createProduct_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(productService.createProduct(any(ProductDto.class))).thenReturn(product);

        ResponseEntity<Map<String, Object>> response = productController.createProduct(productDto, bindingResult);

        assertEquals(CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product created successfully", response.getBody().get("message"));
        assertEquals(product, response.getBody().get("product"));
        verify(productService, times(1)).createProduct(any(ProductDto.class));
    }

    @Test
    void createProduct_ValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<Map<String, Object>> response = productController.createProduct(productDto, bindingResult);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        verify(productService, never()).createProduct(any(ProductDto.class));
    }

    @Test
    void updateProduct_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(productService.updateProduct(eq(1L), any(ProductDto.class))).thenReturn(product);

        ResponseEntity<Map<String, Object>> response = productController.updateProduct(1L, productDto, bindingResult);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product updated successfully", response.getBody().get("message"));
        assertEquals(product, response.getBody().get("product"));
        verify(productService, times(1)).updateProduct(eq(1L), any(ProductDto.class));
    }

    @Test
    void updateProduct_ValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<Map<String, Object>> response = productController.updateProduct(1L, productDto, bindingResult);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        verify(productService, never()).updateProduct(anyLong(), any(ProductDto.class));
    }

    @Test
    void getProductById_Success() {
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        ResponseEntity<Map<String, Object>> response = productController.getProductById(1L);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("product") instanceof Optional);
        Optional<Product> actualProduct = (Optional<Product>) response.getBody().get("product");
        assertTrue(actualProduct.isPresent());
        assertEquals(product, actualProduct.get());
        verify(productService, times(1)).getProductById(1L);
    }


    @Test
    void getProductById_NotFound() {
        when(productService.getProductById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = productController.getProductById(1L);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product not found with ID: 1", response.getBody().get("message"));
        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void getAllProducts_Success() {
        when(productService.getAllProducts()).thenReturn(Collections.singletonList(product));

        ResponseEntity<Map<String, Object>> response = productController.getAllProducts();

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, ((List<?>) response.getBody().get("products")).size());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void deleteProduct_Success() {
        doNothing().when(productService).deleteProduct(1L);

        ResponseEntity<Map<String, Object>> response = productController.deleteProduct(1L);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product deleted successfully with ID: 1", response.getBody().get("message"));
        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void deleteProduct_NotFound() {
        doThrow(new ProductNotFoundException("Product not found")).when(productService).deleteProduct(1L);

        ResponseEntity<Map<String, Object>> response = productController.deleteProduct(1L);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product not found with ID: 1", response.getBody().get("message"));
        verify(productService, times(1)).deleteProduct(1L);
    }
}
