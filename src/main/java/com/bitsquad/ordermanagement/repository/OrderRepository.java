package com.bitsquad.ordermanagement.repository;

import com.bitsquad.ordermanagement.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Fetch all orders for a specific user
    List<Order> findByUserId(Long userId);
}
