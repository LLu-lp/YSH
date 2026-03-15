package com.haowu.shop.service;

import com.haowu.shop.entity.Product;
import com.haowu.shop.entity.Shop;
import com.haowu.shop.entity.ShopFollow;
import com.haowu.shop.mapper.ProductRepository;
import com.haowu.shop.mapper.ShopRepository;
import com.haowu.shop.mapper.ShopFollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopFollowRepository shopFollowRepository;

    public Map<String, Object> getShopDetail(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));
        
        Map<String, Object> detail = new HashMap<>();
        detail.put("id", shop.getId());
        detail.put("merchantId", shop.getMerchantId());
        detail.put("name", shop.getName());
        detail.put("logo", shop.getLogo());
        detail.put("description", shop.getDescription());
        detail.put("address", shop.getAddress());
        detail.put("phone", shop.getPhone());
        detail.put("status", shop.getStatus());
        detail.put("auditStatus", shop.getAuditStatus());
        detail.put("rating", shop.getRating());
        detail.put("salesCount", shop.getSalesCount());
        detail.put("createTime", shop.getCreateTime());
        detail.put("updateTime", shop.getUpdateTime());
        
        return detail;
    }

    public Page<Map<String, Object>> getShopProducts(Long shopId, String sortBy, Pageable pageable) {
        // 验证店铺存在
        shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));

        Page<Product> productPage;
        
        // 根据排序方式调整分页
        if ("sales".equals(sortBy)) {
            productPage = productRepository.findByShopIdAndStatus(shopId, 1, 
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                            Sort.by(Sort.Direction.DESC, "salesCount")));
        } else if ("price_asc".equals(sortBy)) {
            productPage = productRepository.findByShopIdAndStatus(shopId, 1,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                            Sort.by(Sort.Direction.ASC, "price")));
        } else if ("price_desc".equals(sortBy)) {
            productPage = productRepository.findByShopIdAndStatus(shopId, 1,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                            Sort.by(Sort.Direction.DESC, "price")));
        } else if ("newest".equals(sortBy)) {
            productPage = productRepository.findByShopIdAndStatus(shopId, 1,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                            Sort.by(Sort.Direction.DESC, "createTime")));
        } else {
            productPage = productRepository.findByShopIdAndStatus(shopId, 1, pageable);
        }
        
        List<Map<String, Object>> content = new ArrayList<>();
        for (Product product : productPage.getContent()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", product.getId());
            item.put("name", product.getName());
            item.put("description", product.getDescription());
            item.put("price", product.getPrice());
            item.put("originalPrice", product.getOriginalPrice());
            item.put("mainImage", product.getMainImage());
            item.put("salesCount", product.getSalesCount());
            item.put("rating", product.getRating());
            content.add(item);
        }
        
        return new PageImpl<>(content, pageable, productPage.getTotalElements());
    }

    public void followShop(Long userId, Long shopId) {
        // 验证店铺存在
        shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));

        // 检查是否已关注
        if (shopFollowRepository.findByUserIdAndShopId(userId, shopId).isPresent()) {
            throw new RuntimeException("已关注该店铺");
        }

        ShopFollow follow = new ShopFollow();
        follow.setUserId(userId);
        follow.setShopId(shopId);
        shopFollowRepository.save(follow);
    }

    public void unfollowShop(Long userId, Long shopId) {
        ShopFollow follow = shopFollowRepository.findByUserIdAndShopId(userId, shopId)
                .orElseThrow(() -> new RuntimeException("未关注该店铺"));
        shopFollowRepository.delete(follow);
    }

    public boolean isFollowing(Long userId, Long shopId) {
        return shopFollowRepository.findByUserIdAndShopId(userId, shopId).isPresent();
    }
}

