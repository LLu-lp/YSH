package com.haowu.shop.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "type", nullable = false)
    private Integer type; // 1=平台券，2=店铺券

    @Column(name = "shop_id")
    private Long shopId; // 店铺ID，平台券为null

    @Column(name = "value", nullable = false)
    private Double value; // 优惠券金额

    @Column(name = "min_amount")
    private Double minAmount; // 最低使用金额

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; // 开始时间

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime; // 结束时间

    @Column(name = "total_count", nullable = false)
    private Integer totalCount; // 总数量

    @Column(name = "remaining_count", nullable = false)
    private Integer remainingCount; // 剩余数量

    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 1")
    private Integer status; // 状态：0=禁用，1=启用

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
