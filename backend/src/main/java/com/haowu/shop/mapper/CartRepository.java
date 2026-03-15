package com.haowu.shop.mapper;

import com.haowu.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);
    Cart findByUserIdAndProductIdAndSpec(Long userId, Long productId, String spec);
}