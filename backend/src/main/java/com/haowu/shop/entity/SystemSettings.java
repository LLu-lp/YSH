package com.haowu.shop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_system_settings")
public class SystemSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 基础设置
    @Column(name = "platform_name", length = 100)
    private String platformName;
    
    @Column(name = "platform_desc", columnDefinition = "TEXT")
    private String platformDesc;
    
    @Column(name = "platform_logo", length = 500)
    private String platformLogo;
    
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;
    
    @Column(name = "contact_email", length = 100)
    private String contactEmail;
    
    // 交易设置
    @Column(name = "default_shipping")
    private Double defaultShipping;
    
    @Column(name = "free_shipping_amount")
    private Double freeShippingAmount;
    
    @Column(name = "order_timeout")
    private Integer orderTimeout;
    
    @Column(name = "refund_days")
    private Integer refundDays;
    
    // 商家设置
    @Column(name = "merchant_deposit")
    private Double merchantDeposit;
    
    @Column(name = "platform_commission")
    private Double platformCommission;
    
    @Column(name = "merchant_audit_required")
    private Boolean merchantAuditRequired;
    
    // 用户设置
    @Column(name = "new_user_coupon")
    private Boolean newUserCoupon;
    
    @Column(name = "new_user_coupon_amount")
    private Double newUserCouponAmount;
    
    @Column(name = "account_lock_threshold")
    private Integer accountLockThreshold;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPlatformName() {
        return platformName;
    }
    
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }
    
    public String getPlatformDesc() {
        return platformDesc;
    }
    
    public void setPlatformDesc(String platformDesc) {
        this.platformDesc = platformDesc;
    }
    
    public String getPlatformLogo() {
        return platformLogo;
    }
    
    public void setPlatformLogo(String platformLogo) {
        this.platformLogo = platformLogo;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public Double getDefaultShipping() {
        return defaultShipping;
    }
    
    public void setDefaultShipping(Double defaultShipping) {
        this.defaultShipping = defaultShipping;
    }
    
    public Double getFreeShippingAmount() {
        return freeShippingAmount;
    }
    
    public void setFreeShippingAmount(Double freeShippingAmount) {
        this.freeShippingAmount = freeShippingAmount;
    }
    
    public Integer getOrderTimeout() {
        return orderTimeout;
    }
    
    public void setOrderTimeout(Integer orderTimeout) {
        this.orderTimeout = orderTimeout;
    }
    
    public Integer getRefundDays() {
        return refundDays;
    }
    
    public void setRefundDays(Integer refundDays) {
        this.refundDays = refundDays;
    }
    
    public Double getMerchantDeposit() {
        return merchantDeposit;
    }
    
    public void setMerchantDeposit(Double merchantDeposit) {
        this.merchantDeposit = merchantDeposit;
    }
    
    public Double getPlatformCommission() {
        return platformCommission;
    }
    
    public void setPlatformCommission(Double platformCommission) {
        this.platformCommission = platformCommission;
    }
    
    public Boolean getMerchantAuditRequired() {
        return merchantAuditRequired;
    }
    
    public void setMerchantAuditRequired(Boolean merchantAuditRequired) {
        this.merchantAuditRequired = merchantAuditRequired;
    }
    
    public Boolean getNewUserCoupon() {
        return newUserCoupon;
    }
    
    public void setNewUserCoupon(Boolean newUserCoupon) {
        this.newUserCoupon = newUserCoupon;
    }
    
    public Double getNewUserCouponAmount() {
        return newUserCouponAmount;
    }
    
    public void setNewUserCouponAmount(Double newUserCouponAmount) {
        this.newUserCouponAmount = newUserCouponAmount;
    }
    
    public Integer getAccountLockThreshold() {
        return accountLockThreshold;
    }
    
    public void setAccountLockThreshold(Integer accountLockThreshold) {
        this.accountLockThreshold = accountLockThreshold;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}

