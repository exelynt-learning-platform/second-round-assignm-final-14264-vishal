package com.vishal.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vishal.ecommerce.entity.Cart;
import com.vishal.ecommerce.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long>{

    Cart findByUser(User user);
    
}
