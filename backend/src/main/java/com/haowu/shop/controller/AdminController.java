package com.haowu.shop.controller;

import com.haowu.shop.entity.Category;
import com.haowu.shop.entity.Coupon;
import com.haowu.shop.service.*;
import com.haowu.shop.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AdminOrderService adminOrderService;

    @Autowired
    private AdminProductService adminProductService;

    @Autowired
    private AdminMerchantService adminMerchantService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    // ==================== 统计接口 ====================

    @GetMapping("/stats")
    public ResponseUtil<Object> getPlatformStats() {
        try {
            return ResponseUtil.success(adminService.getPlatformStats());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/stats/sales-trend")
    public ResponseUtil<Object> getSalesTrend(@RequestParam(value = "days", defaultValue = "7") Integer days) {
        try {
            return ResponseUtil.success(adminService.getSalesTrend(days));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/stats/todo-list")
    public ResponseUtil<Object> getTodoList() {
        try {
            return ResponseUtil.success(adminService.getTodoList());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/stats/pending-count")
    public ResponseUtil<Object> getPendingCount() {
        try {
            return ResponseUtil.success(adminService.getPendingCount());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // ==================== 用户管理 ====================

    @GetMapping("/users")
    public ResponseUtil<Object> getUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            return ResponseUtil.success(adminUserService.getUsers(keyword, status, pageable));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PutMapping("/users/{id}/status")
    public ResponseUtil<Object> freezeUser(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer userStatus = request.get("status");
            return ResponseUtil.success(adminUserService.freezeUser(id, userStatus));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // ==================== 商家管理 ====================

    @GetMapping("/merchants")
    public ResponseUtil<Object> getMerchants(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            Object result = adminMerchantService.getMerchants(keyword, status, pageable);
            return ResponseUtil.success(result);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/merchants/{id}")
    public ResponseUtil<Object> getMerchantDetail(@PathVariable Long id) {
        try {
            return ResponseUtil.success(adminMerchantService.getMerchantDetail(id));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PutMapping("/merchants/{id}/review")
    public ResponseUtil<Object> reviewMerchant(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Integer auditStatus = Integer.valueOf(request.get("auditStatus").toString());
            String auditRemark = (String) request.get("auditRemark");
            return ResponseUtil.success(adminMerchantService.reviewMerchant(id, auditStatus, auditRemark));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PutMapping("/merchants/{id}/status")
    public ResponseUtil<Object> freezeMerchant(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer merchantStatus = request.get("status");
            return ResponseUtil.success(adminMerchantService.freezeMerchant(id, merchantStatus));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // ==================== 商品管理 ====================

    @GetMapping("/products")
    public ResponseUtil<Object> getProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "auditStatus", required = false) Integer auditStatus,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            return ResponseUtil.success(adminProductService.getProducts(keyword, auditStatus, pageable));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PutMapping("/products/{id}/review")
    public ResponseUtil<Object> reviewProduct(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Integer auditStatus = Integer.valueOf(request.get("auditStatus").toString());
            String auditRemark = (String) request.get("auditRemark");
            return ResponseUtil.success(adminProductService.reviewProduct(id, auditStatus, auditRemark));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseUtil<Object> deleteProduct(@PathVariable Long id) {
        try {
            adminProductService.deleteProduct(id);
            return ResponseUtil.success("删除成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // ==================== 订单管理 ====================

    @GetMapping("/orders")
    public ResponseUtil<Object> getOrders(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            return ResponseUtil.success(adminOrderService.getOrders(keyword, status, pageable));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @GetMapping("/orders/{id}")
    public ResponseUtil<Object> getOrderDetail(@PathVariable Long id) {
        try {
            return ResponseUtil.success(adminOrderService.getOrderDetail(id));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PutMapping("/orders/{id}/status")
    public ResponseUtil<Object> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer orderStatus = request.get("status");
            return ResponseUtil.success(adminOrderService.updateOrderStatus(id, orderStatus));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PostMapping("/orders/{id}/refund/handle")
    public ResponseUtil<Object> handleRefund(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String action = request.get("action");
            String remark = request.get("remark");
            return ResponseUtil.success(adminOrderService.handleRefund(id, action, remark));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // ==================== 优惠券管理 ====================

    @GetMapping("/coupons")
    public ResponseUtil<Object> getCoupons() {
        try {
            return ResponseUtil.success(couponService.getPlatformCoupons());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PostMapping("/coupons")
    public ResponseUtil<Object> createCoupon(@RequestBody Coupon coupon) {
        try {
            return ResponseUtil.success(couponService.createPlatformCoupon(coupon));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseUtil<Object> deleteCoupon(@PathVariable Long id) {
        try {
            couponService.deletePlatformCoupon(id);
            return ResponseUtil.success("删除成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // ==================== 分类管理 ====================

    @GetMapping("/categories")
    public ResponseUtil<Object> getCategories() {
        try {
            return ResponseUtil.success(categoryService.getAllCategories());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PostMapping("/categories")
    public ResponseUtil<Object> addCategory(@RequestBody Category category) {
        try {
            return ResponseUtil.success(categoryService.addCategory(category));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseUtil<Object> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            return ResponseUtil.success(categoryService.updateCategory(id, category));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseUtil<Object> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseUtil.success("删除成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // ==================== 系统设置 ====================

    @GetMapping("/settings")
    public ResponseUtil<Object> getSettings() {
        try {
            return ResponseUtil.success(systemSettingsService.getSettings());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PutMapping("/settings")
    public ResponseUtil<Object> saveSettings(@RequestBody Map<String, Object> settings) {
        try {
            return ResponseUtil.success(systemSettingsService.saveSettings(settings));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    @PostMapping("/settings/reset")
    public ResponseUtil<Object> resetSettings() {
        try {
            return ResponseUtil.success(systemSettingsService.resetSettings());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }
}
