package com.bitsquad.ordermanagement.repository;

import com.bitsquad.ordermanagement.entity.Order;
import com.bitsquad.ordermanagement.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    @Query("""
    SELECT o.status, COUNT(o)
    FROM OrderEntity o
    WHERE o.userId = :userId
    GROUP BY o.status
""")
    List<Object[]> countOrdersByStatus(@Param("userId") Long userId);

    long countByUserId(Long userId);
}
