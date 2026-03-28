package com.vishal.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vishal.ecommerce.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{

    
    
}
