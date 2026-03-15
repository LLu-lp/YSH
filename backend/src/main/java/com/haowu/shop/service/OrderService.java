package com.haowu.shop.service;

import com.haowu.shop.entity.Order;
import com.haowu.shop.entity.OrderItem;
import com.haowu.shop.entity.Product;
import com.haowu.shop.entity.Address;
import com.haowu.shop.entity.Coupon;
import com.haowu.shop.entity.UserCoupon;
import com.haowu.shop.mapper.OrderRepository;
import com.haowu.shop.mapper.OrderItemRepository;
import com.haowu.shop.mapper.ProductRepository;
import com.haowu.shop.mapper.AddressRepository;
import com.haowu.shop.entity.Refund;
import com.haowu.shop.mapper.CartRepository;
import com.haowu.shop.mapper.RefundRepository;
import com.haowu.shop.mapper.ShopRepository;
import com.haowu.shop.mapper.CouponRepository;
import com.haowu.shop.mapper.UserCouponRepository;
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
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    /**
     * 创建订单 — 支持两种模式：
     *  1. 购物车结算：传入 cartIds，从购物车取商品后清空对应购物车记录
     *  2. 立即购买：传入 items（每项含 productId / quantity / price / spec），直接创建
     */
    @Transactional
    public Order createOrder(Long userId, List<Long> cartIds,
                             List<Map<String, Object>> items,
                             Long addressId, String remark, Long couponId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        // 构建订单项数据
        List<Map<String, Object>> orderItemData = new ArrayList<>();

        if (cartIds != null && !cartIds.isEmpty()) {
            // 模式1：购物车结算
            for (Long cartId : cartIds) {
                com.haowu.shop.entity.Cart cart = cartRepository.findById(cartId)
                        .orElseThrow(() -> new RuntimeException("购物车项不存在: " + cartId));
                Product product = productRepository.findById(cart.getProductId())
                        .orElseThrow(() -> new RuntimeException("商品不存在: " + cart.getProductId()));
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("product", product);
                itemMap.put("quantity", cart.getQuantity());
                itemMap.put("spec", cart.getSpec());
                itemMap.put("price", product.getPrice());
                orderItemData.add(itemMap);
            }
        } else if (items != null && !items.isEmpty()) {
            // 模式2：立即购买
            for (Map<String, Object> item : items) {
                Long productId = Long.valueOf(item.get("productId").toString());
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));
                int qty = Integer.parseInt(item.get("quantity").toString());
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("product", product);
                itemMap.put("quantity", qty);
                itemMap.put("spec", item.getOrDefault("spec", ""));
                itemMap.put("price", product.getPrice());
                orderItemData.add(itemMap);
            }
        } else {
            throw new RuntimeException("订单商品不能为空");
        }

        // 校验库存、确定 shopId、计算总价
        Long shopId = null;
        double totalAmount = 0.0;
        for (Map<String, Object> itemMap : orderItemData) {
            Product product = (Product) itemMap.get("product");
            int qty = (int) itemMap.get("quantity");
            if (product.getStock() < qty) {
                throw new RuntimeException("商品库存不足: " + product.getName());
            }
            if (shopId == null) {
                shopId = product.getShopId();
            }
            totalAmount += product.getPrice() * qty;
        }

        // 处理优惠券
        double discountAmount = 0.0;
        if (couponId != null) {
            UserCoupon userCoupon = userCouponRepository.findById(couponId)
                    .orElseThrow(() -> new RuntimeException("优惠券不存在"));
            
            if (!userCoupon.getUserId().equals(userId)) {
                throw new RuntimeException("优惠券不属于当前用户");
            }
            
            if (userCoupon.getStatus() != 1) {
                throw new RuntimeException("优惠券已使用或已过期");
            }
            
            Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
                    .orElseThrow(() -> new RuntimeException("优惠券不存在"));
            
            if (LocalDateTime.now().isAfter(coupon.getEndTime())) {
                throw new RuntimeException("优惠券已过期");
            }
            
            discountAmount = coupon.getValue();
        }

        double payAmount = Math.max(0, totalAmount - discountAmount);

        String orderNo = "ORD" + System.currentTimeMillis()
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setShopId(shopId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(payAmount);
        order.setCouponId(couponId);
        order.setStatus(0);
        order.setReceiverName(address.getReceiver());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity()
                + address.getDistrict() + address.getDetail());
        order.setRemark(remark);

        Order savedOrder = orderRepository.save(order);

        // 创建订单项、扣减库存
        for (Map<String, Object> itemMap : orderItemData) {
            Product product = (Product) itemMap.get("product");
            int qty = (int) itemMap.get("quantity");
            String spec = (String) itemMap.get("spec");

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setOrderNo(savedOrder.getOrderNo());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(qty);
            orderItem.setSpec(spec);
            orderItem.setReviewed(0);
            orderItemRepository.save(orderItem);

            product.setStock(product.getStock() - qty);
            productRepository.save(product);
        }

        // 购物车模式：清除已结算的购物车记录
        if (cartIds != null && !cartIds.isEmpty()) {
            cartRepository.deleteAllById(cartIds);
        }

        return savedOrder;
    }

    public Page<Map<String, Object>> getOrderList(Long userId, Integer status, Pageable pageable) {
        Page<Order> orderPage;
        if (status != null) {
            orderPage = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            orderPage = orderRepository.findByUserId(userId, pageable);
        }
        
        List<Map<String, Object>> content = new ArrayList<>();
        for (Order order : orderPage.getContent()) {
            Map<String, Object> item = orderToMap(order);
            content.add(item);
        }
        
        return new PageImpl<>(content, pageable, orderPage.getTotalElements());
    }

    public Map<String, Object> getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));
        return orderToMap(order);
    }

    @Transactional
    public Order payOrder(Long orderId, String payMethod) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态错误");
        }

        order.setStatus(1);
        order.setPayMethod(payMethod);
        order.setPayTime(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // 如果使用了优惠券，标记为已使用
        if (order.getCouponId() != null) {
            UserCoupon userCoupon = userCouponRepository.findById(order.getCouponId())
                    .orElseThrow(() -> new RuntimeException("优惠券不存在"));
            userCoupon.setStatus(2); // 2 表示已使用
            userCouponRepository.save(userCoupon);
        }

        return savedOrder;
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态错误");
        }

        order.setStatus(4);
        order.setCancelTime(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Transactional
    public Order confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态错误");
        }

        order.setStatus(3);
        order.setConfirmTime(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Transactional
    public Order applyRefund(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getStatus() != 2) {
            throw new RuntimeException("只有待收货状态的订单才能申请售后，确认收货后请联系客服处理");
        }

        order.setStatus(5);
        orderRepository.save(order);

        // 将售后原因写入 t_refund 表
        Refund refund = new Refund();
        refund.setOrderId(order.getId());
        refund.setOrderNo(order.getOrderNo());
        refund.setUserId(order.getUserId());
        refund.setAmount(order.getPayAmount() != null ? order.getPayAmount() : order.getTotalAmount());
        refund.setReason(reason != null ? reason : "");
        refund.setStatus(0);
        refundRepository.save(refund);

        return order;
    }

    private Map<String, Object> orderToMap(Order order) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", order.getId());
        item.put("orderNo", order.getOrderNo());
        item.put("userId", order.getUserId());
        item.put("shopId", order.getShopId());
        
        // 添加店铺名称
        if (order.getShopId() != null) {
            shopRepository.findById(order.getShopId()).ifPresent(shop -> {
                item.put("shopName", shop.getName());
            });
        }
        
        item.put("totalAmount", order.getTotalAmount());
        item.put("payAmount", order.getPayAmount());
        item.put("couponId", order.getCouponId());
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