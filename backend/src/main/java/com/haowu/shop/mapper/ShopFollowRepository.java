package com.haowu.shop.mapper;

import com.haowu.shop.entity.ShopFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopFollowRepository extends JpaRepository<ShopFollow, Long> {
    Optional<ShopFollow> findByUserIdAndShopId(Long userId, Long shopId);
    List<ShopFollow> findByUserId(Long userId);
    long countByShopId(Long shopId);
}

