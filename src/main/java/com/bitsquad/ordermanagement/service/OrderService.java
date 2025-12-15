package com.bitsquad.ordermanagement.service;

import com.bitsquad.ordermanagement.entity.Order;
import com.bitsquad.ordermanagement.entity.OrderStatus;

import java.util.List;

public interface OrderService {

    Order createOrder(Long userId);

    Order getOrderById(Long orderId);

    List<Order> getOrdersByUserId(Long userId);

    Order updateOrderStatus(Long orderId, OrderStatus status);
}
