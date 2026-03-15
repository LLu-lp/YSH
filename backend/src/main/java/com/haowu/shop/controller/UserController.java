package com.haowu.shop.controller;

import com.haowu.shop.entity.Address;
import com.haowu.shop.service.AddressService;
import com.haowu.shop.service.CouponService;
import com.haowu.shop.service.FavoriteService;
import com.haowu.shop.service.UserService;
import com.haowu.shop.util.ResponseUtil;
import com.haowu.shop.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private CouponService couponService;

    // 获取用户信息
    @GetMapping("/info")
    public ResponseUtil<Object> getUserInfo() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            return ResponseUtil.success(userService.getUserInfo(userId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 更新用户信息
    @PutMapping("/info")
    public ResponseUtil<Object> updateUserInfo(@RequestBody Map<String, String> request) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            String username = request.get("username");
            String avatar = request.get("avatar");
            String phone = request.get("phone");
            String email = request.get("email");
            return ResponseUtil.success(userService.updateUserInfo(userId, username, avatar, phone, email));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 修改密码
    @PutMapping("/password")
    public ResponseUtil<Object> changePassword(@RequestBody Map<String, String> request) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            userService.changePassword(userId, oldPassword, newPassword);
            return ResponseUtil.success("密码修改成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 收货地址管理
    // 获取地址列表
    @GetMapping("/addresses")
    public ResponseUtil<Object> getAddressList() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            return ResponseUtil.success(addressService.getAddressList(userId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 新增地址
    @PostMapping("/addresses")
    public ResponseUtil<Object> addAddress(@RequestBody Address address) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            address.setUserId(userId);
            return ResponseUtil.success(addressService.addAddress(address));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 修改地址
    @PutMapping("/addresses/{id}")
    public ResponseUtil<Object> updateAddress(@PathVariable Long id, @RequestBody Address address) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            address.setUserId(userId);
            return ResponseUtil.success(addressService.updateAddress(id, address));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 删除地址
    @DeleteMapping("/addresses/{id}")
    public ResponseUtil<Object> deleteAddress(@PathVariable Long id) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            addressService.deleteAddress(id, userId);
            return ResponseUtil.success("删除成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 设为默认地址
    @PutMapping("/addresses/{id}/default")
    public ResponseUtil<Object> setDefaultAddress(@PathVariable Long id) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            return ResponseUtil.success(addressService.setDefaultAddress(id, userId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 收藏管理
    // 获取收藏列表
    @GetMapping("/favorites")
    public ResponseUtil<Object> getFavorites() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            return ResponseUtil.success(favoriteService.getFavorites(userId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 收藏商品
    @PostMapping("/favorites")
    public ResponseUtil<Object> addFavorite(@RequestBody Map<String, Long> request) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            Long productId = request.get("productId");
            return ResponseUtil.success(favoriteService.addFavorite(userId, productId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 取消收藏
    @DeleteMapping("/favorites/{productId}")
    public ResponseUtil<Object> removeFavorite(@PathVariable Long productId) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            favoriteService.removeFavorite(userId, productId);
            return ResponseUtil.success("取消收藏成功");
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 优惠券管理
    // 获取用户优惠券列表
    @GetMapping("/coupons")
    public ResponseUtil<Object> getUserCoupons() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            return ResponseUtil.success(couponService.getUserCoupons(userId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 领取优惠券
    @PostMapping("/coupons/receive")
    public ResponseUtil<Object> receiveCoupon(@RequestBody Map<String, Long> request) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            Long couponId = request.get("couponId");
            return ResponseUtil.success(couponService.receiveCoupon(userId, couponId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 获取可领取的优惠券列表
    @GetMapping("/coupons/available")
    public ResponseUtil<Object> getAvailableCoupons() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            return ResponseUtil.success(couponService.getAvailableCouponsForReceive(userId));
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }
}