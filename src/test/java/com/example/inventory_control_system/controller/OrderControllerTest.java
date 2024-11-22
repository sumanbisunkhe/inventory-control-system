package com.example.inventory_control_system.controller;

import com.example.inventory_control_system.dto.OrderDto;
import com.example.inventory_control_system.model.Order;
import com.example.inventory_control_system.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private Order order;

    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();

        // Setup order and orderDto for testing
        order = new Order(1L, null, null, 5, BigDecimal.valueOf(100), null, null);
        orderDto = new OrderDto(1L, 1L, 1L, 5, BigDecimal.valueOf(100), "PENDING");
    }

    @Test
    void createOrder_Success() throws Exception {
        when(orderService.createOrder(any(OrderDto.class))).thenReturn(order);

        mockMvc.perform(post("/api/orders/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.order.id").value(order.getId()))
                .andExpect(jsonPath("$.order.quantity").value(order.getQuantity()));

        verify(orderService, times(1)).createOrder(any(OrderDto.class));
    }

    @Test
    void createOrder_BadRequest() throws Exception {
        orderDto.setQuantity(-1);  // Invalid quantity

        mockMvc.perform(post("/api/orders/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.errors").isArray());

        verify(orderService, times(0)).createOrder(any(OrderDto.class));
    }

    @Test
    void updateOrder_Success() throws Exception {
        when(orderService.updateOrder(anyLong(), any(OrderDto.class))).thenReturn(order);

        mockMvc.perform(put("/api/orders/update/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Order updated successfully"))
                .andExpect(jsonPath("$.order.id").value(order.getId()))
                .andExpect(jsonPath("$.order.quantity").value(order.getQuantity()));

        verify(orderService, times(1)).updateOrder(anyLong(), any(OrderDto.class));
    }

    @Test
    void updateOrder_NotFound() throws Exception {
        when(orderService.updateOrder(anyLong(), any(OrderDto.class))).thenThrow(new IllegalArgumentException("Order not found"));

        mockMvc.perform(put("/api/orders/update/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Order not found"));

        verify(orderService, times(1)).updateOrder(anyLong(), any(OrderDto.class));
    }

    @Test
    void getOrderById_Success() throws Exception {
        when(orderService.getOrderById(anyLong())).thenReturn(order);

        mockMvc.perform(get("/api/orders/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.order.id").value(order.getId()))
                .andExpect(jsonPath("$.order.quantity").value(order.getQuantity()));

        verify(orderService, times(1)).getOrderById(anyLong());
    }

    @Test
    void getOrderById_NotFound() throws Exception {
        when(orderService.getOrderById(anyLong())).thenThrow(new IllegalArgumentException("Order not found"));

        mockMvc.perform(get("/api/orders/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Order not found"));

        verify(orderService, times(1)).getOrderById(anyLong());
    }

    @Test
    void getAllOrders_Success() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.orders").isArray())
                .andExpect(jsonPath("$.orders[0].id").value(order.getId()));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void deleteOrder_Success() throws Exception {
        doNothing().when(orderService).deleteOrder(anyLong());

        mockMvc.perform(delete("/api/orders/delete/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Order deleted successfully"));

        verify(orderService, times(1)).deleteOrder(anyLong());
    }

    @Test
    void deleteOrder_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Order not found")).when(orderService).deleteOrder(anyLong());

        mockMvc.perform(delete("/api/orders/delete/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Order not found"));

        verify(orderService, times(1)).deleteOrder(anyLong());
    }
}
