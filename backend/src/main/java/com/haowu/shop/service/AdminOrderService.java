package com.haowu.shop.service;

import com.haowu.shop.entity.Order;
import com.haowu.shop.entity.OrderItem;
import com.haowu.shop.entity.Shop;
import com.haowu.shop.entity.User;
import com.haowu.shop.mapper.OrderItemRepository;
import com.haowu.shop.mapper.OrderRepository;
import com.haowu.shop.mapper.ShopRepository;
import com.haowu.shop.mapper.UserRepository;
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
public class AdminOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    public Page<Map<String, Object>> getOrders(String keyword, Integer status, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<Map<String, Object>> content = new ArrayList<>();
        
        for (Order order : orderPage.getContent()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", order.getId());
            item.put("orderNo", order.getOrderNo());
            item.put("totalAmount", order.getTotalAmount());
            item.put("payAmount", order.getPayAmount());
            item.put("status", order.getStatus());
            item.put("receiverName", order.getReceiverName());
            item.put("receiverPhone", order.getReceiverPhone());
            item.put("receiverAddress", order.getReceiverAddress());
            item.put("payTime", order.getPayTime());
            item.put("sendTime", order.getSendTime());
            item.put("confirmTime", order.getConfirmTime());
            item.put("cancelTime", order.getCancelTime());
            item.put("createTime", order.getCreateTime());
            item.put("expressCompany", order.getExpressCompany());
            item.put("expressNo", order.getExpressNo());
            
            String username = "-";
            if (order.getUserId() != null) {
                User user = userRepository.findById(order.getUserId()).orElse(null);
                if (user != null) {
                    username = user.getUsername();
                }
            }
            item.put("username", username);
            
            String shopName = "-";
            if (order.getShopId() != null) {
                Shop shop = shopRepository.findById(order.getShopId()).orElse(null);
                if (shop != null) {
                    shopName = shop.getName();
                }
            }
            item.put("shopName", shopName);
            
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            List<Map<String, Object>> products = new ArrayList<>();
            for (OrderItem orderItem : orderItems) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", orderItem.getProductId());
                product.put("name", orderItem.getProductName());
                product.put("image", orderItem.getProductImage());
                product.put("price", orderItem.getPrice());
                product.put("quantity", orderItem.getQuantity());
                product.put("spec", orderItem.getSpec());
                products.add(product);
            }
            item.put("products", products);
            
            content.add(item);
        }
        
        return new PageImpl<>(content, pageable, orderPage.getTotalElements());
    }

    public Map<String, Object> getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));
        
        Map<String, Object> item = new HashMap<>();
        item.put("id", order.getId());
        item.put("orderNo", order.getOrderNo());
        item.put("totalAmount", order.getTotalAmount());
        item.put("payAmount", order.getPayAmount());
        item.put("status", order.getStatus());
        item.put("receiverName", order.getReceiverName());
        item.put("receiverPhone", order.getReceiverPhone());
        item.put("receiverAddress", order.getReceiverAddress());
        item.put("payTime", order.getPayTime());
        item.put("sendTime", order.getSendTime());
        item.put("confirmTime", order.getConfirmTime());
        item.put("cancelTime", order.getCancelTime());
        item.put("createTime", order.getCreateTime());
        item.put("expressCompany", order.getExpressCompany());
        item.put("expressNo", order.getExpressNo());
        item.put("remark", order.getRemark());
        
        String username = "-";
        if (order.getUserId() != null) {
            User user = userRepository.findById(order.getUserId()).orElse(null);
            if (user != null) {
                username = user.getUsername();
            }
        }
        item.put("username", username);
        
        String shopName = "-";
        if (order.getShopId() != null) {
            Shop shop = shopRepository.findById(order.getShopId()).orElse(null);
            if (shop != null) {
                shopName = shop.getName();
            }
        }
        item.put("shopName", shopName);
        
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        List<Map<String, Object>> products = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            Map<String, Object> product = new HashMap<>();
            product.put("id", orderItem.getProductId());
            product.put("name", orderItem.getProductName());
            product.put("image", orderItem.getProductImage());
            product.put("price", orderItem.getPrice());
            product.put("quantity", orderItem.getQuantity());
            product.put("spec", orderItem.getSpec());
            products.add(product);
        }
        item.put("products", products);
        
        return item;
    }

    public Order updateOrderStatus(Long orderId, Integer status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order handleRefund(Long orderId, String action, String remark) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (order.getStatus() != 5) {
            throw new RuntimeException("订单不是退款状态");
        }
        
        if ("approve".equals(action)) {
            order.setStatus(4);
        } else if ("reject".equals(action)) {
            order.setStatus(3);
        } else {
            throw new RuntimeException("无效的操作类型");
        }
        
        return orderRepository.save(order);
    }
}