package com.haowu.shop.service;

import com.haowu.shop.entity.Category;
import com.haowu.shop.entity.Product;
import com.haowu.shop.entity.Merchant;
import com.haowu.shop.mapper.CategoryRepository;
import com.haowu.shop.mapper.ProductRepository;
import com.haowu.shop.mapper.MerchantRepository;
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
public class MerchantProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Map<String, Object>> getMerchantProducts(Long merchantId, String keyword, Long categoryId, Integer status, Pageable pageable) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        
        // 获取所有商品，然后在内存中过滤
        Page<Product> productPage = productRepository.findByShopId(merchant.getShopId(), pageable);
        List<Product> allProducts = productPage.getContent();
        
        // 应用过滤条件
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : allProducts) {
            boolean matches = true;
            
            // 按关键词过滤（商品名称）
            if (keyword != null && !keyword.isEmpty()) {
                if (!product.getName().contains(keyword)) {
                    matches = false;
                }
            }
            
            // 按分类过滤
            if (categoryId != null && categoryId > 0) {
                if (!product.getCategoryId().equals(categoryId)) {
                    matches = false;
                }
            }
            
            // 按状态过滤
            if (status != null) {
                if (!product.getStatus().equals(status)) {
                    matches = false;
                }
            }
            
            if (matches) {
                filteredProducts.add(product);
            }
        }
        
        List<Map<String, Object>> content = new ArrayList<>();
        for (Product product : filteredProducts) {
            Map<String, Object> item = productToMap(product);
            content.add(item);
        }
        
        return new PageImpl<>(content, pageable, filteredProducts.size());
    }

    public Product createProduct(Long merchantId, Product product) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        product.setShopId(merchant.getShopId());
        product.setStatus(0);
        product.setAuditStatus(0);
        return productRepository.save(product);
    }

    public Product updateProduct(Long merchantId, Long productId, Product product) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("商品不存在"));
        if (!existingProduct.getShopId().equals(merchant.getShopId())) {
            throw new RuntimeException("无权操作该商品");
        }

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setOriginalPrice(product.getOriginalPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setMainImage(product.getMainImage());
        existingProduct.setImages(product.getImages());
        existingProduct.setSpecs(product.getSpecs());
        existingProduct.setCategoryId(product.getCategoryId());

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long merchantId, Long productId) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("商品不存在"));
        if (!product.getShopId().equals(merchant.getShopId())) {
            throw new RuntimeException("无权操作该商品");
        }
        productRepository.delete(product);
    }

    public Product updateProductStatus(Long merchantId, Long productId, Integer status) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("商品不存在"));
        if (!product.getShopId().equals(merchant.getShopId())) {
            throw new RuntimeException("无权操作该商品");
        }
        product.setStatus(status);
        return productRepository.save(product);
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
        
        return item;
    }
}