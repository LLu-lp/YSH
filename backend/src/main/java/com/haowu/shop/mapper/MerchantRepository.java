package com.haowu.shop.mapper;

import com.haowu.shop.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Merchant findByUsername(String username);
    Page<Merchant> findByStatus(Integer status, Pageable pageable);
    Page<Merchant> findByUsernameContainingOrPhoneContaining(String username, String phone, Pageable pageable);
}