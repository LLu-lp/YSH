package com.haowu.shop.mapper;

import com.haowu.shop.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    List<Refund> findByOrderId(Long orderId);
    List<Refund> findByUserId(Long userId);
    List<Refund> findByStatus(Integer status);
}