package com.haowu.shop.service;

import com.haowu.shop.entity.Product;
import com.haowu.shop.entity.Category;
import com.haowu.shop.entity.Shop;
import com.haowu.shop.entity.Review;
import com.haowu.shop.mapper.ProductRepository;
import com.haowu.shop.mapper.CategoryRepository;
import com.haowu.shop.mapper.ShopRepository;
import com.haowu.shop.mapper.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public Page<Map<String, Object>> getProducts(String keyword, Long categoryId, Pageable pageable) {
        Page<Product> productPage;
        if (keyword != null && !keyword.isEmpty()) {
            productPage = productRepository.findByNameContaining(keyword, pageable);
        } else if (categoryId != null) {
            productPage = productRepository.findByCategoryId(categoryId, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }
        
        List<Map<String, Object>> content = new ArrayList<>();
        for (Product product : productPage.getContent()) {
            Map<String, Object> item = productToMap(product);
            content.add(item);
        }
        
        return new PageImpl<>(content, pageable, productPage.getTotalElements());
    }

    public Map<String, Object> getProductDetail(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("商品不存在"));
        Map<String, Object> detail = productToMap(product);
        
        // 添加评价数据
        List<Review> reviews = reviewRepository.findByProductId(productId);
        List<Map<String, Object>> reviewList = new ArrayList<>();
        for (Review review : reviews) {
            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("id", review.getId());
            reviewMap.put("userName", review.getUserName());
            reviewMap.put("userAvatar", review.getUserAvatar());
            reviewMap.put("rating", review.getRating());
            reviewMap.put("content", review.getContent());
            reviewMap.put("images", review.getImages());
            reviewMap.put("createTime", review.getCreateTime());
            reviewList.add(reviewMap);
        }
        detail.put("reviews", reviewList);
        
        return detail;
    }

    public List<Category> getCategories() {
        return categoryRepository.findByStatus(1);
    }

    public Page<Map<String, Object>> getProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);
        List<Map<String, Object>> content = new ArrayList<>();
        for (Product product : productPage.getContent()) {
            Map<String, Object> item = productToMap(product);
            content.add(item);
        }
        return new PageImpl<>(content, pageable, productPage.getTotalElements());
    }

    private Map<String, Object> productToMap(Product product) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", product.getId());
        item.put("shopId", product.getShopId());
        item.put("categoryId", product.getCategoryId());
        item.put("name", product.getName());
        item.put("description", product.getDescription());
        item.put("price", product.getPrice());
        item.put("originalPrice", product.getOriginalPrice());
        item.put("stock", product.getStock());
        item.put("mainImage", product.getMainImage());
        item.put("images", product.getImages());
        item.put("specs", product.getSpecs());
        item.put("status", product.getStatus());
        item.put("auditStatus", product.getAuditStatus());
        item.put("salesCount", product.getSalesCount());
        item.put("rating", product.getRating());
        item.put("createTime", product.getCreateTime());
        item.put("updateTime", product.getUpdateTime());
        
        String shopName = "官方旗舰店";
        if (product.getShopId() != null) {
            Shop shop = shopRepository.findById(product.getShopId()).orElse(null);
            if (shop != null) {
                shopName = shop.getName();
            }
        }
        item.put("shopName", shopName);
        
        String categoryName = null;
        if (product.getCategoryId() != null) {
            Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
            if (category != null) {
                categoryName = category.getName();
            }
        }
        item.put("categoryName", categoryName);
        
        return item;
    }
}