package com.haowu.shop.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "t_category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "icon", length = 255)
    private String icon;

    @Column(name = "parent_id", columnDefinition = "bigint default 0")
    private Long parentId;

    @Column(name = "sort", columnDefinition = "int default 0")
    private Integer sort;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 1")
    private Integer status;
}