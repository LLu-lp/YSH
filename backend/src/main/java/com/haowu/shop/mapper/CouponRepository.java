package com.haowu.shop.mapper;

import com.haowu.shop.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    // 获取平台优惠券
    List<Coupon> findByTypeAndStatusAndStartTimeBeforeAndEndTimeAfter(Integer type, Integer status, LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取店铺优惠券
    List<Coupon> findByTypeAndShopIdAndStatusAndStartTimeBeforeAndEndTimeAfter(Integer type, Long shopId, Integer status, LocalDateTime startTime, LocalDateTime endTime);
    
    // 根据店铺ID获取优惠券列表
    List<Coupon> findByShopId(Long shopId);
    
    // 根据店铺ID、状态和有效期获取优惠券列表（用户端领取）
    List<Coupon> findByShopIdAndStatusAndStartTimeBeforeAndEndTimeAfter(Long shopId, Integer status, LocalDateTime startTime, LocalDateTime endTime);
    
    // 根据类型获取优惠券列表
    List<Coupon> findByType(Integer type);
}
