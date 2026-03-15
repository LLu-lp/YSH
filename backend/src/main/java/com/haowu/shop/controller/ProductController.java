package com.haowu.shop.controller;

import com.haowu.shop.service.BannerService;
import com.haowu.shop.service.ProductService;
import com.haowu.shop.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.PageRequest; 
import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.Sort; 
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private BannerService bannerService;

    // 获取商品列表
    @GetMapping("/products")
    public ResponseUtil<Object> getProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "12") Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder
    ) {
        try {
            Pageable pageable;
            if (sortBy != null) {
                Sort sort = sortOrder.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
                pageable = PageRequest.of(page - 1, pageSize, sort);
            } else {
                pageable = PageRequest.of(page - 1, pageSize);
            }
            Page<?> products = productService.getProducts(keyword, categoryId, pageable);
            return ResponseUtil.success(products);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 获取商品详情
    @GetMapping("/products/{id}")
    public ResponseUtil<Object> getProductDetail(@PathVariable Long id) {
        try {
            return ResponseUtil.success(productService.getProductDetail(id));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 获取商品分类列表
    @GetMapping("/categories")
    public ResponseUtil<Object> getCategories() {
        try {
            return ResponseUtil.success(productService.getCategories());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 获取首页轮播图列表
    @GetMapping("/banners")
    public ResponseUtil<Object> getBanners() {
        try {
            return ResponseUtil.success(bannerService.getBanners());
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }
}