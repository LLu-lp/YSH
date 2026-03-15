package com.haowu.shop.service;

import com.haowu.shop.entity.Merchant;
import com.haowu.shop.entity.Shop;
import com.haowu.shop.mapper.MerchantRepository;
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
public class AdminMerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    public Page<Map<String, Object>> getMerchants(String keyword, Integer status, Pageable pageable) {
        // 获取所有商家（不分页）
        List<Merchant> allMerchants = merchantRepository.findAll();
        
        List<Map<String, Object>> filteredContent = new ArrayList<>();
        
        for (Merchant merchant : allMerchants) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", merchant.getId());
            item.put("username", merchant.getUsername());
            item.put("phone", merchant.getPhone());
            item.put("email", merchant.getEmail());
            item.put("createTime", merchant.getCreateTime());
            item.put("shopId", merchant.getShopId());
            
            // 获取店铺的审核状态和运营状态
            Integer auditStatus = 0; // 默认待审核
            Integer shopStatus = 1; // 默认正常
            if (merchant.getShopId() != null) {
                Shop shop = shopRepository.findById(merchant.getShopId()).orElse(null);
                if (shop != null) {
                    auditStatus = shop.getAuditStatus();
                    shopStatus = shop.getStatus();
                    item.put("shopName", shop.getName());
                    item.put("productCount", productRepository.countByShopId(shop.getId()));
                    item.put("totalSales", shop.getSalesCount() != null ? shop.getSalesCount() : 0);
                }
            } else {
                item.put("shopName", null);
                item.put("productCount", 0);
                item.put("totalSales", 0);
            }
            
            // 如果审核未通过，显示审核状态；否则显示运营状态
            Integer displayStatus = auditStatus == 1 ? shopStatus : auditStatus;
            item.put("status", displayStatus);
            item.put("auditStatus", auditStatus);
            item.put("shopStatus", shopStatus);
            
            // 按状态过滤
            if (status != null) {
                // 如果是审核状态（0=待审核，2=已拒绝），按auditStatus过滤
                if (status == 0 || status == 2) {
                    if (!auditStatus.equals(status)) {
                        continue;
                    }
                } else {
                    // 否则按shopStatus过滤（1=正常，3=封禁）
                    if (!shopStatus.equals(status)) {
                        continue;
                    }
                }
            }
            
            // 按关键词过滤
            if (keyword != null && !keyword.isEmpty()) {
                String lowerKeyword = keyword.toLowerCase();
                if (!merchant.getUsername().toLowerCase().contains(lowerKeyword) &&
                    (merchant.getPhone() == null || !merchant.getPhone().contains(keyword)) &&
                    (item.get("shopName") == null || !item.get("shopName").toString().toLowerCase().contains(lowerKeyword))) {
                    continue;
                }
            }
            
            filteredContent.add(item);
        }
        
        // 手动分页
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredContent.size());
        
        List<Map<String, Object>> pageContent;
        if (start >= filteredContent.size()) {
            pageContent = new ArrayList<>();
        } else {
            pageContent = filteredContent.subList(start, end);
        }
        
        return new PageImpl<>(pageContent, pageable, filteredContent.size());
    }

    public Shop reviewMerchant(Long merchantId, Integer auditStatus, String auditRemark) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("商家未创建店铺");
        }
        Shop shop = shopRepository.findById(merchant.getShopId()).orElseThrow(() -> new RuntimeException("店铺不存在"));
        
        // 更新店铺的审核状态
        shop.setAuditStatus(auditStatus);
        shop.setAuditRemark(auditRemark);
        
        // 审核通过时，同时更新店铺状态为正常（1）
        if (auditStatus == 1) {
            shop.setStatus(1);
            merchant.setStatus(1);
            merchantRepository.save(merchant);
        }
        // 审核拒绝时，保持店铺状态为禁用（0）
        else if (auditStatus == 2) {
            shop.setStatus(0);
        }
        
        return shopRepository.save(shop);
    }

    public Merchant freezeMerchant(Long merchantId, Integer status) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        merchant.setStatus(status);
        merchantRepository.save(merchant);
        
        // 同时更新店铺状态
        if (merchant.getShopId() != null) {
            Shop shop = shopRepository.findById(merchant.getShopId()).orElse(null);
            if (shop != null) {
                shop.setStatus(status);
                shopRepository.save(shop);
            }
        }
        
        return merchant;
    }

    public Map<String, Object> getMerchantDetail(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        Map<String, Object> result = new HashMap<>();
        result.put("id", merchant.getId());
        result.put("username", merchant.getUsername());
        result.put("phone", merchant.getPhone());
        result.put("email", merchant.getEmail());
        result.put("status", merchant.getStatus());
        result.put("createTime", merchant.getCreateTime());
        result.put("shopId", merchant.getShopId());
        
        if (merchant.getShopId() != null) {
            Shop shop = shopRepository.findById(merchant.getShopId()).orElse(null);
            if (shop != null) {
                result.put("shopName", shop.getName());
                result.put("shopLogo", shop.getLogo());
                result.put("shopDescription", shop.getDescription());
                result.put("shopAddress", shop.getAddress());
                result.put("shopPhone", shop.getPhone());
                result.put("shopRating", shop.getRating());
                result.put("shopSalesCount", shop.getSalesCount());
                result.put("auditStatus", shop.getAuditStatus());
            }
        }
        
        return result;
    }
}
