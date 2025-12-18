package com.bitsquad.ordermanagement.service;

import com.bitsquad.ordermanagement.dto.CreateOrderRequest;
import com.bitsquad.ordermanagement.dto.OrderResponse;
import com.bitsquad.ordermanagement.dto.OrderSummaryResponse;
import com.bitsquad.ordermanagement.entity.Order;
import com.bitsquad.ordermanagement.entity.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getOrdersByUserId(Long userId);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);
    List<OrderResponse> getOrdersByUserAndStatus(Long userId, OrderStatus status);
    OrderSummaryResponse getOrderSummaryByUser(Long userId);
}
