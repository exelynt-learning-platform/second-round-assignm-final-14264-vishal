package com.vishal.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vishal.ecommerce.entity.Order;
import com.vishal.ecommerce.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
    
}
