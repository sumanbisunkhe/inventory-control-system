package com.example.inventory_control_system.dto;

import com.example.inventory_control_system.enums.Category;
import com.example.inventory_control_system.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {
    private Long id;

    @NotBlank(message = "Product name is mandatory")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "SKU is mandatory")
    @Size(max = 255, message = "SKU cannot exceed 255 characters")
    private String sku;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Quantity is mandatory")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    private Integer minStockLevel;

    private Category category;

    private Status status;

    @NotNull(message = "Supplier ID is mandatory")
    private Long supplierId;

}
