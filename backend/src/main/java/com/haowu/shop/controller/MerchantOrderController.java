package com.haowu.shop.controller;

import com.haowu.shop.service.MerchantOrderService;
import com.haowu.shop.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/merchant/orders")
public class MerchantOrderController {

    @Autowired
    private MerchantOrderService merchantOrderService;

    // 获取商家订单列表
    @GetMapping
    public ResponseUtil<Object> getMerchantOrders(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        try {
            Long merchantId = getCurrentMerchantId();
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            return ResponseUtil.success(merchantOrderService.getMerchantOrders(merchantId, status, keyword, pageable));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 获取订单详情
    @GetMapping("/{id}")
    public ResponseUtil<Object> getOrderDetail(@PathVariable Long id) {
        try {
            Long merchantId = getCurrentMerchantId();
            return ResponseUtil.success(merchantOrderService.getOrderDetail(merchantId, id));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 发货
    @PostMapping("/{id}/ship")
    public ResponseUtil<Object> shipOrder(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Long merchantId = getCurrentMerchantId();
            String expressCompany = request.get("expressCompany");
            String expressNo = request.get("expressNo");
            return ResponseUtil.success(merchantOrderService.shipOrder(merchantId, id, expressCompany, expressNo));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 处理退款
    @PostMapping("/{id}/refund/handle")
    public ResponseUtil<Object> handleRefund(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Long merchantId = getCurrentMerchantId();
            String action = request.get("action");
            String remark = request.get("remark");
            return ResponseUtil.success(merchantOrderService.handleRefund(merchantId, id, action, remark));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 获取当前登录商家ID
    private Long getCurrentMerchantId() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new RuntimeException("无法获取商家ID");
    }
}