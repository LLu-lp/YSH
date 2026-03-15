package com.haowu.shop.mapper;

import com.haowu.shop.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findByUserId(Long userId);
    
    List<UserCoupon> findByUserIdAndStatus(Long userId, Integer status);
    
    UserCoupon findByUserIdAndCouponId(Long userId, Long couponId);
}
