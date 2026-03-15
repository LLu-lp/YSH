package com.haowu.shop.service;

import com.haowu.shop.entity.Cart;
import com.haowu.shop.entity.Product;
import com.haowu.shop.mapper.CartRepository;
import com.haowu.shop.mapper.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Map<String, Object>> getCartList(Long userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Cart cart : carts) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", cart.getId());
            item.put("userId", cart.getUserId());
            item.put("productId", cart.getProductId());
            item.put("shopId", cart.getShopId());
            item.put("quantity", cart.getQuantity());
            item.put("spec", cart.getSpec());
            item.put("checked", cart.getChecked());
            item.put("createTime", cart.getCreateTime());
            item.put("updateTime", cart.getUpdateTime());
            
            Product product = productRepository.findById(cart.getProductId()).orElse(null);
            if (product != null) {
                item.put("productName", product.getName());
                item.put("productImage", product.getMainImage());
                item.put("price", product.getPrice());
                item.put("stock", product.getStock());
                item.put("status", product.getStatus());
            }
            
            result.add(item);
        }
        
        return result;
    }

    public Map<String, Object> addToCart(Long userId, Long productId, Integer quantity, String spec) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("商品不存在"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        Cart existingCart = cartRepository.findByUserIdAndProductIdAndSpec(userId, productId, spec);
        Cart cart;
        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            cart = cartRepository.save(existingCart);
        } else {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setShopId(product.getShopId());
            cart.setQuantity(quantity);
            cart.setSpec(spec);
            cart.setChecked(1);
            cart = cartRepository.save(cart);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", cart.getId());
        result.put("userId", cart.getUserId());
        result.put("productId", cart.getProductId());
        result.put("shopId", cart.getShopId());
        result.put("quantity", cart.getQuantity());
        result.put("spec", cart.getSpec());
        result.put("checked", cart.getChecked());
        result.put("productName", product.getName());
        result.put("productImage", product.getMainImage());
        result.put("price", product.getPrice());
        result.put("stock", product.getStock());
        
        return result;
    }

    public Map<String, Object> updateCartItem(Long cartId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("购物车项不存在"));

        Product product = productRepository.findById(cart.getProductId()).orElseThrow(() -> new RuntimeException("商品不存在"));
        if (product.getStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        cart.setQuantity(quantity);
        cart = cartRepository.save(cart);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", cart.getId());
        result.put("quantity", cart.getQuantity());
        result.put("productName", product.getName());
        result.put("productImage", product.getMainImage());
        result.put("price", product.getPrice());
        
        return result;
    }

    public void deleteCartItem(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    public void batchDeleteCart(List<Long> cartIds) {
        cartRepository.deleteAllById(cartIds);
    }
}
