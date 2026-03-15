package com.haowu.shop.controller;

import com.haowu.shop.service.DataExportService;
import com.haowu.shop.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/export")
public class DataExportController {

    @Autowired
    private DataExportService dataExportService;

    // 导出订单数据
    @GetMapping("/orders")
    public ResponseUtil<Object> exportOrders(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        try {
            List<Map<String, Object>> data = dataExportService.getOrdersForExport(status, startDate, endDate);
            return ResponseUtil.success(data);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 导出用户数据
    @GetMapping("/users")
    public ResponseUtil<Object> exportUsers(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        try {
            List<Map<String, Object>> data = dataExportService.getUsersForExport(status, startDate, endDate);
            return ResponseUtil.success(data);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 导出商品数据
    @GetMapping("/products")
    public ResponseUtil<Object> exportProducts(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "auditStatus", required = false) Integer auditStatus) {
        try {
            List<Map<String, Object>> data = dataExportService.getProductsForExport(status, auditStatus);
            return ResponseUtil.success(data);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }

    // 导出商家数据
    @GetMapping("/merchants")
    public ResponseUtil<Object> exportMerchants(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "auditStatus", required = false) Integer auditStatus) {
        try {
            List<Map<String, Object>> data = dataExportService.getMerchantsForExport(status, auditStatus);
            return ResponseUtil.success(data);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage());
        }
    }
}

