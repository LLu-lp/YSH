package com.haowu.shop.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_shop")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "logo", length = 255)
    private String logo;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 1")
    private Integer status;

    @Column(name = "audit_status", nullable = false, columnDefinition = "tinyint default 0")
    private Integer auditStatus;

    @Column(name = "audit_remark", length = 255)
    private String auditRemark;

    @Column(name = "rating", columnDefinition = "decimal(3,1) default 5.0")
    private Double rating;

    @Column(name = "sales_count", columnDefinition = "int default 0")
    private Integer salesCount;

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