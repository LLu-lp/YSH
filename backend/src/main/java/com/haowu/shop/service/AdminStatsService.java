package com.haowu.shop.service;

import com.haowu.shop.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminStatsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShopRepository shopRepository;

    public Map<String, Object> getOverviewStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userRepository.count();
        long totalMerchants = merchantRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        long totalShops = shopRepository.count();
        
        double totalRevenue = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() >= 2)
                .mapToDouble(order -> order.getPayAmount() != null ? order.getPayAmount() : 0.0)
                .sum();
        
        long pendingProducts = productRepository.findAll().stream()
                .filter(product -> product.getAuditStatus() != null && product.getAuditStatus() == 0)
                .count();
        
        long pendingMerchants = merchantRepository.findAll().stream()
                .filter(merchant -> merchant.getStatus() != null && merchant.getStatus() == 0)
                .count();
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalMerchants", totalMerchants);
        stats.put("totalProducts", totalProducts);
        stats.put("totalOrders", totalOrders);
        stats.put("totalShops", totalShops);
        stats.put("totalRevenue", totalRevenue);
        stats.put("pendingProducts", pendingProducts);
        stats.put("pendingMerchants", pendingMerchants);
        
        return stats;
    }

    public List<Map<String, Object>> getSalesTrend(int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            LocalDateTime startOfDay = date.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = date.withHour(23).withMinute(59).withSecond(59);
            
            double dailySales = orderRepository.findAll().stream()
                    .filter(order -> order.getCreateTime() != null 
                            && order.getCreateTime().isAfter(startOfDay) 
                            && order.getCreateTime().isBefore(endOfDay)
                            && order.getStatus() >= 2)
                    .mapToDouble(order -> order.getPayAmount() != null ? order.getPayAmount() : 0.0)
                    .sum();
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("amount", dailySales);
            trend.add(item);
        }
        
        return trend;
    }

    public List<Map<String, Object>> getTodoList() {
        List<Map<String, Object>> todoList = new ArrayList<>();
        
        long pendingProductAudits = productRepository.findAll().stream()
                .filter(product -> product.getAuditStatus() != null && product.getAuditStatus() == 0)
                .count();
        
        long pendingMerchantAudits = merchantRepository.findAll().stream()
                .filter(merchant -> merchant.getStatus() != null && merchant.getStatus() == 0)
                .count();
        
        long pendingRefunds = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() != null && order.getStatus() == 5)
                .count();
        
        if (pendingProductAudits > 0) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "product_audit");
            item.put("title", "待审核商品");
            item.put("count", pendingProductAudits);
            item.put("link", "/admin/product-audit");
            todoList.add(item);
        }
        
        if (pendingMerchantAudits > 0) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "merchant_audit");
            item.put("title", "待审核商家");
            item.put("count", pendingMerchantAudits);
            item.put("link", "/admin/merchant-manage");
            todoList.add(item);
        }
        
        if (pendingRefunds > 0) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "refund");
            item.put("title", "待处理退款");
            item.put("count", pendingRefunds);
            item.put("link", "/admin/order-manage");
            todoList.add(item);
        }
        
        return todoList;
    }

    public long getPendingCount() {
        long pendingProducts = productRepository.findAll().stream()
                .filter(product -> product.getAuditStatus() != null && product.getAuditStatus() == 0)
                .count();
        
        long pendingMerchants = merchantRepository.findAll().stream()
                .filter(merchant -> merchant.getStatus() != null && merchant.getStatus() == 0)
                .count();
        
        long pendingRefunds = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() != null && order.getStatus() == 5)
                .count();
        
        return pendingProducts + pendingMerchants + pendingRefunds;
    }
}
