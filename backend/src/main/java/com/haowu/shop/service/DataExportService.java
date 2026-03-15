package com.haowu.shop.service;

import com.haowu.shop.entity.*;
import com.haowu.shop.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DataExportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private ShopRepository shopRepository;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 导出订单数据
    public List<Map<String, Object>> getOrdersForExport(Integer status, String startDate, String endDate) {
        List<Order> orders = orderRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        LocalDateTime start = null;
        LocalDateTime end = null;

        if (startDate != null && !startDate.isEmpty()) {
            start = LocalDate.parse(startDate).atStartOfDay();
        }
        if (endDate != null && !endDate.isEmpty()) {
            end = LocalDate.parse(endDate).plusDays(1).atStartOfDay();
        }

        for (Order order : orders) {
            // 按状态过滤
            if (status != null && !order.getStatus().equals(status)) {
                continue;
            }

            // 按时间范围过滤
            if (start != null && order.getCreateTime().isBefore(start)) {
                continue;
            }
            if (end != null && order.getCreateTime().isAfter(end)) {
                continue;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("订单号", order.getOrderNo());
            item.put("用户ID", order.getUserId());
            
            // 获取用户名
            User user = userRepository.findById(order.getUserId()).orElse(null);
            item.put("用户名", user != null ? user.getUsername() : "-");
            
            item.put("金额", "¥" + String.format("%.2f", order.getPayAmount()));
            item.put("状态", getOrderStatusText(order.getStatus()));
            item.put("下单时间", order.getCreateTime().format(dateFormatter));
            item.put("支付时间", order.getPayTime() != null ? order.getPayTime().format(dateFormatter) : "-");
            item.put("收货地址", order.getReceiverAddress() != null ? order.getReceiverAddress() : "-");

            result.add(item);
        }

        return result;
    }

    // 导出用户数据
    public List<Map<String, Object>> getUsersForExport(Integer status, String startDate, String endDate) {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        LocalDateTime start = null;
        LocalDateTime end = null;

        if (startDate != null && !startDate.isEmpty()) {
            start = LocalDate.parse(startDate).atStartOfDay();
        }
        if (endDate != null && !endDate.isEmpty()) {
            end = LocalDate.parse(endDate).plusDays(1).atStartOfDay();
        }

        for (User user : users) {
            // 按状态过滤
            if (status != null && !user.getStatus().equals(status)) {
                continue;
            }

            // 按注册时间过滤
            if (start != null && user.getCreateTime().isBefore(start)) {
                continue;
            }
            if (end != null && user.getCreateTime().isAfter(end)) {
                continue;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("用户ID", user.getId());
            item.put("用户名", user.getUsername());
            item.put("邮箱", user.getEmail() != null ? user.getEmail() : "-");
            item.put("手机号", user.getPhone() != null ? user.getPhone() : "-");
            item.put("注册时间", user.getCreateTime().format(dateFormatter));
            item.put("最后登录", user.getLastLoginTime() != null ? user.getLastLoginTime().format(dateFormatter) : "-");
            item.put("状态", user.getStatus() == 1 ? "正常" : "禁用");

            result.add(item);
        }

        return result;
    }

    // 导出商品数据
    public List<Map<String, Object>> getProductsForExport(Integer status, Integer auditStatus) {
        List<Product> products = productRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Product product : products) {
            // 按状态过滤
            if (status != null && !product.getStatus().equals(status)) {
                continue;
            }

            // 按审核状态过滤
            if (auditStatus != null && !product.getAuditStatus().equals(auditStatus)) {
                continue;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("商品ID", product.getId());
            item.put("商品名", product.getName());
            item.put("分类", product.getCategoryId());
            item.put("价格", "¥" + String.format("%.2f", product.getPrice()));
            item.put("原价", product.getOriginalPrice() != null ? "¥" + String.format("%.2f", product.getOriginalPrice()) : "-");
            item.put("库存", product.getStock());
            item.put("销量", product.getSalesCount() != null ? product.getSalesCount() : 0);
            item.put("评分", product.getRating() != null ? String.format("%.1f", product.getRating()) : "-");
            item.put("商品状态", product.getStatus() == 1 ? "在售" : "下架");
            item.put("审核状态", getAuditStatusText(product.getAuditStatus()));
            item.put("创建时间", product.getCreateTime().format(dateFormatter));

            result.add(item);
        }

        return result;
    }

    // 导出商家数据
    public List<Map<String, Object>> getMerchantsForExport(Integer status, Integer auditStatus) {
        List<Merchant> merchants = merchantRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Merchant merchant : merchants) {
            // 按状态过滤
            if (status != null && !merchant.getStatus().equals(status)) {
                continue;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("商家ID", merchant.getId());
            item.put("商家账号", merchant.getUsername());
            item.put("联系电话", merchant.getPhone() != null ? merchant.getPhone() : "-");
            item.put("联系邮箱", merchant.getEmail() != null ? merchant.getEmail() : "-");

            // 获取店铺信息
            if (merchant.getShopId() != null) {
                Shop shop = shopRepository.findById(merchant.getShopId()).orElse(null);
                if (shop != null) {
                    item.put("店铺名", shop.getName());
                    item.put("店铺地址", shop.getAddress() != null ? shop.getAddress() : "-");
                    item.put("店铺电话", shop.getPhone() != null ? shop.getPhone() : "-");
                    item.put("店铺评分", shop.getRating() != null ? String.format("%.1f", shop.getRating()) : "-");
                    item.put("销售数", shop.getSalesCount() != null ? shop.getSalesCount() : 0);
                    
                    // 按审核状态过滤
                    if (auditStatus != null && !shop.getAuditStatus().equals(auditStatus)) {
                        continue;
                    }
                    
                    item.put("审核状态", getAuditStatusText(shop.getAuditStatus()));
                    item.put("运营状态", shop.getStatus() == 1 ? "正常" : "禁用");
                } else {
                    item.put("店铺名", "-");
                    item.put("店铺地址", "-");
                    item.put("店铺电话", "-");
                    item.put("店铺评分", "-");
                    item.put("销售数", 0);
                    item.put("审核状态", "-");
                    item.put("运营状态", "-");
                }
            } else {
                item.put("店铺名", "-");
                item.put("店铺地址", "-");
                item.put("店铺电话", "-");
                item.put("店铺评分", "-");
                item.put("销售数", 0);
                item.put("审核状态", "-");
                item.put("运营状态", "-");
            }

            item.put("商家状态", merchant.getStatus() == 1 ? "正常" : "禁用");
            item.put("注册时间", merchant.getCreateTime().format(dateFormatter));

            result.add(item);
        }

        return result;
    }

    private String getOrderStatusText(Integer status) {
        if (status == null) return "-";
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已取消";
            case 5: return "退款中";
            default: return "-";
        }
    }

    private String getAuditStatusText(Integer status) {
        if (status == null) return "-";
        switch (status) {
            case 0: return "审核中";
            case 1: return "已通过";
            case 2: return "已拒绝";
            default: return "-";
        }
    }
}

