package com.example.inventory_control_system.service.impl;

import com.example.inventory_control_system.dto.OrderDto;
import com.example.inventory_control_system.enums.OrderStatus;
import com.example.inventory_control_system.model.Order;
import com.example.inventory_control_system.model.Product;
import com.example.inventory_control_system.model.Supplier;
import com.example.inventory_control_system.repo.OrderRepo;
import com.example.inventory_control_system.repo.ProductRepo;
import com.example.inventory_control_system.service.OrderService;
import com.example.inventory_control_system.service.EmailService;
import com.example.inventory_control_system.utils.CustomEmailMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepository;
    private final ProductRepo productRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public Order createOrder(OrderDto orderDto) {
        Product product = productRepository.findById(orderDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Supplier supplier = product.getSupplier();
        if (supplier == null || !supplier.getId().equals(orderDto.getSupplierId())) {
            throw new IllegalArgumentException("Invalid supplier for the product.");
        }

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(orderDto.getQuantity()));

        Order order = new Order();
        order.setProduct(product);
        order.setSupplier(supplier);
        order.setQuantity(orderDto.getQuantity());
        order.setTotalPrice(totalPrice);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);
        sendOrderEmail(supplier, product, orderDto.getQuantity());
        return savedOrder;
    }


    @Override
    @Transactional
    public Order updateOrder(Long id, OrderDto orderDto) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Product product = productRepository.findById(orderDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Supplier supplier = product.getSupplier();
        if (supplier == null || !supplier.getId().equals(orderDto.getSupplierId())) {
            throw new IllegalArgumentException("Invalid supplier for the product.");
        }

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(orderDto.getQuantity()));

        existingOrder.setProduct(product);
        existingOrder.setSupplier(supplier);
        existingOrder.setQuantity(orderDto.getQuantity());
        existingOrder.setTotalPrice(totalPrice);
        existingOrder.setStatus(OrderStatus.valueOf(orderDto.getStatus().toUpperCase()));

        return orderRepository.save(existingOrder);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        orderRepository.delete(order);
    }

    private void sendOrderEmail(Supplier supplier, Product product, Integer quantity) {
        String emailText = String.format(
                "Dear %s,\n\nA new order has been placed for the product '%s' (SKU: %s).\n\n" +
                        "Quantity: %d\n" +
                        "Please ensure timely delivery.\n\n" +
                        "Thank you,\nInventory Management Team",
                supplier.getName(),
                product.getName(),
                product.getSku(),
                quantity
        );

        CustomEmailMessage emailMessage = new CustomEmailMessage(
                "readymade090@gmail.com",
                supplier.getEmail(),
                new Date(),
                "New Order for Product: " + product.getName(),
                emailText
        );
        // Send the email
        emailService.sendEmail(emailMessage);
    }
}
