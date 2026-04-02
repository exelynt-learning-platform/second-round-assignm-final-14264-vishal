package com.vishal.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.vishal.ecommerce.entity.Cart;
import com.vishal.ecommerce.entity.User;


public interface CartRepository extends JpaRepository<Cart, Long>{

        Optional<Cart> findByUser(User user);

    
}
