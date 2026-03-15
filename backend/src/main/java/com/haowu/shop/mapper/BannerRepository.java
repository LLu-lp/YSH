package com.haowu.shop.mapper;

import com.haowu.shop.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    // 获取启用的轮播图，按排序值升序
    List<Banner> findByStatusOrderBySortAsc(Integer status);
}
