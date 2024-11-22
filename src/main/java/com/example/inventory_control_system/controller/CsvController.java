package com.example.inventory_control_system.controller;

import com.example.inventory_control_system.dto.OrderDto;
import com.example.inventory_control_system.dto.ProductDto;
import com.example.inventory_control_system.model.Order;
import com.example.inventory_control_system.model.Product;
import com.example.inventory_control_system.service.OrderService;
import com.example.inventory_control_system.service.ProductService;
import com.example.inventory_control_system.utils.CsvUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/csv")
public class CsvController {

    @Autowired
    private CsvUtils csvUtils;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    /**
     * Import products from a CSV file.
     *
     * @param file Multipart file containing the product data.
     * @return ResponseEntity with the imported products or error details.
     */
    @PostMapping("/import/products")
    public ResponseEntity<?> importProducts(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("status", "error");
            response.put("message", "The uploaded file is empty.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String filePath = saveFileToTemp(file);
            List<ProductDto> products = csvUtils.importProductsFromCSV(filePath);
            response.put("status", "success");
            response.put("message", "Products imported successfully.");
            response.put("data", products);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error importing products from CSV", e);
            response.put("status", "error");
            response.put("message", "Failed to import products.");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Export products to a CSV file.
     *
     * @param filePath File path where the CSV should be saved.
     * @return ResponseEntity with success message or error details.
     */
    @PostMapping("/export/products")
    public ResponseEntity<?> exportProducts(@RequestBody(required = false) String filePath) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Default export directory
            String exportDir = "D:\\JAVA\\inventory-control-system-exports";
            if (filePath == null || filePath.isBlank()) {
                filePath = exportDir + "\\products-" + System.currentTimeMillis() + ".csv";
            }

            List<Product> products = productService.getAllProducts();
            csvUtils.exportProductsToCSV(products, filePath);

            response.put("status", "success");
            response.put("message", "Products exported successfully.");
            response.put("filePath", filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error exporting products to CSV", e);
            response.put("status", "error");
            response.put("message", "Failed to export products.");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Import orders from a CSV file.
     *
     * @param file Multipart file containing the order data.
     * @return ResponseEntity with the imported orders or error details.
     */
    @PostMapping("/import/orders")
    public ResponseEntity<?> importOrders(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("status", "error");
            response.put("message", "The uploaded file is empty.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String filePath = saveFileToTemp(file);
            List<OrderDto> orders = csvUtils.importOrdersFromCSV(filePath);
            response.put("status", "success");
            response.put("message", "Orders imported successfully.");
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error importing orders from CSV", e);
            response.put("status", "error");
            response.put("message", "Failed to import orders.");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Export orders to a CSV file.
     *
     * @param filePath File path where the CSV should be saved.
     * @return ResponseEntity with success message or error details.
     */
    @PostMapping("/export/orders")
    public ResponseEntity<?> exportOrders(@RequestBody(required = false) String filePath) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Default export directory
            String exportDir = "D:\\JAVA\\inventory-control-system-exports";
            if (filePath == null || filePath.isBlank()) {
                filePath = exportDir + "\\orders-" + System.currentTimeMillis() + ".csv";
            }

            List<Order> orders = orderService.getAllOrders();
            csvUtils.exportOrdersToCSV(orders, filePath);

            response.put("status", "success");
            response.put("message", "Orders exported successfully.");
            response.put("filePath", filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error exporting orders to CSV", e);
            response.put("status", "error");
            response.put("message", "Failed to export orders.");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Helper method to save an uploaded file to a temporary location.
     *
     * @param file Multipart file to save.
     * @return Absolute path to the saved file.
     * @throws Exception if an error occurs during file saving.
     */
    private String saveFileToTemp(MultipartFile file) throws Exception {
        String tempDir = System.getProperty("java.io.tmpdir");
        Path tempFile = Files.createTempFile(Paths.get(tempDir), "uploaded-", ".csv");
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        return tempFile.toString();
    }
}
