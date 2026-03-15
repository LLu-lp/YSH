package com.haowu.shop.mapper;

import com.haowu.shop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByShopId(Long shopId, Pageable pageable);
    Page<Order> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);
    Page<Order> findByShopIdAndStatus(Long shopId, Integer status, Pageable pageable);
    Order findByOrderNo(String orderNo);
    List<Order> findByShopIdAndCreateTimeAfter(Long shopId, LocalDateTime createTime);
    
    // 按关键词搜索（订单号或收货人名称）
    @Query("SELECT o FROM Order o WHERE o.shopId = :shopId AND (o.orderNo LIKE %:keyword% OR o.receiverName LIKE %:keyword%)")
    Page<Order> findByShopIdAndKeyword(@Param("shopId") Long shopId, @Param("keyword") String keyword, Pageable pageable);
    
    // 按状态和关键词搜索
    @Query("SELECT o FROM Order o WHERE o.shopId = :shopId AND o.status = :status AND (o.orderNo LIKE %:keyword% OR o.receiverName LIKE %:keyword%)")
    Page<Order> findByShopIdAndStatusAndKeyword(@Param("shopId") Long shopId, @Param("status") Integer status, @Param("keyword") String keyword, Pageable pageable);
}