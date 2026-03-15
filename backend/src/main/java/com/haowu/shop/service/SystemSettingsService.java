package com.haowu.shop.service;

import com.haowu.shop.entity.SystemSettings;
import com.haowu.shop.mapper.SystemSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class SystemSettingsService {

    @Autowired
    private SystemSettingsRepository systemSettingsRepository;

    private static final Long SETTINGS_ID = 1L;

    // 获取系统设置
    public Map<String, Object> getSettings() {
        Optional<SystemSettings> optional = systemSettingsRepository.findById(SETTINGS_ID);
        if (optional.isPresent()) {
            SystemSettings settings = optional.get();
            return convertToMap(settings);
        }
        // 如果不存在，返回默认设置
        return getDefaultSettings();
    }

    // 保存系统设置
    public Map<String, Object> saveSettings(Map<String, Object> settingsMap) {
        SystemSettings settings = systemSettingsRepository.findById(SETTINGS_ID)
                .orElse(new SystemSettings());

        // 基础设置
        if (settingsMap.containsKey("platformName")) {
            settings.setPlatformName((String) settingsMap.get("platformName"));
        }
        if (settingsMap.containsKey("platformDesc")) {
            settings.setPlatformDesc((String) settingsMap.get("platformDesc"));
        }
        if (settingsMap.containsKey("platformLogo")) {
            settings.setPlatformLogo((String) settingsMap.get("platformLogo"));
        }
        if (settingsMap.containsKey("contactPhone")) {
            settings.setContactPhone((String) settingsMap.get("contactPhone"));
        }
        if (settingsMap.containsKey("contactEmail")) {
            settings.setContactEmail((String) settingsMap.get("contactEmail"));
        }

        // 交易设置
        if (settingsMap.containsKey("defaultShipping")) {
            settings.setDefaultShipping(convertToDouble(settingsMap.get("defaultShipping")));
        }
        if (settingsMap.containsKey("freeShippingAmount")) {
            settings.setFreeShippingAmount(convertToDouble(settingsMap.get("freeShippingAmount")));
        }
        if (settingsMap.containsKey("orderTimeout")) {
            settings.setOrderTimeout(convertToInt(settingsMap.get("orderTimeout")));
        }
        if (settingsMap.containsKey("refundDays")) {
            settings.setRefundDays(convertToInt(settingsMap.get("refundDays")));
        }

        // 商家设置
        if (settingsMap.containsKey("merchantDeposit")) {
            settings.setMerchantDeposit(convertToDouble(settingsMap.get("merchantDeposit")));
        }
        if (settingsMap.containsKey("platformCommission")) {
            settings.setPlatformCommission(convertToDouble(settingsMap.get("platformCommission")));
        }
        if (settingsMap.containsKey("merchantAuditRequired")) {
            settings.setMerchantAuditRequired((Boolean) settingsMap.get("merchantAuditRequired"));
        }

        // 用户设置
        if (settingsMap.containsKey("newUserCoupon")) {
            settings.setNewUserCoupon((Boolean) settingsMap.get("newUserCoupon"));
        }
        if (settingsMap.containsKey("newUserCouponAmount")) {
            settings.setNewUserCouponAmount(convertToDouble(settingsMap.get("newUserCouponAmount")));
        }
        if (settingsMap.containsKey("accountLockThreshold")) {
            settings.setAccountLockThreshold(convertToInt(settingsMap.get("accountLockThreshold")));
        }

        // 设置时间戳
        if (settings.getId() == null) {
            settings.setCreateTime(LocalDateTime.now());
        }
        settings.setUpdateTime(LocalDateTime.now());

        SystemSettings saved = systemSettingsRepository.save(settings);
        return convertToMap(saved);
    }

    // 重置为默认设置
    public Map<String, Object> resetSettings() {
        systemSettingsRepository.deleteById(SETTINGS_ID);
        return getDefaultSettings();
    }

    private Map<String, Object> convertToMap(SystemSettings settings) {
        return Map.ofEntries(
                Map.entry("platformName", settings.getPlatformName()),
                Map.entry("platformDesc", settings.getPlatformDesc()),
                Map.entry("platformLogo", settings.getPlatformLogo()),
                Map.entry("contactPhone", settings.getContactPhone()),
                Map.entry("contactEmail", settings.getContactEmail()),
                Map.entry("defaultShipping", settings.getDefaultShipping()),
                Map.entry("freeShippingAmount", settings.getFreeShippingAmount()),
                Map.entry("orderTimeout", settings.getOrderTimeout()),
                Map.entry("refundDays", settings.getRefundDays()),
                Map.entry("merchantDeposit", settings.getMerchantDeposit()),
                Map.entry("platformCommission", settings.getPlatformCommission()),
                Map.entry("merchantAuditRequired", settings.getMerchantAuditRequired()),
                Map.entry("newUserCoupon", settings.getNewUserCoupon()),
                Map.entry("newUserCouponAmount", settings.getNewUserCouponAmount()),
                Map.entry("accountLockThreshold", settings.getAccountLockThreshold())
        );
    }

    private Map<String, Object> getDefaultSettings() {
        return Map.ofEntries(
                Map.entry("platformName", "好物购买系统"),
                Map.entry("platformDesc", "一个功能完整的电商平台"),
                Map.entry("platformLogo", ""),
                Map.entry("contactPhone", "400-123-4567"),
                Map.entry("contactEmail", "support@example.com"),
                Map.entry("defaultShipping", 10.0),
                Map.entry("freeShippingAmount", 100.0),
                Map.entry("orderTimeout", 30),
                Map.entry("refundDays", 7),
                Map.entry("merchantDeposit", 1000.0),
                Map.entry("platformCommission", 5.0),
                Map.entry("merchantAuditRequired", true),
                Map.entry("newUserCoupon", true),
                Map.entry("newUserCouponAmount", 10.0),
                Map.entry("accountLockThreshold", 5)
        );
    }

    private Double convertToDouble(Object obj) {
        if (obj == null) return 0.0;
        if (obj instanceof Double) return (Double) obj;
        if (obj instanceof Integer) return ((Integer) obj).doubleValue();
        if (obj instanceof String) return Double.parseDouble((String) obj);
        return Double.parseDouble(obj.toString());
    }

    private Integer convertToInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Double) return ((Double) obj).intValue();
        if (obj instanceof String) return Integer.parseInt((String) obj);
        return Integer.parseInt(obj.toString());
    }
}

