package com.haowu.shop.mapper;

import com.haowu.shop.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Favorite findByUserIdAndProductId(Long userId, Long productId);
}