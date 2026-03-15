package com.haowu.shop.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "t_order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "order_no", nullable = false, length = 30)
    private String orderNo;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_image", length = 255)
    private String productImage;

    @Column(name = "price", nullable = false, columnDefinition = "decimal(10,2)")
    private Double price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "spec", length = 200)
    private String spec;

    @Column(name = "reviewed", columnDefinition = "tinyint default 0")
    private Integer reviewed;
}