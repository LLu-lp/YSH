package com.haowu.shop.controller;

import com.haowu.shop.service.CartService;
import com.haowu.shop.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // 获取购物车列表
    @GetMapping
    public ResponseUtil<Object> getCartList() {
        try {
            Long userId = getCurrentUserId();
            return ResponseUtil.success(cartService.getCartList(userId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 添加商品到购物车
    @PostMapping
    public ResponseUtil<Object> addToCart(@RequestBody Map<String, Object> request) {
        try {
            Long userId = getCurrentUserId();
            Long productId = Long.valueOf(request.get("productId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            String spec = (String) request.get("spec");
            return ResponseUtil.success(cartService.addToCart(userId, productId, quantity, spec));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 更新购物车商品数量
    @PutMapping("/{id}")
    public ResponseUtil<Object> updateCartItem(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer quantity = request.get("quantity");
            return ResponseUtil.success(cartService.updateCartItem(id, quantity));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 删除购物车商品
    @DeleteMapping("/{id}")
    public ResponseUtil<Object> deleteCartItem(@PathVariable Long id) {
        try {
            cartService.deleteCartItem(id);
            return ResponseUtil.success("删除成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 批量删除购物车商品
    @DeleteMapping("/batch")
    public ResponseUtil<Object> batchDeleteCart(@RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> cartIds = request.get("ids");
            cartService.batchDeleteCart(cartIds);
            return ResponseUtil.success("删除成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 获取当前登录用户ID
    private Long getCurrentUserId() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new RuntimeException("无法获取用户ID");
    }
}