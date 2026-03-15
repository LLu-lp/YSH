package com.haowu.shop.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_refund")
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "order_no", nullable = false, length = 30)
    private String orderNo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false, columnDefinition = "decimal(10,2)")
    private Double amount;

    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 0")
    private Integer status;

    @Column(name = "handle_remark", length = 255)
    private String handleRemark;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "handle_time")
    private LocalDateTime handleTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}