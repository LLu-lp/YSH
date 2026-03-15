package com.haowu.shop.controller;

import com.haowu.shop.entity.Product;
import com.haowu.shop.service.MerchantProductService;
import com.haowu.shop.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/merchant/products")
public class MerchantProductController {

    @Autowired
    private MerchantProductService merchantProductService;

    // 获取商家商品列表
    @GetMapping
    public ResponseUtil<Object> getMerchantProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        try {
            Long merchantId = getCurrentMerchantId();
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            return ResponseUtil.success(merchantProductService.getMerchantProducts(merchantId, keyword, categoryId, status, pageable));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 创建商品
    @PostMapping
    public ResponseUtil<Object> createProduct(@RequestBody Product product) {
        try {
            Long merchantId = getCurrentMerchantId();
            return ResponseUtil.success(merchantProductService.createProduct(merchantId, product));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 更新商品
    @PutMapping("/{id}")
    public ResponseUtil<Object> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            Long merchantId = getCurrentMerchantId();
            return ResponseUtil.success(merchantProductService.updateProduct(merchantId, id, product));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 删除商品
    @DeleteMapping("/{id}")
    public ResponseUtil<Object> deleteProduct(@PathVariable Long id) {
        try {
            Long merchantId = getCurrentMerchantId();
            merchantProductService.deleteProduct(merchantId, id);
            return ResponseUtil.success("删除成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 上架/下架商品
    @PutMapping("/{id}/status")
    public ResponseUtil<Object> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Long merchantId = getCurrentMerchantId();
            Integer status = request.get("status");
            return ResponseUtil.success(merchantProductService.updateProductStatus(merchantId, id, status));
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