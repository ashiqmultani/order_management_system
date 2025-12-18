package com.bitsquad.ordermanagement.service.impl;

import com.bitsquad.ordermanagement.dto.*;
import com.bitsquad.ordermanagement.entity.Order;
import com.bitsquad.ordermanagement.entity.OrderItem;
import com.bitsquad.ordermanagement.entity.OrderStatus;
import com.bitsquad.ordermanagement.exception.InvalidOrderStatusTransitionException;
import com.bitsquad.ordermanagement.exception.OrderNotFoundException;
import com.bitsquad.ordermanagement.exception.ResourceNotFoundException;
import com.bitsquad.ordermanagement.repository.OrderRepository;
import com.bitsquad.ordermanagement.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bitsquad.ordermanagement.entity.OrderStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.CREATED);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setProductName(itemRequest.getProductName());
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(itemRequest.getPrice());
            item.setOrder(order);
            orderItems.add(item);

            totalAmount = totalAmount.add(
                    itemRequest.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
            );
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {} for user: {}", savedOrder.getId(), savedOrder.getUserId());

        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        OrderStatus currentStatus = order.getStatus();

        // ✓ Validate transition before updating
        validateStatusTransition(orderId, currentStatus, newStatus);

        // ✓ Only update if transition is valid
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        return mapToResponse(updatedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserAndStatus(Long userId, OrderStatus status) {
        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, status);
        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderSummaryResponse getOrderSummaryByUser(Long userId) {


        List<Object[]> results = orderRepository.countOrdersByStatus(userId);

        Map<OrderStatus, Long> statusCountMap = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            statusCountMap.put(status, 0L);
        }

        for (Object[] row : results) {
            OrderStatus status = (OrderStatus) row[0];
            Long count = (Long) row[1];
            statusCountMap.put(status, count);
        }

        long totalOrders = orderRepository.countByUserId(userId);

        return OrderSummaryResponse.builder()
                .userId(userId)
                .totalOrders(totalOrders)
                .created(statusCountMap.get(OrderStatus.CREATED))
                .processing(statusCountMap.get(OrderStatus.PROCESSING))
                .completed(statusCountMap.get(OrderStatus.COMPLETED))
                .cancelled(statusCountMap.get(OrderStatus.CANCELLED))
                .build();
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        response.setItems(order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .collect(Collectors.toList())
        );

        return response;
    }
    private void validateStatusTransition(Long orderId, OrderStatus currentStatus, OrderStatus newStatus) {

        // Idempotent: Same status is allowed
        if (currentStatus == newStatus) {
            return;
        }

        switch (currentStatus) {
            case CREATED:
                if (newStatus == PROCESSING || newStatus == COMPLETED || newStatus == CANCELLED) {
                    return;  // ✓ Valid
                }
                break;

            case PROCESSING:
                if (newStatus == COMPLETED || newStatus == CANCELLED) {
                    return;  // ✓ Valid
                }
                break;

            case COMPLETED:
                throw new InvalidOrderStatusTransitionException(
                        "Invalid status transition: " + currentStatus + " → " + newStatus
                );

            case CANCELLED:
                throw new InvalidOrderStatusTransitionException(
                        "Invalid status transition: " + currentStatus + " → " + newStatus
                );
        }

        // Invalid transition
        throw new InvalidOrderStatusTransitionException(
                "Invalid status transition: " + currentStatus + " → " + newStatus
        );
    }

}