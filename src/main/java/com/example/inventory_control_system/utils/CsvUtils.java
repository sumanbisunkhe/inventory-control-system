package com.example.inventory_control_system.utils;

import com.example.inventory_control_system.dto.OrderDto;
import com.example.inventory_control_system.dto.ProductDto;
import com.example.inventory_control_system.enums.Category;
import com.example.inventory_control_system.enums.OrderStatus;
import com.example.inventory_control_system.enums.Status;
import com.example.inventory_control_system.exceptions.CsvImportException;
import com.example.inventory_control_system.model.Order;
import com.example.inventory_control_system.model.Product;
import com.example.inventory_control_system.model.Supplier;
import com.example.inventory_control_system.repo.OrderRepo;
import com.example.inventory_control_system.repo.ProductRepo;
import com.example.inventory_control_system.repo.SupplierRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CsvUtils {

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private OrderRepo orderRepository;

    @Autowired
    private SupplierRepo supplierRepo;

    public void exportProductsToCSV(List<Product> products, String filePath) {
        try {
            ensureDirectoryExists(filePath);

            try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
                String[] header = {"ID", "Name", "SKU", "Price", "Quantity", "MinStockLevel", "Category", "Status", "SupplierId", "CreatedAt", "UpdatedAt"};
                writer.writeNext(header);

                for (Product product : products) {
                    String[] data = {
                            String.valueOf(product.getId()),
                            product.getName(),
                            product.getSku(),
                            product.getPrice() != null ? product.getPrice().toString() : "N/A",
                            product.getQuantity() != null ? product.getQuantity().toString() : "N/A",
                            product.getMinStockLevel() != null ? product.getMinStockLevel().toString() : "N/A",
                            product.getCategory() != null ? product.getCategory().toString() : "N/A",
                            product.getStatus() != null ? product.getStatus().toString() : "N/A",
                            product.getSupplier() != null ? String.valueOf(product.getSupplier().getId()) : "N/A",
                            product.getCreatedAt() != null ? product.getCreatedAt().toString() : "N/A",
                            product.getUpdatedAt() != null ? product.getUpdatedAt().toString() : "N/A"
                    };
                    writer.writeNext(data);
                }
                log.info("Products exported successfully to: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Error exporting products to CSV", e);
        }
    }



    public List<ProductDto> importProductsFromCSV(String filePath) {
        List<ProductDto> productDtos = new ArrayList<>();
        try (CSVReader reader = new CSVReader(Files.newBufferedReader(Paths.get(filePath)))) {
            String[] line;
            boolean isHeader = true;

            while ((line = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (line.length < 11) continue; // Skip rows with insufficient data

                try {
                    Product product = new Product();
                    product.setName(line[1].trim());
                    product.setSku(line[2].trim());
                    product.setPrice(new BigDecimal(line[3].trim()));
                    product.setQuantity(Integer.parseInt(line[4].trim()));
                    product.setMinStockLevel(!line[5].trim().isEmpty() ? Integer.parseInt(line[5].trim()) : null);
                    product.setCategory(!line[6].trim().isEmpty() ? Category.valueOf(line[6].trim().toUpperCase()) : null);
                    product.setStatus(!line[7].trim().isEmpty() ? Status.valueOf(line[7].trim().toUpperCase()) : null);

                    Long supplierId = Long.parseLong(line[8].trim());
                    Supplier supplier = supplierRepo.findById(supplierId)
                            .orElseThrow(() -> new CsvImportException("Supplier with ID " + supplierId + " not found", null));
                    product.setSupplier(supplier);

                    product.setCreatedAt(!line[9].trim().isEmpty() ? LocalDateTime.parse(line[9].trim()) : null);
                    product.setUpdatedAt(!line[10].trim().isEmpty() ? LocalDateTime.parse(line[10].trim()) : null);

                    product = productRepository.save(product);

                    ProductDto productDto = new ProductDto();
                    productDto.setName(product.getName());
                    productDto.setSku(product.getSku());
                    productDto.setPrice(product.getPrice());
                    productDto.setQuantity(product.getQuantity());
                    productDto.setMinStockLevel(product.getMinStockLevel());
                    productDto.setCategory(product.getCategory());
                    productDto.setStatus(product.getStatus());
                    productDto.setSupplierId(product.getSupplier().getId());

                    productDtos.add(productDto);

                } catch (Exception e) {
                    log.warn("Failed to import product. Skipping entry: {}", (line.length > 1 ? line[1] : "Unknown"), e);
                }
            }
        } catch (Exception e) {
            log.error("Critical error while reading the CSV file", e);
            throw new CsvImportException("Error reading CSV file", e);
        }
        return productDtos;
    }

    public void exportOrdersToCSV(List<Order> orders, String filePath) {
        try {
            ensureDirectoryExists(filePath);

            try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
                String[] header = {"OrderID", "ProductId", "SupplierId", "Quantity", "TotalPrice", "CreatedAt", "Status"};
                writer.writeNext(header);

                for (Order order : orders) {
                    String[] data = {
                            String.valueOf(order.getId()),
                            String.valueOf(order.getProduct().getId()),
                            String.valueOf(order.getSupplier().getId()),
                            order.getQuantity() != null ? order.getQuantity().toString() : "N/A",
                            order.getTotalPrice() != null ? order.getTotalPrice().toString() : "N/A",
                            order.getCreatedAt() != null ? order.getCreatedAt().toString() : "N/A",
                            order.getStatus() != null ? order.getStatus().name() : "N/A"
                    };
                    writer.writeNext(data);
                }
                log.info("Orders exported successfully to: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Error exporting orders to CSV", e);
        }
    }
    public List<OrderDto> importOrdersFromCSV(String filePath) {
        List<OrderDto> orderDtos = new ArrayList<>();
        try (CSVReader reader = new CSVReader(Files.newBufferedReader(Paths.get(filePath)))) {
            String[] line;
            boolean isHeader = true;

            while ((line = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (line.length < 7) continue; // Ensure all fields are present

                try {
                    Order order = new Order();

                    Long productId = Long.parseLong(line[1].trim());
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new CsvImportException("Product with ID " + productId + " not found", null));
                    order.setProduct(product);

                    Long supplierId = Long.parseLong(line[2].trim());
                    Supplier supplier = supplierRepo.findById(supplierId)
                            .orElseThrow(() -> new CsvImportException("Supplier with ID " + supplierId + " not found", null));
                    order.setSupplier(supplier);

                    order.setQuantity(Integer.parseInt(line[3].trim()));
                    order.setTotalPrice(new BigDecimal(line[4].trim()));
                    order.setCreatedAt(LocalDateTime.parse(line[5].trim()));

                    // Handle status with fallback
                    try {
                        order.setStatus(OrderStatus.valueOf(line[6].trim().toUpperCase()));
                    } catch (IllegalArgumentException | NullPointerException e) {
                        log.warn("Invalid or missing status for order. Defaulting to 'PENDING'.");
                        order.setStatus(OrderStatus.PENDING); // Default to PENDING
                    }

                    order = orderRepository.save(order);

                    OrderDto orderDto = new OrderDto();
                    orderDto.setId(order.getId());
                    orderDto.setProductId(order.getProduct().getId());
                    orderDto.setSupplierId(order.getSupplier().getId());
                    orderDto.setQuantity(order.getQuantity());
                    orderDto.setTotalPrice(order.getTotalPrice());
                    orderDto.setStatus(order.getStatus() != null ? order.getStatus().name() : null); 

                    orderDtos.add(orderDto);

                } catch (Exception e) {
                    log.warn("Failed to import order. Skipping entry: {}", (line.length > 0 ? line[0] : "Unknown"), e);
                }
            }
        } catch (Exception e) {
            log.error("Critical error while reading the CSV file", e);
            throw new CsvImportException("Error reading CSV file", e);
        }
        return orderDtos;
    }


    private void ensureDirectoryExists(String filePath) throws IOException {
        Path directoryPath = Paths.get(filePath).getParent();
        if (directoryPath != null && !Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
    }


}
