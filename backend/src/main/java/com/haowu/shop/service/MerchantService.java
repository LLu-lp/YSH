package com.haowu.shop.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.haowu.shop.entity.Category;
import com.haowu.shop.entity.Merchant;
import com.haowu.shop.entity.Order;
import com.haowu.shop.entity.Product;
import com.haowu.shop.entity.Shop;
import com.haowu.shop.mapper.CategoryRepository;
import com.haowu.shop.mapper.MerchantRepository;
import com.haowu.shop.mapper.OrderRepository;
import com.haowu.shop.mapper.ProductRepository;
import com.haowu.shop.mapper.ShopRepository;

@Service
public class MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Merchant getMerchantInfo(Long merchantId) {
        return merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
    }

    public Shop getShopInfo(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        return shopRepository.findById(merchant.getShopId()).orElseThrow(() -> new RuntimeException("店铺不存在"));
    }

    public Shop updateShopInfo(Long merchantId, String name, String logo, String description, String address, String phone) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        Shop shop = shopRepository.findById(merchant.getShopId()).orElseThrow(() -> new RuntimeException("店铺不存在"));

        shop.setName(name);
        shop.setLogo(logo);
        shop.setDescription(description);
        shop.setAddress(address);
        shop.setPhone(phone);

        return shopRepository.save(shop);
    }

    public Map<String, Object> getShopStats(Long merchantId, String period) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        Long shopId = merchant.getShopId();

        LocalDateTime startTime = calculateStartTime(period);
        List<Order> orders = orderRepository.findByShopIdAndCreateTimeAfter(shopId, startTime);

        double totalSales = orders.stream()
                .filter(order -> order.getStatus() >= 2)
                .mapToDouble(order -> order.getPayAmount() != null ? order.getPayAmount() : 0.0)
                .sum();

        int totalOrders = orders.size();

        int newOrders = (int) orders.stream()
                .filter(order -> order.getStatus() == 0 || order.getStatus() == 1)
                .count();

        long totalProducts = productRepository.countByShopId(shopId);

        List<Map<String, Object>> salesTrend = calculateSalesTrend(orders, period, startTime);

        List<Map<String, Object>> topProducts = getTopProducts(shopId, 5);

        Map<String, Object> result = new HashMap<>();
        result.put("totalSales", totalSales);
        result.put("totalOrders", totalOrders);
        result.put("totalProducts", totalProducts);
        result.put("newOrders", newOrders);
        result.put("salesTrend", salesTrend);
        result.put("topProducts", topProducts);

        return result;
    }

    public Map<String, Object> getShopOverview(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        Long shopId = merchant.getShopId();

        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        List<Order> todayOrders = orderRepository.findByShopIdAndCreateTimeAfter(shopId, todayStart);

        double todaySales = todayOrders.stream()
                .filter(order -> order.getStatus() >= 2)
                .mapToDouble(order -> order.getPayAmount() != null ? order.getPayAmount() : 0.0)
                .sum();

        int todayOrdersCount = todayOrders.size();

        int pendingOrders = (int) orderRepository.findByShopId(shopId, Pageable.unpaged()).stream()
                .filter(order -> order.getStatus() == 1)
                .count();

        int pendingRefunds = (int) orderRepository.findByShopId(shopId, Pageable.unpaged()).stream()
                .filter(order -> order.getStatus() == 5)
                .count();

        long totalProducts = productRepository.countByShopId(shopId);

        Map<String, Object> result = new HashMap<>();
        result.put("todaySales", todaySales);
        result.put("todayOrders", todayOrdersCount);
        result.put("pendingOrders", pendingOrders);
        result.put("pendingRefunds", pendingRefunds);
        result.put("totalProducts", totalProducts);

        return result;
    }

    public List<Map<String, Object>> getSalesTrend(Long merchantId, int days) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        Long shopId = merchant.getShopId();

        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            LocalDateTime startOfDay = date.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = date.withHour(23).withMinute(59).withSecond(59);

            List<Order> dayOrders = orderRepository.findByShopIdAndCreateTimeAfter(shopId, startOfDay);
            double dailySales = dayOrders.stream()
                    .filter(order -> order.getCreateTime() != null 
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

    public List<Map<String, Object>> getCategoryRatio(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        Long shopId = merchant.getShopId();

        List<Product> products = productRepository.findByShopId(shopId);
        Map<Long, Integer> categoryCount = new HashMap<>();

        for (Product product : products) {
            Long categoryId = product.getCategoryId();
            categoryCount.put(categoryId, categoryCount.getOrDefault(categoryId, 0) + 1);
        }

        List<Map<String, Object>> ratioList = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : categoryCount.entrySet()) {
            // 从数据库获取真实的分类名称
            Category category = categoryRepository.findById(entry.getKey()).orElse(null);
            String categoryName = category != null ? category.getName() : "未知分类";
            
            Map<String, Object> item = new HashMap<>();
            item.put("name", categoryName);
            item.put("value", entry.getValue());
            item.put("count", entry.getValue());
            ratioList.add(item);
        }

        return ratioList;
    }

    private LocalDateTime calculateStartTime(String period) {
        LocalDateTime now = LocalDateTime.now();
        switch (period) {
            case "today":
                return now.withHour(0).withMinute(0).withSecond(0);
            case "week":
                return now.minusDays(7);
            case "month":
                return now.minusMonths(1);
            case "year":
                return now.minusYears(1);
            default:
                return now.withHour(0).withMinute(0).withSecond(0);
        }
    }

    private List<Map<String, Object>> calculateSalesTrend(List<Order> orders, String period, LocalDateTime startTime) {
        Map<String, Double> dailySales = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Order order : orders) {
            if (order.getStatus() >= 2 && order.getCreateTime() != null) {
                String dateKey = order.getCreateTime().format(formatter);
                double amount = order.getPayAmount() != null ? order.getPayAmount() : 0.0;
                dailySales.put(dateKey, dailySales.getOrDefault(dateKey, 0.0) + amount);
            }
        }

        List<Map<String, Object>> trend = new ArrayList<>();
        for (Map.Entry<String, Double> entry : dailySales.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("amount", entry.getValue());
            trend.add(item);
        }

        return trend;
    }

    private List<Map<String, Object>> getTopProducts(Long shopId, int limit) {
        List<Product> products = productRepository.findByShopId(shopId);
        List<Map<String, Object>> topProducts = new ArrayList<>();

        products.sort((a, b) -> {
            int salesA = a.getSalesCount() != null ? a.getSalesCount() : 0;
            int salesB = b.getSalesCount() != null ? b.getSalesCount() : 0;
            return salesB - salesA;
        });

        int count = 0;
        for (Product product : products) {
            if (count >= limit) break;
            Map<String, Object> item = new HashMap<>();
            item.put("productId", product.getId());
            item.put("name", product.getName());
            item.put("salesCount", product.getSalesCount() != null ? product.getSalesCount() : 0);
            item.put("amount", (product.getPrice() != null ? product.getPrice() : 0.0) * (product.getSalesCount() != null ? product.getSalesCount() : 0));
            topProducts.add(item);
            count++;
        }

        return topProducts;
    }

    public Page<Map<String, Object>> getMerchantCustomers(Long merchantId, int page, int pageSize) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        Long shopId = merchant.getShopId();

        // 获取该店铺的所有订单
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Order> orderPage = orderRepository.findByShopId(shopId, pageable);

        // 提取用户信息并去重
        Map<Long, Map<String, Object>> customerMap = new HashMap<>();
        for (Order order : orderPage.getContent()) {
            Long userId = order.getUserId();
            if (!customerMap.containsKey(userId)) {
                Map<String, Object> customer = new HashMap<>();
                customer.put("userId", userId);
                customer.put("username", order.getReceiverName());
                customer.put("totalOrders", 0);
                customer.put("totalAmount", 0.0);
                customer.put("lastOrderTime", order.getCreateTime());
                customerMap.put(userId, customer);
            }
        }

        // 统计每个用户的订单数和消费金额
        for (Order order : orderPage.getContent()) {
            Long userId = order.getUserId();
            Map<String, Object> customer = customerMap.get(userId);
            if (customer != null) {
                int totalOrders = (int) customer.get("totalOrders") + 1;
                double totalAmount = (double) customer.get("totalAmount") + (order.getPayAmount() != null ? order.getPayAmount() : 0.0);
                customer.put("totalOrders", totalOrders);
                customer.put("totalAmount", totalAmount);
                // 更新最后订单时间
                LocalDateTime lastTime = (LocalDateTime) customer.get("lastOrderTime");
                if (order.getCreateTime() != null && (lastTime == null || order.getCreateTime().isAfter(lastTime))) {
                    customer.put("lastOrderTime", order.getCreateTime());
                }
            }
        }

        List<Map<String, Object>> customers = new ArrayList<>(customerMap.values());
        return new PageImpl<>(customers, pageable, orderPage.getTotalElements());
    }
}
