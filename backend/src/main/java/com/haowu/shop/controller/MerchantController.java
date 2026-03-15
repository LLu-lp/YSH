package com.haowu.shop.controller;

import com.haowu.shop.entity.Coupon;
import com.haowu.shop.service.CouponService;
import com.haowu.shop.service.MerchantService;
import com.haowu.shop.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CouponService couponService;

    @GetMapping("/shop")
    public ResponseUtil<Object> getShopInfo() {
        try {
            Long merchantId = getCurrentMerchantId();
            return ResponseUtil.success(merchantService.getShopInfo(merchantId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PutMapping("/shop")
    public ResponseUtil<Object> updateShopInfo(@RequestBody Map<String, String> request) {
        try {
            Long merchantId = getCurrentMerchantId();
            String name = request.get("name");
            String logo = request.get("logo");
            String description = request.get("description");
            String address = request.get("address");
            String phone = request.get("phone");
            return ResponseUtil.success(merchantService.updateShopInfo(merchantId, name, logo, description, address, phone));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseUtil<Object> getShopStats(@RequestParam(value = "period", defaultValue = "today") String period) {
        try {
            Long merchantId = getCurrentMerchantId();
            return ResponseUtil.success(merchantService.getShopStats(merchantId, period));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/stats/overview")
    public ResponseUtil<Object> getShopOverview() {
        try {
            Long merchantId = getCurrentMerchantId();
            return ResponseUtil.success(merchantService.getShopOverview(merchantId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/stats/sales-trend")
    public ResponseUtil<Object> getSalesTrend(@RequestParam(value = "days", defaultValue = "7") Integer days) {
        try {
            Long merchantId = getCurrentMerchantId();
            return ResponseUtil.success(merchantService.getSalesTrend(merchantId, days));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/stats/category-ratio")
    public ResponseUtil<Object> getCategoryRatio() {
        try {
            Long merchantId = getCurrentMerchantId();
            // 直接返回列表，前端会处理
            return ResponseUtil.success(merchantService.getCategoryRatio(merchantId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/coupons")
    public ResponseUtil<Object> getMerchantCoupons() {
        try {
            Long merchantId = getCurrentMerchantId();
            return ResponseUtil.success(couponService.getMerchantCoupons(merchantId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PostMapping("/coupons")
    public ResponseUtil<Object> createCoupon(@RequestBody Coupon coupon) {
        try {
            Long merchantId = getCurrentMerchantId();
            return ResponseUtil.success(couponService.createMerchantCoupon(merchantId, coupon));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseUtil<Object> deleteCoupon(@PathVariable Long id) {
        try {
            Long merchantId = getCurrentMerchantId();
            couponService.deleteMerchantCoupon(merchantId, id);
            return ResponseUtil.success("删除成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/customers")
    public ResponseUtil<Object> getMerchantCustomers(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            Long merchantId = getCurrentMerchantId();
            return ResponseUtil.success(merchantService.getMerchantCustomers(merchantId, page, pageSize));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    private Long getCurrentMerchantId() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new RuntimeException("无法获取商家ID");
    }
}
