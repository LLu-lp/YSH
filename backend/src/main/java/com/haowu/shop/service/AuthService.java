package com.haowu.shop.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haowu.shop.entity.Admin;
import com.haowu.shop.entity.Merchant;
import com.haowu.shop.entity.Shop;
import com.haowu.shop.entity.User;
import com.haowu.shop.mapper.AdminRepository;
import com.haowu.shop.mapper.MerchantRepository;
import com.haowu.shop.mapper.ShopRepository;
import com.haowu.shop.mapper.UserRepository;
import com.haowu.shop.util.JwtUtil;
import com.haowu.shop.util.PasswordUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordUtil passwordUtil;

    // 用户登录
    public Map<String, Object> userLogin(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户账号已被禁用");
        }
        if (!passwordUtil.checkPassword(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        // 生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "user");
        claims.put("userId", user.getId());
        String token = jwtUtil.generateToken(user.getUsername(), claims);

        // 构建用户信息 Map
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("status", user.getStatus());
        userInfo.put("createTime", user.getCreateTime());
        userInfo.put("lastLoginTime", user.getLastLoginTime());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return result;
    }

    // 商家登录
    public Map<String, Object> merchantLogin(String username, String password) {
        Merchant merchant = merchantRepository.findByUsername(username);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        if (merchant.getStatus() == 0) {
            throw new RuntimeException("商家账号已被禁用");
        }
        if (!passwordUtil.checkPassword(password, merchant.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "merchant");
        claims.put("merchantId", merchant.getId());
        claims.put("shopId", merchant.getShopId());
        String token = jwtUtil.generateToken(merchant.getUsername(), claims);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("merchantInfo", merchant);
        return result;
    }

    // 管理员登录
    public Map<String, Object> adminLogin(String username, String password) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }
        if (admin.getStatus() == 0) {
            throw new RuntimeException("管理员账号已被禁用");
        }
        if (!passwordUtil.checkPassword(password, admin.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 更新最后登录时间
        admin.setLastLoginTime(LocalDateTime.now());
        adminRepository.save(admin);

        // 生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin");
        claims.put("adminId", admin.getId());
        String token = jwtUtil.generateToken(admin.getUsername(), claims);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("adminInfo", admin);
        return result;
    }

    // 用户注册
    public Long userRegister(String username, String password, String phone, String email) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 检查手机号是否已存在
        if (userRepository.findByPhone(phone) != null) {
            throw new RuntimeException("手机号已被注册");
        }
        // 检查邮箱是否已存在
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordUtil.encryptPassword(password));
        user.setPhone(phone);
        user.setEmail(email);
        user.setStatus(1);

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    // 商家注册
    public Long merchantRegister(String username, String password, String phone, String email, String shopName, String shopAddress) {
        // 检查用户名是否已存在
        if (merchantRepository.findByUsername(username) != null) {
            throw new RuntimeException("商家账号已存在");
        }

        // 先创建商家账号（不设置shopId）
        Merchant merchant = new Merchant();
        merchant.setUsername(username);
        merchant.setPassword(passwordUtil.encryptPassword(password));
        merchant.setPhone(phone);
        merchant.setEmail(email);
        merchant.setStatus(0); // 初始状态为禁用，等待审核
        Merchant savedMerchant = merchantRepository.save(merchant);

        // 再创建店铺，设置merchant_id
        Shop shop = new Shop();
        shop.setMerchantId(savedMerchant.getId());
        shop.setName(shopName);
        shop.setAddress(shopAddress);
        shop.setPhone(phone);
        shop.setStatus(0); // 初始状态为禁用
        shop.setAuditStatus(0); // 初始状态为待审核
        shop.setRating(5.0);
        shop.setSalesCount(0);
        Shop savedShop = shopRepository.save(shop);

        // 更新商家的shopId
        savedMerchant.setShopId(savedShop.getId());
        merchantRepository.save(savedMerchant);

        return savedMerchant.getId();
    }
}