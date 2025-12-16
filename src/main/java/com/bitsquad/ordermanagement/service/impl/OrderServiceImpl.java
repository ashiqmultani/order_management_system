package com.bitsquad.ordermanagement.service.impl;

import com.bitsquad.ordermanagement.entity.Order;
import com.bitsquad.ordermanagement.entity.OrderStatus;
import com.bitsquad.ordermanagement.exception.OrderNotFoundException;
import com.bitsquad.ordermanagement.repository.OrderRepository;
import com.bitsquad.ordermanagement.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrder(Long userId) {
        Order order = new Order();
        order.setUserId(userId);
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);

        // Prevent updates once order is completed or cancelled
        if (order.getStatus() == OrderStatus.COMPLETED ||
                order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Order status cannot be updated once completed or cancelled"
            );
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}
