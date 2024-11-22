package com.example.inventory_control_system.controller;

import com.example.inventory_control_system.dto.OrderDto;
import com.example.inventory_control_system.model.Order;
import com.example.inventory_control_system.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Create a new order
     *
     * @param orderDto      Order details for creation
     * @param bindingResult Validation errors if any
     * @return JSON response with status and created order details
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody OrderDto orderDto, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        Order createdOrder = orderService.createOrder(orderDto);
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "Order created successfully");
        response.put("order", createdOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing order
     *
     * @param id            Order ID to be updated
     * @param orderDto      Updated order details
     * @param bindingResult Validation errors if any
     * @return JSON response with status and updated order details
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderDto orderDto, BindingResult bindingResult) {
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
            Order updatedOrder = orderService.updateOrder(id, orderDto);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Order updated successfully");
            response.put("order", updatedOrder);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get order details by ID
     *
     * @param id Order ID to fetch details
     * @return JSON response with status and order details or error message
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Order order = orderService.getOrderById(id);
            response.put("status", HttpStatus.OK.value());
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get all orders
     *
     * @return JSON response with status and list of orders
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        Map<String, Object> response = new HashMap<>();
        List<Order> orders = orderService.getAllOrders();
        response.put("status", HttpStatus.OK.value());
        response.put("orders", orders);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete an order by ID
     *
     * @param id Order ID to delete
     * @return JSON response with status and message
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.deleteOrder(id);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Order deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
