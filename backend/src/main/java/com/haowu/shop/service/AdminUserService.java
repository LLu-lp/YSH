package com.haowu.shop.service;

import com.haowu.shop.entity.User;
import com.haowu.shop.mapper.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    // 获取用户列表（管理员）
    public Page<User> getUsers(String keyword, Integer status, Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // 冻结/解封用户
    public User freezeUser(Long userId, Integer status) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(status);
        return userRepository.save(user);
    }

    // 获取用户详情
    public User getUserDetail(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}