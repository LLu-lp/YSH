package com.haowu.shop.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_user_coupon")
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 1")
    private Integer status; // 状态：1=未使用，2=已使用，3=已过期

    @Column(name = "receive_time", nullable = false, updatable = false)
    private LocalDateTime receiveTime;

    @Column(name = "use_time")
    private LocalDateTime useTime;

    @PrePersist
    protected void onCreate() {
        receiveTime = LocalDateTime.now();
    }
}
