package com.haowu.shop.controller;

import com.haowu.shop.dto.LoginDTO;
import com.haowu.shop.dto.RegisterDTO;
import com.haowu.shop.service.AuthService;
import com.haowu.shop.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 用户注册
    @PostMapping("/user/register")
    public ResponseUtil<Object> userRegister(@RequestBody RegisterDTO registerDTO) {
        try {
            Long userId = authService.userRegister(
                    registerDTO.getUsername(),
                    registerDTO.getPassword(),
                    registerDTO.getPhone(),
                    registerDTO.getEmail()
            );
            return ResponseUtil.success("注册成功", Map.of("userId", userId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 用户登录
    @PostMapping("/user/login")
    public ResponseUtil<Object> userLogin(@RequestBody LoginDTO loginDTO) {
        try {
            Map<String, Object> result = authService.userLogin(
                    loginDTO.getUsername(),
                    loginDTO.getPassword()
            );
            return ResponseUtil.success(result);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 商家登录
    @PostMapping("/merchant/login")
    public ResponseUtil<Object> merchantLogin(@RequestBody LoginDTO loginDTO) {
        try {
            Map<String, Object> result = authService.merchantLogin(
                    loginDTO.getUsername(),
                    loginDTO.getPassword()
            );
            return ResponseUtil.success(result);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 管理员登录
    @PostMapping("/admin/login")
    public ResponseUtil<Object> adminLogin(@RequestBody LoginDTO loginDTO) {
        try {
            Map<String, Object> result = authService.adminLogin(
                    loginDTO.getUsername(),
                    loginDTO.getPassword()
            );
            return ResponseUtil.success(result);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 商家注册
    @PostMapping("/merchant/register")
    public ResponseUtil<Object> merchantRegister(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String phone = request.get("phone");
            String email = request.get("email");
            String shopName = request.get("shopName");
            String shopAddress = request.get("shopAddress");
            
            Long merchantId = authService.merchantRegister(
                    username,
                    password,
                    phone,
                    email,
                    shopName,
                    shopAddress
            );
            return ResponseUtil.success("注册成功，等待审核", Map.of("merchantId", merchantId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }
}