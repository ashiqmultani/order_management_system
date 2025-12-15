package com.bitsquad.ordermanagement.controller;

import com.bitsquad.ordermanagement.dto.CreateOrderRequest;
import com.bitsquad.ordermanagement.dto.UpdateOrderStatusRequest;
import com.bitsquad.ordermanagement.entity.Order;
import com.bitsquad.ordermanagement.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // -------------------------
    // Create a new order
    // -------------------------
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        Order order = orderService.createOrder(request.getUserId());
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    // -------------------------
    // Get order by ID
    // -------------------------
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    // -------------------------
    // Get all orders for a user
    // -------------------------
    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    // -------------------------
    // Update order status
    // -------------------------
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        Order updatedOrder =
                orderService.updateOrderStatus(orderId, request.getStatus());

        return ResponseEntity.ok(updatedOrder);
    }
}
