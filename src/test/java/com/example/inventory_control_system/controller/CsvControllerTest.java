package com.example.inventory_control_system.controller;

import com.example.inventory_control_system.dto.OrderDto;
import com.example.inventory_control_system.dto.ProductDto;
import com.example.inventory_control_system.model.Order;
import com.example.inventory_control_system.model.Product;
import com.example.inventory_control_system.service.OrderService;
import com.example.inventory_control_system.service.ProductService;
import com.example.inventory_control_system.utils.CsvUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvControllerTest {

    @Mock
    private CsvUtils csvUtils;

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CsvController csvController;

    private MockMultipartFile validFile;
    private MockMultipartFile emptyFile;

    @BeforeEach
    void setUp() {
        validFile = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "test data".getBytes()
        );

        emptyFile = new MockMultipartFile(
                "file",
                "empty.csv",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );
    }

    @Test
    void importProducts_ValidFile_ReturnsSuccess() {
        // Arrange
        List<ProductDto> mockProducts = Arrays.asList(
                createMockProductDto(1L),
                createMockProductDto(2L)
        );
        when(csvUtils.importProductsFromCSV(anyString())).thenReturn(mockProducts);

        // Act
        ResponseEntity<?> response = csvController.importProducts(validFile);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("status"));
        assertEquals("Products imported successfully.", responseBody.get("message"));
        assertEquals(mockProducts, responseBody.get("data"));
        verify(csvUtils).importProductsFromCSV(anyString());
    }

    @Test
    void importProducts_EmptyFile_ReturnsBadRequest() {
        // Act
        ResponseEntity<?> response = csvController.importProducts(emptyFile);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("error", responseBody.get("status"));
        assertEquals("The uploaded file is empty.", responseBody.get("message"));
    }

    @Test
    void exportProducts_Success() {
        // Arrange
        List<Product> mockProducts = Arrays.asList(
                createMockProduct(1L),
                createMockProduct(2L)
        );
        when(productService.getAllProducts()).thenReturn(mockProducts);
        doNothing().when(csvUtils).exportProductsToCSV(anyList(), anyString());

        // Act
        ResponseEntity<?> response = csvController.exportProducts(null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("status"));
        assertEquals("Products exported successfully.", responseBody.get("message"));
        assertNotNull(responseBody.get("filePath"));
        verify(productService).getAllProducts();
        verify(csvUtils).exportProductsToCSV(anyList(), anyString());
    }

    @Test
    void importOrders_ValidFile_ReturnsSuccess() {
        // Arrange
        List<OrderDto> mockOrders = Arrays.asList(
                createMockOrderDto(1L),
                createMockOrderDto(2L)
        );
        when(csvUtils.importOrdersFromCSV(anyString())).thenReturn(mockOrders);

        // Act
        ResponseEntity<?> response = csvController.importOrders(validFile);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("status"));
        assertEquals("Orders imported successfully.", responseBody.get("message"));
        assertEquals(mockOrders, responseBody.get("data"));
        verify(csvUtils).importOrdersFromCSV(anyString());
    }

    @Test
    void importOrders_EmptyFile_ReturnsBadRequest() {
        // Act
        ResponseEntity<?> response = csvController.importOrders(emptyFile);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("error", responseBody.get("status"));
        assertEquals("The uploaded file is empty.", responseBody.get("message"));
    }

    @Test
    void exportOrders_Success() {
        // Arrange
        List<Order> mockOrders = Arrays.asList(
                createMockOrder(1L),
                createMockOrder(2L)
        );
        when(orderService.getAllOrders()).thenReturn(mockOrders);
        doNothing().when(csvUtils).exportOrdersToCSV(anyList(), anyString());

        // Act
        ResponseEntity<?> response = csvController.exportOrders(null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("status"));
        assertEquals("Orders exported successfully.", responseBody.get("message"));
        assertNotNull(responseBody.get("filePath"));
        verify(orderService).getAllOrders();
        verify(csvUtils).exportOrdersToCSV(anyList(), anyString());
    }

    @Test
    void exportProducts_WithError_ReturnsInternalServerError() {
        // Arrange
        when(productService.getAllProducts()).thenThrow(new RuntimeException("Test error"));

        // Act
        ResponseEntity<?> response = csvController.exportProducts(null);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("error", responseBody.get("status"));
        assertEquals("Failed to export products.", responseBody.get("message"));
    }

    @Test
    void exportOrders_WithError_ReturnsInternalServerError() {
        // Arrange
        when(orderService.getAllOrders()).thenThrow(new RuntimeException("Test error"));

        // Act
        ResponseEntity<?> response = csvController.exportOrders(null);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("error", responseBody.get("status"));
        assertEquals("Failed to export orders.", responseBody.get("message"));
    }

    private ProductDto createMockProductDto(Long id) {
        ProductDto dto = new ProductDto();
        dto.setId(id);
        dto.setName("Product " + id);
        dto.setPrice(new BigDecimal("99.99"));
        dto.setQuantity(100);
        return dto;
    }

    private Product createMockProduct(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("Product " + id);
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(100);
        return product;
    }

    private OrderDto createMockOrderDto(Long id) {
        OrderDto dto = new OrderDto();
        dto.setId(id);
        dto.setProductId(id);
        dto.setSupplierId(id);
        dto.setQuantity(10);
        dto.setTotalPrice(new BigDecimal("999.90"));
        return dto;
    }

    private Order createMockOrder(Long id) {
        Order order = new Order();
        order.setId(id);
        Product product = new Product();
        product.setId(id);
        order.setProduct(product);
        order.setQuantity(10);
        order.setTotalPrice(new BigDecimal("999.90"));
        return order;
    }
}