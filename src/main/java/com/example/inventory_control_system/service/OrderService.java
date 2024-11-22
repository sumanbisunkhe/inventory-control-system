package com.example.inventory_control_system.service;

import com.example.inventory_control_system.dto.OrderDto;
import com.example.inventory_control_system.model.Order;

import java.util.List;

public interface OrderService {

    Order createOrder(OrderDto orderDto);

    List<Order> getAllOrders();

    Order getOrderById(Long id);

    Order updateOrder(Long id, OrderDto orderDto);

    void deleteOrder(Long id);
}
