package com.haowu.shop.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, unique = true, length = 30)
    private String orderNo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "total_amount", nullable = false, columnDefinition = "decimal(10,2)")
    private Double totalAmount;

    @Column(name = "pay_amount", nullable = false, columnDefinition = "decimal(10,2)")
    private Double payAmount;

    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "pay_method", length = 20)
    private String payMethod;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 0")
    private Integer status;

    @Column(name = "receiver_name", nullable = false, length = 50)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    @Column(name = "receiver_address", nullable = false, length = 300)
    private String receiverAddress;

    @Column(name = "remark", length = 255)
    private String remark;

    @Column(name = "express_company", length = 50)
    private String expressCompany;

    @Column(name = "express_no", length = 50)
    private String expressNo;

    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @Column(name = "send_time")
    private LocalDateTime sendTime;

    @Column(name = "confirm_time")
    private LocalDateTime confirmTime;

    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}