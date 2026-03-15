package com.haowu.shop.service;

import com.haowu.shop.entity.Favorite;
import com.haowu.shop.entity.Product;
import com.haowu.shop.entity.Shop;
import com.haowu.shop.mapper.FavoriteRepository;
import com.haowu.shop.mapper.ProductRepository;
import com.haowu.shop.mapper.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopRepository shopRepository;

    // 获取用户收藏列表
    public List<Map<String, Object>> getFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Favorite favorite : favorites) {
            Product product = productRepository.findById(favorite.getProductId()).orElse(null);
            if (product != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", product.getId());
                item.put("name", product.getName());
                item.put("price", product.getPrice());
                item.put("mainImage", product.getMainImage());
                item.put("salesCount", product.getSalesCount());
                item.put("rating", product.getRating());
                
                String shopName = "好物店铺";
                if (product.getShopId() != null) {
                    Shop shop = shopRepository.findById(product.getShopId()).orElse(null);
                    if (shop != null) {
                        shopName = shop.getName();
                    }
                }
                item.put("shopName", shopName);
                
                result.add(item);
            }
        }
        
        return result;
    }

    // 收藏商品
    public Favorite addFavorite(Long userId, Long productId) {
        // 检查商品是否存在
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("商品不存在");
        }
        
        // 检查是否已经收藏
        Favorite existingFavorite = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (existingFavorite != null) {
            throw new RuntimeException("商品已经在收藏夹中");
        }
        
        // 创建新收藏
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        
        return favoriteRepository.save(favorite);
    }

    // 取消收藏
    public void removeFavorite(Long userId, Long productId) {
        Favorite favorite = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (favorite == null) {
            throw new RuntimeException("商品不在收藏夹中");
        }
        
        favoriteRepository.delete(favorite);
    }
}
