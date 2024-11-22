package com.example.inventory_control_system.service;

import com.example.inventory_control_system.dto.OrderDto;
import com.example.inventory_control_system.enums.OrderStatus;
import com.example.inventory_control_system.model.Order;
import com.example.inventory_control_system.model.Product;
import com.example.inventory_control_system.model.Supplier;
import com.example.inventory_control_system.repo.OrderRepo;
import com.example.inventory_control_system.repo.ProductRepo;
import com.example.inventory_control_system.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private EmailService emailService;

    private Product product;
    private Supplier supplier;
    private Order order;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Supplier A");
        supplier.setEmail("supplierA@example.com");

        product = new Product();
        product.setId(1L);
        product.setName("Product A");
        product.setSku("SKU123");
        product.setPrice(new BigDecimal("50.00"));
        product.setSupplier(supplier);

        order = new Order();
        order.setId(1L);
        order.setProduct(product);
        order.setSupplier(supplier);
        order.setQuantity(5);
        order.setTotalPrice(new BigDecimal("250.00"));
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
    }

    @Test
    void testCreateOrder() {
        OrderDto orderDto = new OrderDto(1L, product.getId(), supplier.getId(), 5, null, "PENDING");

        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(orderRepo.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.createOrder(orderDto);

        assertNotNull(createdOrder);
        assertEquals(5, createdOrder.getQuantity());
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        assertEquals(new BigDecimal("250.00"), createdOrder.getTotalPrice());

        verify(orderRepo, times(1)).save(any(Order.class));
        verify(emailService, times(1)).sendEmail(any());
    }

    @Test
    void testCreateOrderWithInvalidSupplier() {
        OrderDto orderDto = new OrderDto(1L, product.getId(), 2L, 5, null, "PENDING");

        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderDto));
        assertEquals("Invalid supplier for the product.", exception.getMessage());
    }

    @Test
    void testUpdateOrder() {
        OrderDto orderDto = new OrderDto(1L, product.getId(), supplier.getId(), 10, null, "COMPLETED");

        when(orderRepo.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(orderRepo.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.updateOrder(order.getId(), orderDto);

        assertNotNull(updatedOrder);
        assertEquals(10, updatedOrder.getQuantity());
        assertEquals(OrderStatus.COMPLETED, updatedOrder.getStatus());
        assertEquals(new BigDecimal("500.00"), updatedOrder.getTotalPrice());

        verify(orderRepo, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrderWithInvalidId() {
        OrderDto orderDto = new OrderDto(1L, product.getId(), supplier.getId(), 10, null, "COMPLETED");

        when(orderRepo.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.updateOrder(2L, orderDto));
        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void testGetAllOrders() {
        when(orderRepo.findAll()).thenReturn(List.of(order));

        List<Order> orders = orderService.getAllOrders();

        assertNotNull(orders);
        assertEquals(1, orders.size());
        verify(orderRepo, times(1)).findAll();
    }

    @Test
    void testGetOrderById() {
        when(orderRepo.findById(order.getId())).thenReturn(Optional.of(order));

        Order fetchedOrder = orderService.getOrderById(order.getId());

        assertNotNull(fetchedOrder);
        assertEquals(order.getId(), fetchedOrder.getId());
        verify(orderRepo, times(1)).findById(order.getId());
    }

    @Test
    void testGetOrderByIdNotFound() {
        when(orderRepo.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById(2L));
        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void testDeleteOrder() {
        when(orderRepo.findById(order.getId())).thenReturn(Optional.of(order));
        doNothing().when(orderRepo).delete(order);

        orderService.deleteOrder(order.getId());

        verify(orderRepo, times(1)).delete(order);
    }

    @Test
    void testDeleteOrderNotFound() {
        when(orderRepo.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.deleteOrder(2L));
        assertEquals("Order not found", exception.getMessage());
    }
}
