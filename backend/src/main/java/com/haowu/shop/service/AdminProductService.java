package com.haowu.shop.service;

import com.haowu.shop.entity.Category;
import com.haowu.shop.entity.Product;
import com.haowu.shop.entity.Shop;
import com.haowu.shop.mapper.CategoryRepository;
import com.haowu.shop.mapper.ProductRepository;
import com.haowu.shop.mapper.ShopRepository;
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
public class AdminProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ShopRepository shopRepository;

    public Page<Map<String, Object>> getProducts(String keyword, Integer auditStatus, Pageable pageable) {
        Page<Product> productPage;
        
        if (auditStatus != null) {
            productPage = productRepository.findByAuditStatus(auditStatus, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            productPage = productRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }
        
        List<Map<String, Object>> content = new ArrayList<>();
        
        for (Product product : productPage.getContent()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", product.getId());
            item.put("name", product.getName());
            item.put("price", product.getPrice());
            item.put("stock", product.getStock());
            item.put("mainImage", product.getMainImage());
            item.put("description", product.getDescription());
            item.put("status", product.getStatus());
            item.put("auditStatus", product.getAuditStatus());
            item.put("auditRemark", product.getAuditRemark());
            item.put("salesCount", product.getSalesCount());
            item.put("rating", product.getRating());
            item.put("createTime", product.getCreateTime());
            item.put("updateTime", product.getUpdateTime());
            
            String categoryName = "未分类";
            if (product.getCategoryId() != null) {
                Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
                if (category != null) {
                    categoryName = category.getName();
                }
            }
            item.put("categoryName", categoryName);
            
            String shopName = "-";
            if (product.getShopId() != null) {
                Shop shop = shopRepository.findById(product.getShopId()).orElse(null);
                if (shop != null) {
                    shopName = shop.getName();
                }
            }
            item.put("shopName", shopName);
            
            content.add(item);
        }
        
        return new PageImpl<>(content, pageable, productPage.getTotalElements());
    }

    public Product reviewProduct(Long productId, Integer auditStatus, String auditRemark) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("商品不存在"));
        product.setAuditStatus(auditStatus);
        product.setAuditRemark(auditRemark);
        
        // 审核通过时，自动上架商品
        if (auditStatus == 1) {
            product.setStatus(1);
        }
        
        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
}