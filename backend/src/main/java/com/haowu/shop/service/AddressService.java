package com.haowu.shop.service;

import com.haowu.shop.entity.Address;
import com.haowu.shop.mapper.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    // 获取用户地址列表
    public List<Address> getAddressList(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    // 添加地址
    public Address addAddress(Address address) {
        // 如果设置为默认地址，先将其他地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            Address defaultAddress = addressRepository.findByUserIdAndIsDefault(address.getUserId(), 1);
            if (defaultAddress != null) {
                defaultAddress.setIsDefault(0);
                addressRepository.save(defaultAddress);
            }
        }
        return addressRepository.save(address);
    }

    // 更新地址
    public Address updateAddress(Long addressId, Address address) {
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        // 检查地址是否属于当前用户
        if (!existingAddress.getUserId().equals(address.getUserId())) {
            throw new RuntimeException("无权操作此地址");
        }

        // 如果设置为默认地址，先将其他地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            Address defaultAddress = addressRepository.findByUserIdAndIsDefault(address.getUserId(), 1);
            if (defaultAddress != null && !defaultAddress.getId().equals(addressId)) {
                defaultAddress.setIsDefault(0);
                addressRepository.save(defaultAddress);
            }
        }

        // 更新地址信息
        existingAddress.setReceiver(address.getReceiver());
        existingAddress.setPhone(address.getPhone());
        existingAddress.setProvince(address.getProvince());
        existingAddress.setCity(address.getCity());
        existingAddress.setDistrict(address.getDistrict());
        existingAddress.setDetail(address.getDetail());
        existingAddress.setIsDefault(address.getIsDefault());

        return addressRepository.save(existingAddress);
    }

    // 删除地址
    public void deleteAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        // 检查地址是否属于当前用户
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此地址");
        }

        addressRepository.delete(address);
    }

    // 设置默认地址
    public Address setDefaultAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        // 检查地址是否属于当前用户
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此地址");
        }

        // 将其他地址设为非默认
        Address defaultAddress = addressRepository.findByUserIdAndIsDefault(userId, 1);
        if (defaultAddress != null && !defaultAddress.getId().equals(addressId)) {
            defaultAddress.setIsDefault(0);
            addressRepository.save(defaultAddress);
        }

        // 设置当前地址为默认
        address.setIsDefault(1);
        return addressRepository.save(address);
    }
}
