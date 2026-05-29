package com.jforce.productmanagmentBackend.repository;

import com.jforce.productmanagmentBackend.entity.Order;
import com.jforce.productmanagmentBackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
}
