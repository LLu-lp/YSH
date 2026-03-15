package com.haowu.shop.service;

import com.haowu.shop.entity.Banner;
import com.haowu.shop.mapper.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    // 获取首页轮播图列表
    public List<Banner> getBanners() {
        // 获取启用的轮播图，按排序值升序
        return bannerRepository.findByStatusOrderBySortAsc(1);
    }
}
