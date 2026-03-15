package com.haowu.shop.service;

import com.haowu.shop.entity.User;
import com.haowu.shop.mapper.UserRepository;
import com.haowu.shop.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtil passwordUtil;

    // 获取用户信息
    public Map<String, Object> getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("phone", user.getPhone());
        info.put("email", user.getEmail());
        info.put("avatar", user.getAvatar());
        info.put("status", user.getStatus());
        info.put("createTime", user.getCreateTime());
        info.put("lastLoginTime", user.getLastLoginTime());
        
        return info;
    }

    // 更新用户信息
    public User updateUserInfo(Long userId, String username, String avatar, String phone, String email) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        // 检查用户名是否被其他用户使用
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否被其他用户使用
        existingUser = userRepository.findByPhone(phone);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new RuntimeException("手机号已被使用");
        }

        // 检查邮箱是否被其他用户使用
        existingUser = userRepository.findByEmail(email);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new RuntimeException("邮箱已被使用");
        }

        user.setUsername(username);
        user.setAvatar(avatar);
        user.setPhone(phone);
        user.setEmail(email);

        return userRepository.save(user);
    }

    // 修改密码
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!passwordUtil.checkPassword(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        user.setPassword(passwordUtil.encryptPassword(newPassword));
        userRepository.save(user);
    }
}