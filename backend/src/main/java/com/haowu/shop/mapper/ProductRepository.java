package com.haowu.shop.mapper;

import com.haowu.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByShopId(Long shopId, Pageable pageable);
    Page<Product> findByShopIdAndStatus(Long shopId, Integer status, Pageable pageable);
    List<Product> findByShopId(Long shopId);
    long countByShopId(Long shopId);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByNameContaining(String keyword, Pageable pageable);
    Page<Product> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
    Page<Product> findByAuditStatus(Integer auditStatus, Pageable pageable);
    List<Product> findByStatus(Integer status);
    List<Product> findByAuditStatus(Integer auditStatus);
}