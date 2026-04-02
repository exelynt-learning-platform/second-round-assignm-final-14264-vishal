package com.vishal.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vishal.ecommerce.entity.Cart;
import com.vishal.ecommerce.entity.User;

import jakarta.persistence.LockModeType;

public interface CartRepository extends JpaRepository<Cart, Long>{

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cart c left join fetch c.items where c.user = :user")
    Cart findByUserForUpdate(@Param("user") User user);

    Cart findByUser(User user);
    
}
