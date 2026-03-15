package com.haowu.shop.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "price", nullable = false, columnDefinition = "decimal(10,2)")
    private Double price;

    @Column(name = "original_price", columnDefinition = "decimal(10,2)")
    private Double originalPrice;

    @Column(name = "stock", nullable = false, columnDefinition = "int default 0")
    private Integer stock;

    @Column(name = "main_image", nullable = false, length = 255)
    private String mainImage;

    @Column(name = "images", columnDefinition = "text")
    private String images;

    @Column(name = "specs", columnDefinition = "text")
    private String specs;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 0")
    private Integer status;

    @Column(name = "audit_status", nullable = false, columnDefinition = "tinyint default 0")
    private Integer auditStatus;

    @Column(name = "audit_remark", length = 255)
    private String auditRemark;

    @Column(name = "sales_count", columnDefinition = "int default 0")
    private Integer salesCount;

    @Column(name = "rating", columnDefinition = "decimal(3,1) default 5.0")
    private Double rating;

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