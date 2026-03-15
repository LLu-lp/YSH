package com.haowu.shop.service;

import com.haowu.shop.entity.Coupon;
import com.haowu.shop.entity.Merchant;
import com.haowu.shop.entity.UserCoupon;
import com.haowu.shop.mapper.CouponRepository;
import com.haowu.shop.mapper.MerchantRepository;
import com.haowu.shop.mapper.UserCouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    // 获取用户优惠券列表（包含优惠券详情）
    public List<Map<String, Object>> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (UserCoupon uc : userCoupons) {
            Coupon coupon = couponRepository.findById(uc.getCouponId()).orElse(null);
            if (coupon != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", uc.getId());
                item.put("couponId", uc.getCouponId());
                item.put("userId", uc.getUserId());
                item.put("status", uc.getStatus());
                item.put("receiveTime", uc.getReceiveTime());
                item.put("useTime", uc.getUseTime());
                item.put("name", coupon.getName());
                item.put("value", coupon.getValue());
                item.put("minAmount", coupon.getMinAmount());
                item.put("startTime", coupon.getStartTime());
                item.put("endTime", coupon.getEndTime());
                item.put("type", coupon.getType());
                result.add(item);
            }
        }
        
        return result;
    }

    // 获取用户未使用的优惠券
    public List<UserCoupon> getAvailableCoupons(Long userId) {
        return userCouponRepository.findByUserIdAndStatus(userId, 1);
    }

    // 领取优惠券
    public UserCoupon receiveCoupon(Long userId, Long couponId) {
        // 检查优惠券是否存在
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("优惠券不存在"));

        // 检查优惠券是否有效
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStatus() != 1 || now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new RuntimeException("优惠券已失效");
        }

        // 检查优惠券是否还有剩余
        if (coupon.getRemainingCount() <= 0) {
            throw new RuntimeException("优惠券已领完");
        }

        // 检查用户是否已领取
        UserCoupon existingUserCoupon = userCouponRepository.findByUserIdAndCouponId(userId, couponId);
        if (existingUserCoupon != null) {
            throw new RuntimeException("您已领取过该优惠券");
        }

        // 减少优惠券剩余数量
        coupon.setRemainingCount(coupon.getRemainingCount() - 1);
        couponRepository.save(coupon);

        // 创建用户优惠券记录
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(1); // 未使用

        return userCouponRepository.save(userCoupon);
    }

    // 获取可领取的优惠券列表（包括平台优惠券和商家优惠券）
    public List<Coupon> getAvailableCouponsForReceive(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        // 获取平台优惠券
        List<Coupon> platformCoupons = couponRepository.findByTypeAndStatusAndStartTimeBeforeAndEndTimeAfter(1, 1, now, now);
        // 获取商家优惠券
        List<Coupon> merchantCoupons = couponRepository.findByTypeAndStatusAndStartTimeBeforeAndEndTimeAfter(2, 1, now, now);
        
        // 合并两个列表
        List<Coupon> allCoupons = new ArrayList<>();
        allCoupons.addAll(platformCoupons);
        allCoupons.addAll(merchantCoupons);
        
        // 过滤掉用户已领取的优惠券和库存为0的优惠券
        allCoupons.removeIf(coupon -> {
            UserCoupon userCoupon = userCouponRepository.findByUserIdAndCouponId(userId, coupon.getId());
            return userCoupon != null || coupon.getRemainingCount() <= 0;
        });
        return allCoupons;
    }
    
    // 获取商家优惠券列表（用户端查看）
    public List<Map<String, Object>> getMerchantCouponsForUser(Long shopId) {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> coupons = couponRepository.findByShopIdAndStatusAndStartTimeBeforeAndEndTimeAfter(shopId, 1, now, now);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Coupon coupon : coupons) {
            if (coupon.getRemainingCount() > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", coupon.getId());
                item.put("name", coupon.getName());
                item.put("value", coupon.getValue());
                item.put("minAmount", coupon.getMinAmount());
                item.put("startTime", coupon.getStartTime());
                item.put("endTime", coupon.getEndTime());
                item.put("type", coupon.getType());
                item.put("remainingCount", coupon.getRemainingCount());
                item.put("totalCount", coupon.getTotalCount());
                item.put("shopId", coupon.getShopId());
                result.add(item);
            }
        }
        
        return result;
    }

    // 使用优惠券
    public void useCoupon(Long userCouponId, Long userId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new RuntimeException("优惠券不存在"));

        // 检查是否属于当前用户
        if (!userCoupon.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此优惠券");
        }

        // 检查优惠券状态
        if (userCoupon.getStatus() != 1) {
            throw new RuntimeException("优惠券已使用或已过期");
        }

        // 检查优惠券是否过期
        Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
                .orElseThrow(() -> new RuntimeException("优惠券不存在"));
        if (LocalDateTime.now().isAfter(coupon.getEndTime())) {
            userCoupon.setStatus(3); // 已过期
            userCouponRepository.save(userCoupon);
            throw new RuntimeException("优惠券已过期");
        }

        // 更新优惠券状态
        userCoupon.setStatus(2); // 已使用
        userCoupon.setUseTime(LocalDateTime.now());
        userCouponRepository.save(userCoupon);
    }

    // 商家优惠券管理
    // 获取商家优惠券列表
    public List<Coupon> getMerchantCoupons(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        return couponRepository.findByShopId(merchant.getShopId());
    }

    // 创建商家优惠券
    public Coupon createMerchantCoupon(Long merchantId, Coupon coupon) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }

        // 设置优惠券类型为店铺券
        coupon.setType(2);
        coupon.setShopId(merchant.getShopId());
        coupon.setRemainingCount(coupon.getTotalCount());
        coupon.setStatus(1); // 启用状态

        return couponRepository.save(coupon);
    }

    // 删除商家优惠券
    public void deleteMerchantCoupon(Long merchantId, Long couponId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("优惠券不存在"));

        // 检查是否是该商家的优惠券
        if (!coupon.getShopId().equals(merchant.getShopId())) {
            throw new RuntimeException("无权操作此优惠券");
        }

        couponRepository.delete(coupon);
    }

    // 平台优惠券管理
    // 获取平台优惠券列表
    public List<Coupon> getPlatformCoupons() {
        return couponRepository.findByType(1);
    }

    // 创建平台优惠券
    public Coupon createPlatformCoupon(Coupon coupon) {
        // 设置优惠券类型为平台券
        coupon.setType(1);
        coupon.setShopId(null);
        coupon.setRemainingCount(coupon.getTotalCount());
        coupon.setStatus(1); // 启用状态

        return couponRepository.save(coupon);
    }

    // 删除平台优惠券
    public void deletePlatformCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("优惠券不存在"));

        // 检查是否是平台优惠券
        if (coupon.getType() != 1) {
            throw new RuntimeException("无权操作此优惠券");
        }

        couponRepository.delete(coupon);
    }
}
