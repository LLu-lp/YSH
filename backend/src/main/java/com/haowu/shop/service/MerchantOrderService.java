package com.haowu.shop.service;

import com.haowu.shop.entity.Order;
import com.haowu.shop.entity.OrderItem;
import com.haowu.shop.entity.Merchant;
import com.haowu.shop.entity.Refund;
import com.haowu.shop.mapper.OrderRepository;
import com.haowu.shop.mapper.OrderItemRepository;
import com.haowu.shop.mapper.MerchantRepository;
import com.haowu.shop.mapper.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MerchantOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private RefundRepository refundRepository;

    public Page<Map<String, Object>> getMerchantOrders(Long merchantId, Integer status, String keyword, Pageable pageable) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        
        Page<Order> orderPage;
        
        if (status != null && keyword != null && !keyword.isEmpty()) {
            // 按状态和关键词搜索
            orderPage = orderRepository.findByShopIdAndStatusAndKeyword(merchant.getShopId(), status, keyword, pageable);
        } else if (status != null) {
            // 只按状态搜索
            orderPage = orderRepository.findByShopIdAndStatus(merchant.getShopId(), status, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            // 只按关键词搜索
            orderPage = orderRepository.findByShopIdAndKeyword(merchant.getShopId(), keyword, pageable);
        } else {
            // 查询所有
            orderPage = orderRepository.findByShopId(merchant.getShopId(), pageable);
        }
        
        List<Map<String, Object>> content = new ArrayList<>();
        for (Order order : orderPage.getContent()) {
            Map<String, Object> item = orderToMap(order);
            content.add(item);
        }
        
        return new PageImpl<>(content, pageable, orderPage.getTotalElements());
    }

    public Map<String, Object> getOrderDetail(Long merchantId, Long orderId) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!order.getShopId().equals(merchant.getShopId())) {
            throw new RuntimeException("无权查看该订单");
        }
        return orderToMap(order);
    }

    @Transactional
    public Order shipOrder(Long merchantId, Long orderId, String expressCompany, String expressNo) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!order.getShopId().equals(merchant.getShopId())) {
            throw new RuntimeException("无权操作该订单");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态错误");
        }

        order.setStatus(2);
        order.setExpressCompany(expressCompany);
        order.setExpressNo(expressNo);
        order.setSendTime(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Transactional
    public Order handleRefund(Long merchantId, Long orderId, String action, String remark) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new RuntimeException("商家不存在"));
        if (merchant.getShopId() == null) {
            throw new RuntimeException("店铺未创建");
        }
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!order.getShopId().equals(merchant.getShopId())) {
            throw new RuntimeException("无权操作该订单");
        }
        if (order.getStatus() != 5) {
            throw new RuntimeException("订单状态错误");
        }

        if (action.equals("approve")) {
            order.setStatus(4);
        } else if (action.equals("reject")) {
            order.setStatus(2);
        }
        orderRepository.save(order);

        // 更新 refund 记录的处理状态和备注
        List<Refund> refunds = refundRepository.findByOrderId(orderId);
        if (!refunds.isEmpty()) {
            Refund refund = refunds.get(refunds.size() - 1);
            refund.setHandleRemark(remark);
            refund.setHandleTime(java.time.LocalDateTime.now());
            refund.setStatus(action.equals("approve") ? 1 : 2);
            refundRepository.save(refund);
        }

        return order;
    }

    private Map<String, Object> orderToMap(Order order) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", order.getId());
        item.put("orderNo", order.getOrderNo());
        item.put("userId", order.getUserId());
        item.put("shopId", order.getShopId());
        item.put("totalAmount", order.getTotalAmount());
        item.put("payAmount", order.getPayAmount());
        item.put("payMethod", order.getPayMethod());
        item.put("status", order.getStatus());
        item.put("receiverName", order.getReceiverName());
        item.put("receiverPhone", order.getReceiverPhone());
        item.put("receiverAddress", order.getReceiverAddress());
        item.put("remark", order.getRemark());
        item.put("expressCompany", order.getExpressCompany());
        item.put("expressNo", order.getExpressNo());
        item.put("payTime", order.getPayTime());
        item.put("sendTime", order.getSendTime());
        item.put("confirmTime", order.getConfirmTime());
        item.put("cancelTime", order.getCancelTime());
        item.put("createTime", order.getCreateTime());
        item.put("updateTime", order.getUpdateTime());
        
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        List<Map<String, Object>> items = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            Map<String, Object> i = new HashMap<>();
            i.put("id", orderItem.getId());
            i.put("productId", orderItem.getProductId());
            i.put("productName", orderItem.getProductName());
            i.put("productImage", orderItem.getProductImage());
            i.put("price", orderItem.getPrice());
            i.put("quantity", orderItem.getQuantity());
            i.put("spec", orderItem.getSpec());
            items.add(i);
        }
        item.put("orderItems", items);
        
        return item;
    }
}