package com.haowu.shop.controller;

import com.haowu.shop.service.ShopService;
import com.haowu.shop.service.CouponService;
import com.haowu.shop.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private CouponService couponService;

    // 获取店铺详情
    @GetMapping("/{id}")
    public ResponseUtil<Object> getShopDetail(@PathVariable Long id) {
        try {
            return ResponseUtil.success(shopService.getShopDetail(id));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 获取店铺商品列表
    @GetMapping("/{shopId}/products")
    public ResponseUtil<Object> getShopProducts(
            @PathVariable Long shopId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "12") Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy
    ) {
        try {
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            return ResponseUtil.success(shopService.getShopProducts(shopId, sortBy, pageable));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 关注店铺
    @PostMapping("/{shopId}/follow")
    public ResponseUtil<Object> followShop(@PathVariable Long shopId) {
        try {
            Long userId = getCurrentUserId();
            shopService.followShop(userId, shopId);
            return ResponseUtil.success("关注成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 取消关注店铺
    @PostMapping("/{shopId}/unfollow")
    public ResponseUtil<Object> unfollowShop(@PathVariable Long shopId) {
        try {
            Long userId = getCurrentUserId();
            shopService.unfollowShop(userId, shopId);
            return ResponseUtil.success("取消关注成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 检查是否关注
    @GetMapping("/{shopId}/is-following")
    public ResponseUtil<Object> isFollowing(@PathVariable Long shopId) {
        try {
            Long userId = getCurrentUserId();
            boolean following = shopService.isFollowing(userId, shopId);
            return ResponseUtil.success(following);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 获取店铺优惠券列表
    @GetMapping("/{shopId}/coupons")
    public ResponseUtil<Object> getShopCoupons(@PathVariable Long shopId) {
        try {
            return ResponseUtil.success(couponService.getMerchantCouponsForUser(shopId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 获取当前登录用户ID
    private Long getCurrentUserId() {
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new RuntimeException("无法获取用户ID");
    }
}

