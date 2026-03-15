package com.haowu.shop.controller;

import com.haowu.shop.service.OrderService;
import com.haowu.shop.service.ReviewService;
import com.haowu.shop.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReviewService reviewService;

    // 创建订单（支持购物车结算 cartIds 和立即购买 items 两种模式）
    @SuppressWarnings("unchecked")
    @PostMapping
    public ResponseUtil<Object> createOrder(@RequestBody Map<String, Object> request) {
        try {
            Long userId = getCurrentUserId();
            List<Long> cartIds = null;
            Object rawCartIds = request.get("cartIds");
            if (rawCartIds instanceof List) {
                cartIds = ((List<?>) rawCartIds).stream()
                        .map(id -> Long.valueOf(id.toString()))
                        .collect(java.util.stream.Collectors.toList());
            }
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
            Long addressId = Long.valueOf(request.get("addressId").toString());
            String remark = (String) request.get("remark");
            Long couponId = null;
            if (request.get("couponId") != null) {
                couponId = Long.valueOf(request.get("couponId").toString());
            }
            return ResponseUtil.success(orderService.createOrder(userId, cartIds, items, addressId, remark, couponId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 获取订单列表
    @GetMapping
    public ResponseUtil<Object> getOrderList(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        try {
            Long userId = getCurrentUserId();
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            return ResponseUtil.success(orderService.getOrderList(userId, status, pageable));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 获取订单详情
    @GetMapping("/{id}")
    public ResponseUtil<Object> getOrderDetail(@PathVariable Long id) {
        try {
            return ResponseUtil.success(orderService.getOrderDetail(id));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 支付订单
    @PostMapping("/{id}/pay")
    public ResponseUtil<Object> payOrder(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String payMethod = request.get("payMethod");
            return ResponseUtil.success(orderService.payOrder(id, payMethod));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 取消订单
    @PostMapping("/{id}/cancel")
    public ResponseUtil<Object> cancelOrder(@PathVariable Long id) {
        try {
            return ResponseUtil.success(orderService.cancelOrder(id));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 确认收货
    @PostMapping("/{id}/confirm")
    public ResponseUtil<Object> confirmOrder(@PathVariable Long id) {
        try {
            return ResponseUtil.success(orderService.confirmOrder(id));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 申请退款
    @PostMapping("/{id}/refund")
    public ResponseUtil<Object> applyRefund(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            return ResponseUtil.success(orderService.applyRefund(id, reason));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 提交评价
    @PostMapping("/{id}/review")
    public ResponseUtil<Object> submitReview(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Long userId = getCurrentUserId();
            Long productId = Long.valueOf(request.get("productId").toString());
            Integer rating = (Integer) request.get("rating");
            String content = (String) request.get("content");
            String images = (String) request.get("images");
            return ResponseUtil.success(reviewService.submitReview(userId, id, productId, rating, content, images));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 获取当前登录用户ID
    private Long getCurrentUserId() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
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