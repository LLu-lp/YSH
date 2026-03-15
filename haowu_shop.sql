/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80037 (8.0.37)
 Source Host           : localhost:3306
 Source Schema         : haowu_shop

 Target Server Type    : MySQL
 Target Server Version : 80037 (8.0.37)
 File Encoding         : 65001

 Date: 15/03/2026 23:19:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_address
-- ----------------------------
DROP TABLE IF EXISTS `t_address`;
CREATE TABLE `t_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime(6) NOT NULL,
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_default` tinyint NULL DEFAULT 0,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `receiver` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_address
-- ----------------------------
INSERT INTO `t_address` VALUES (1, '杭州市', '2026-03-14 14:45:31.000000', '文三路123号阳光小区1栋101室', '西湖区', 1, '13800138001', '浙江省', '张三', 1);
INSERT INTO `t_address` VALUES (2, '杭州市', '2026-03-14 14:45:31.000000', '滨盛路456号创新大厦A座1502', '滨江区', 0, '13800138001', '浙江省', '张三', 1);
INSERT INTO `t_address` VALUES (3, '深圳市', '2026-03-14 14:45:31.000000', '科技园路789号腾讯大厦', '南山区', 1, '13800138002', '广东省', '李四', 2);
INSERT INTO `t_address` VALUES (4, '上海市', '2026-03-14 14:45:31.000000', '张江高科技园区博云路2号', '浦东新区', 1, '13800138003', '上海市', '王五', 3);
INSERT INTO `t_address` VALUES (5, '北京市', '2026-03-14 14:45:31.000000', '建国路101号国贸中心', '朝阳区', 1, '13800138004', '北京市', '赵六', 4);
INSERT INTO `t_address` VALUES (6, '成都市', '2026-03-14 14:45:31.000000', '天府大道666号天府软件园', '武侯区', 1, '13800138005', '四川省', '孙七', 5);
INSERT INTO `t_address` VALUES (8, '重庆', '2026-03-15 13:03:09.402834', '重庆工程学院', '大足区', 1, '19987364892', '重庆', 'ysh', 6);

-- ----------------------------
-- Table structure for t_admin
-- ----------------------------
DROP TABLE IF EXISTS `t_admin`;
CREATE TABLE `t_admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `last_login_time` datetime(6) NULL DEFAULT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'admin',
  `status` tinyint NOT NULL DEFAULT 1,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_3f137q5sgpm7mtbeaxixnug35`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_admin
-- ----------------------------
INSERT INTO `t_admin` VALUES (1, '2026-03-14 14:45:31.000000', '2026-03-15 23:18:34.730169', '$2a$10$CvV.v4gMJAHDQ/dt9jkVU.CwsoJEy4Xb4cIYQHtPqChNOrePc7jzW', 'super_admin', 1, 'admin');
INSERT INTO `t_admin` VALUES (2, '2026-03-14 14:45:31.000000', NULL, '$2a$10$CvV.v4gMJAHDQ/dt9jkVU.CwsoJEy4Xb4cIYQHtPqChNOrePc7jzW', 'admin', 1, 'operator');

-- ----------------------------
-- Table structure for t_banner
-- ----------------------------
DROP TABLE IF EXISTS `t_banner`;
CREATE TABLE `t_banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `sort` int NOT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `subtitle` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `update_time` datetime(6) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_banner
-- ----------------------------
INSERT INTO `t_banner` VALUES (1, '2026-03-14 14:45:31.000000', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?w=1200&h=400&fit=crop', '/pages/product/list?category=1', 1, 1, '全场5折起，限时抢购', '春季新品上市', '2026-03-14 14:45:31.000000');
INSERT INTO `t_banner` VALUES (2, '2026-03-14 14:45:31.000000', 'https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=1200&h=400&fit=crop', '/pages/product/list?category=2', 2, 1, '爆款手机低至1999元', '数码狂欢节', '2026-03-14 14:45:31.000000');
INSERT INTO `t_banner` VALUES (3, '2026-03-14 14:45:31.000000', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=1200&h=400&fit=crop', '/pages/product/list?category=3', 3, 1, '品质家居，温馨生活', '家居生活节', '2026-03-14 14:45:31.000000');
INSERT INTO `t_banner` VALUES (4, '2026-03-14 14:45:31.000000', 'https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=1200&h=400&fit=crop', '/pages/product/list?category=4', 4, 1, '运动装备全场8折', '运动户外季', '2026-03-14 14:45:31.000000');
INSERT INTO `t_banner` VALUES (5, '2026-03-14 14:45:31.000000', 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=1200&h=400&fit=crop', '/pages/product/list?category=5', 5, 1, '进口零食满99减20', '美食嘉年华', '2026-03-14 14:45:31.000000');

-- ----------------------------
-- Table structure for t_cart
-- ----------------------------
DROP TABLE IF EXISTS `t_cart`;
CREATE TABLE `t_cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `checked` tinyint NULL DEFAULT 1,
  `create_time` datetime(6) NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  `shop_id` bigint NOT NULL,
  `spec` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `update_time` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_cart
-- ----------------------------
INSERT INTO `t_cart` VALUES (1, 1, '2026-03-14 14:45:31.000000', 1, 1, 1, '黑色,L', '2026-03-14 14:45:31.000000', 1);
INSERT INTO `t_cart` VALUES (2, 1, '2026-03-14 14:45:31.000000', 5, 1, 2, '星空黑,256GB', '2026-03-14 14:45:31.000000', 1);
INSERT INTO `t_cart` VALUES (3, 1, '2026-03-14 14:45:31.000000', 3, 2, 1, '红色,M', '2026-03-14 14:45:31.000000', 2);
INSERT INTO `t_cart` VALUES (4, 1, '2026-03-14 14:45:31.000000', 13, 1, 3, '灰色,三人位', '2026-03-14 14:45:31.000000', 2);
INSERT INTO `t_cart` VALUES (5, 1, '2026-03-14 14:45:31.000000', 14, 1, 3, '蓝色,1.8m床', '2026-03-14 14:45:31.000000', 3);
INSERT INTO `t_cart` VALUES (6, 1, '2026-03-14 14:45:31.000000', 16, 1, 4, '黑色,42', '2026-03-14 14:45:31.000000', 4);
INSERT INTO `t_cart` VALUES (7, 1, '2026-03-14 14:45:31.000000', 19, 2, 5, '500g装', '2026-03-14 14:45:31.000000', 5);

-- ----------------------------
-- Table structure for t_category
-- ----------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_id` bigint NULL DEFAULT 0,
  `sort` int NULL DEFAULT 0,
  `status` tinyint NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_category
-- ----------------------------
INSERT INTO `t_category` VALUES (1, 'https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=100&h=100&fit=crop', '服装', 0, 1, 1);
INSERT INTO `t_category` VALUES (2, 'https://images.unsplash.com/photo-1498049794561-7780e7231661?w=100&h=100&fit=crop', '数码电子', 0, 2, 1);
INSERT INTO `t_category` VALUES (3, 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=100&h=100&fit=crop', '家居生活', 0, 3, 1);
INSERT INTO `t_category` VALUES (4, 'https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=100&h=100&fit=crop', '运动户外', 0, 4, 1);
INSERT INTO `t_category` VALUES (5, 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=100&h=100&fit=crop', '食品饮料', 0, 5, 1);
INSERT INTO `t_category` VALUES (6, 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=100&h=100&fit=crop', '美妆护肤', 0, 6, 1);
INSERT INTO `t_category` VALUES (7, 'https://images.unsplash.com/photo-1617137968427-85924c800a22?w=100&h=100&fit=crop', '男装', 1, 1, 1);
INSERT INTO `t_category` VALUES (8, 'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=100&h=100&fit=crop', '女装', 1, 2, 1);
INSERT INTO `t_category` VALUES (9, 'https://images.unsplash.com/photo-1519238263530-99bdd11df2ea?w=100&h=100&fit=crop', '童装', 1, 3, 1);
INSERT INTO `t_category` VALUES (10, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=100&h=100&fit=crop', '手机', 2, 1, 1);
INSERT INTO `t_category` VALUES (11, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=100&h=100&fit=crop', '电脑', 2, 2, 1);
INSERT INTO `t_category` VALUES (12, 'https://images.unsplash.com/photo-1583394838336-acd977736f90?w=100&h=100&fit=crop', '配件', 2, 3, 1);
INSERT INTO `t_category` VALUES (13, 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=100&h=100&fit=crop', '家具', 3, 1, 1);
INSERT INTO `t_category` VALUES (14, 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=100&h=100&fit=crop', '家纺', 3, 2, 1);
INSERT INTO `t_category` VALUES (15, 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=100&h=100&fit=crop', '厨具', 3, 3, 1);

-- ----------------------------
-- Table structure for t_coupon
-- ----------------------------
DROP TABLE IF EXISTS `t_coupon`;
CREATE TABLE `t_coupon`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `end_time` datetime(6) NOT NULL,
  `min_amount` double NULL DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `remaining_count` int NOT NULL,
  `shop_id` bigint NULL DEFAULT NULL,
  `start_time` datetime(6) NOT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `total_count` int NOT NULL,
  `type` int NOT NULL,
  `update_time` datetime(6) NOT NULL,
  `value` double NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_coupon
-- ----------------------------
INSERT INTO `t_coupon` VALUES (1, '2026-03-14 14:45:31.000000', '2026-04-13 14:45:31.000000', 100, '新人专享优惠券', 849, NULL, '2026-03-14 14:45:31.000000', 1, 1000, 1, '2026-03-14 16:04:36.510141', 20);
INSERT INTO `t_coupon` VALUES (2, '2026-03-14 14:45:31.000000', '2026-05-13 14:45:31.000000', 300, '全场通用满减券', 419, NULL, '2026-03-14 14:45:31.000000', 1, 500, 1, '2026-03-14 16:04:53.880232', 50);
INSERT INTO `t_coupon` VALUES (3, '2026-03-14 14:45:31.000000', '2026-03-29 14:45:31.000000', 500, '限时特惠优惠券', 179, NULL, '2026-03-14 14:45:31.000000', 1, 200, 1, '2026-03-14 16:04:54.716139', 100);
INSERT INTO `t_coupon` VALUES (4, '2026-03-14 14:45:31.000000', '2026-04-13 14:45:31.000000', 200, '服装店专属优惠券', 249, 1, '2026-03-14 14:45:31.000000', 1, 300, 2, '2026-03-15 21:41:01.185707', 30);
INSERT INTO `t_coupon` VALUES (5, '2026-03-14 14:45:31.000000', '2026-04-13 14:45:31.000000', 2000, '数码店专属优惠券', 85, 2, '2026-03-14 14:45:31.000000', 1, 100, 2, '2026-03-14 14:45:31.000000', 200);
INSERT INTO `t_coupon` VALUES (6, '2026-03-14 14:45:31.000000', '2026-04-13 14:45:31.000000', 500, '家居店专属优惠券', 160, 3, '2026-03-14 14:45:31.000000', 1, 200, 2, '2026-03-14 14:45:31.000000', 50);
INSERT INTO `t_coupon` VALUES (7, '2026-03-14 14:45:31.000000', '2026-04-13 14:45:31.000000', 300, '运动店专属优惠券', 120, 4, '2026-03-14 14:45:31.000000', 1, 150, 2, '2026-03-14 14:45:31.000000', 40);
INSERT INTO `t_coupon` VALUES (8, '2026-03-14 14:45:31.000000', '2026-04-13 14:45:31.000000', 80, '美食店专属优惠券', 350, 5, '2026-03-14 14:45:31.000000', 1, 400, 2, '2026-03-14 14:45:31.000000', 15);
INSERT INTO `t_coupon` VALUES (10, '2026-03-15 22:44:19.492232', '2026-10-15 00:00:00.000000', 0, '新年优惠劵', 100, 2, '2026-03-15 00:00:00.000000', 1, 100, 2, '2026-03-15 22:44:19.492232', 5);

-- ----------------------------
-- Table structure for t_favorite
-- ----------------------------
DROP TABLE IF EXISTS `t_favorite`;
CREATE TABLE `t_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_favorite
-- ----------------------------
INSERT INTO `t_favorite` VALUES (1, '2026-03-14 14:45:31.000000', 3, 1);
INSERT INTO `t_favorite` VALUES (2, '2026-03-14 14:45:31.000000', 5, 1);
INSERT INTO `t_favorite` VALUES (3, '2026-03-14 14:45:31.000000', 14, 1);
INSERT INTO `t_favorite` VALUES (4, '2026-03-14 14:45:31.000000', 1, 2);
INSERT INTO `t_favorite` VALUES (5, '2026-03-14 14:45:31.000000', 16, 2);
INSERT INTO `t_favorite` VALUES (6, '2026-03-14 14:45:31.000000', 5, 3);
INSERT INTO `t_favorite` VALUES (7, '2026-03-14 14:45:31.000000', 19, 3);
INSERT INTO `t_favorite` VALUES (8, '2026-03-14 14:45:31.000000', 3, 4);
INSERT INTO `t_favorite` VALUES (9, '2026-03-14 14:45:31.000000', 13, 4);
INSERT INTO `t_favorite` VALUES (10, '2026-03-14 14:45:31.000000', 1, 5);
INSERT INTO `t_favorite` VALUES (11, '2026-03-14 18:28:01.706075', 12, 6);
INSERT INTO `t_favorite` VALUES (12, '2026-03-15 17:01:43.744332', 2, 6);

-- ----------------------------
-- Table structure for t_merchant
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant`;
CREATE TABLE `t_merchant`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `shop_id` bigint NULL DEFAULT NULL,
  `shop_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `update_time` datetime(6) NOT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_po71ea3c6ngm23aujr832pkne`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_merchant
-- ----------------------------
INSERT INTO `t_merchant` VALUES (1, '2026-03-14 14:45:31.000000', 'merchant001@shop.com', '$2a$10$CvV.v4gMJAHDQ/dt9jkVU.CwsoJEy4Xb4cIYQHtPqChNOrePc7jzW', '13900139001', NULL, 1, '时尚潮流服饰店', 3, '2026-03-15 16:04:12.473208', 'merchant001');
INSERT INTO `t_merchant` VALUES (2, '2026-03-14 14:45:31.000000', 'merchant002@shop.com', '$2a$10$CvV.v4gMJAHDQ/dt9jkVU.CwsoJEy4Xb4cIYQHtPqChNOrePc7jzW', '13900139002', NULL, 2, '数码科技专营店', 1, '2026-03-14 14:45:31.000000', 'merchant002');
INSERT INTO `t_merchant` VALUES (3, '2026-03-14 14:45:31.000000', 'merchant003@shop.com', '$2a$10$CvV.v4gMJAHDQ/dt9jkVU.CwsoJEy4Xb4cIYQHtPqChNOrePc7jzW', '13900139003', NULL, 3, '家居生活馆', 1, '2026-03-14 14:45:31.000000', 'merchant003');
INSERT INTO `t_merchant` VALUES (4, '2026-03-14 14:45:31.000000', 'merchant004@shop.com', '$2a$10$CvV.v4gMJAHDQ/dt9jkVU.CwsoJEy4Xb4cIYQHtPqChNOrePc7jzW', '13900139004', NULL, 4, '运动户外专营', 1, '2026-03-14 14:45:31.000000', 'merchant004');
INSERT INTO `t_merchant` VALUES (5, '2026-03-14 14:45:31.000000', 'merchant005@shop.com', '$2a$10$CvV.v4gMJAHDQ/dt9jkVU.CwsoJEy4Xb4cIYQHtPqChNOrePc7jzW', '13900139005', NULL, 5, '美食零食铺', 1, '2026-03-14 14:45:31.000000', 'merchant005');
INSERT INTO `t_merchant` VALUES (11, '2026-03-15 16:03:36.773690', '', '$2a$10$rD5IWRhx9y7q7DpRZmhTYuKv1oiocu.dNRlTPqU3oVnLKkAkbltw2', '19978665473', NULL, 11, NULL, 1, '2026-03-15 16:07:33.276085', 'YangSH');
INSERT INTO `t_merchant` VALUES (12, '2026-03-15 23:07:59.373216', '', '$2a$10$zLbO.tuGu5U62saH.svjY.auwefH9PwaqTVlfi2CtDgA4oj6cjefe', '18875896746', NULL, 12, NULL, 1, '2026-03-15 23:08:14.972644', 'YangShuHui');

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cancel_time` datetime(6) NULL DEFAULT NULL,
  `confirm_time` datetime(6) NULL DEFAULT NULL,
  `create_time` datetime(6) NOT NULL,
  `express_company` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `express_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `order_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `pay_amount` decimal(10, 2) NOT NULL,
  `pay_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `pay_time` datetime(6) NULL DEFAULT NULL,
  `receiver_address` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `send_time` datetime(6) NULL DEFAULT NULL,
  `shop_id` bigint NOT NULL,
  `status` tinyint NOT NULL DEFAULT 0,
  `total_amount` decimal(10, 2) NOT NULL,
  `update_time` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  `coupon_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_fjie9ovlyccw6819bahkq6b59`(`order_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES (1, NULL, '2026-03-14 14:45:31.000000', '2026-03-14 14:45:31.000000', '顺丰速运', 'SF1234567890', '2026031300001', 299.00, 'alipay', '2026-03-14 14:45:31.000000', '浙江省杭州市西湖区文三路123号阳光小区1栋101室', '张三', '13800138001', '请尽快发货', '2026-03-14 14:45:31.000000', 1, 3, 598.00, '2026-03-14 14:45:31.000000', 1, NULL);
INSERT INTO `t_order` VALUES (2, NULL, NULL, '2026-03-14 14:45:31.000000', '圆通快递', 'YT9876543210', '2026031300002', 3999.00, 'wechat', '2026-03-14 14:45:31.000000', '浙江省杭州市西湖区文三路123号阳光小区1栋101室', '张三', '13800138001', '', '2026-03-14 14:45:31.000000', 2, 2, 3999.00, '2026-03-14 14:45:31.000000', 1, NULL);
INSERT INTO `t_order` VALUES (3, NULL, NULL, '2026-03-14 14:45:31.000000', '顺丰快递', 'fbfdshfsdhfds', '2026031300003', 336.00, 'alipay', '2026-03-14 14:45:31.000000', '广东省深圳市南山区科技园路789号腾讯大厦', '李四', '13800138002', '周末送货', '2026-03-14 18:33:41.145781', 1, 2, 336.00, '2026-03-14 18:33:41.145781', 2, NULL);
INSERT INTO `t_order` VALUES (4, NULL, NULL, '2026-03-14 14:45:31.000000', NULL, NULL, '2026031300004', 299.00, 'wechat', NULL, '上海市浦东新区张江高科技园区博云路2号', '王五', '13800138003', '', NULL, 3, 0, 299.00, '2026-03-14 14:45:31.000000', 3, NULL);
INSERT INTO `t_order` VALUES (5, NULL, '2026-03-14 14:45:31.000000', '2026-03-14 14:45:31.000000', '中通快递', 'ZT1122334455', '2026031300005', 399.00, 'alipay', '2026-03-14 14:45:31.000000', '北京市朝阳区建国路101号国贸中心', '赵六', '13800138004', '包装完好', '2026-03-14 14:45:31.000000', 4, 3, 399.00, '2026-03-14 14:45:31.000000', 4, NULL);
INSERT INTO `t_order` VALUES (6, '2026-03-14 14:45:31.000000', NULL, '2026-03-14 14:45:31.000000', NULL, NULL, '2026031300006', 256.00, 'wechat', NULL, '四川省成都市武侯区天府大道666号天府软件园', '孙七', '13800138005', '', NULL, 5, 4, 256.00, '2026-03-14 14:45:31.000000', 5, NULL);
INSERT INTO `t_order` VALUES (8, NULL, '2026-03-14 19:40:54.651136', '2026-03-14 18:28:30.019393', '顺丰快递', '萨法沙发', 'ORD1773484110016660F4F43', 1599.00, 'alipay', '2026-03-14 18:28:37.032758', '1111', '1', '1', '', '2026-03-14 19:40:26.030025', 2, 4, 1599.00, '2026-03-14 22:06:19.258396', 6, NULL);
INSERT INTO `t_order` VALUES (9, NULL, NULL, '2026-03-14 19:39:08.623116', '顺丰快递', 'dgsdfgbdfsbd', 'ORD1773488348614B239D0A8', 5499.00, 'alipay', '2026-03-14 19:39:11.905064', '1111', '1', '1', '', '2026-03-14 22:08:18.556845', 2, 4, 5499.00, '2026-03-15 13:04:21.113593', 6, NULL);
INSERT INTO `t_order` VALUES (10, NULL, '2026-03-14 22:09:55.600217', '2026-03-14 22:07:49.325091', '顺丰快递', 'gkjbkjb,', 'ORD17734972693224D5B63A8', 1599.00, 'alipay', '2026-03-14 22:07:53.986997', '1111', '1', '1', '', '2026-03-14 22:09:19.771207', 2, 4, 1599.00, '2026-03-15 13:04:24.382001', 6, NULL);
INSERT INTO `t_order` VALUES (11, '2026-03-15 16:40:46.587889', NULL, '2026-03-15 16:33:56.598250', NULL, NULL, 'ORD177356363659210535EA8', 299.00, NULL, NULL, '重庆重庆大足区重庆工程学院', 'ysh', '19987364892', '', NULL, 1, 4, 299.00, '2026-03-15 16:40:46.587889', 6, NULL);
INSERT INTO `t_order` VALUES (12, '2026-03-15 16:50:25.224655', NULL, '2026-03-15 16:40:57.788450', NULL, NULL, 'ORD177356405778808F29065', 299.00, NULL, NULL, '重庆重庆大足区重庆工程学院', 'ysh', '19987364892', '', NULL, 1, 4, 299.00, '2026-03-15 16:50:25.239205', 6, NULL);
INSERT INTO `t_order` VALUES (13, NULL, '2026-03-15 16:52:35.637724', '2026-03-15 16:50:51.794370', '顺丰快递', 'csavdsvsadsd', 'ORD17735646517897064C66E', 199.00, 'alipay', '2026-03-15 16:50:59.862622', '重庆重庆大足区重庆工程学院', 'ysh', '19987364892', '', '2026-03-15 16:52:12.807695', 1, 3, 299.00, '2026-03-15 16:52:35.637724', 6, 14);
INSERT INTO `t_order` VALUES (14, NULL, NULL, '2026-03-15 21:41:21.328671', NULL, NULL, 'ORD17735820813254BE1C47C', 138.00, 'alipay', '2026-03-15 21:41:42.932421', '重庆重庆大足区重庆工程学院', 'ysh', '19987364892', '', NULL, 1, 1, 168.00, '2026-03-15 21:41:42.935588', 6, 16);

-- ----------------------------
-- Table structure for t_order_item
-- ----------------------------
DROP TABLE IF EXISTS `t_order_item`;
CREATE TABLE `t_order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `order_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `product_id` bigint NOT NULL,
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `quantity` int NOT NULL,
  `reviewed` tinyint NULL DEFAULT 0,
  `spec` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order_item
-- ----------------------------
INSERT INTO `t_order_item` VALUES (1, 1, '2026031300001', 299.00, 1, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop', '男士休闲商务西装外套', 1, 1, '黑色,L');
INSERT INTO `t_order_item` VALUES (2, 2, '2026031300002', 3999.00, 5, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop', '智能手机旗舰版 5G', 1, 0, '星空黑,256GB');
INSERT INTO `t_order_item` VALUES (3, 3, '2026031300003', 168.00, 3, 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=400&h=400&fit=crop', '女装连衣裙夏季新款', 2, 0, '红色,M');
INSERT INTO `t_order_item` VALUES (4, 4, '2026031300004', 299.00, 14, 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=400&h=400&fit=crop', '纯棉四件套床上用品', 1, 0, '蓝色,1.8m床');
INSERT INTO `t_order_item` VALUES (5, 5, '2026031300005', 399.00, 16, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop', '专业跑步运动鞋', 1, 1, '黑色,42');
INSERT INTO `t_order_item` VALUES (6, 6, '2026031300006', 128.00, 19, 'https://images.unsplash.com/photo-1599599810769-bcde5a160d32?w=400&h=400&fit=crop', '进口坚果礼盒装', 2, 0, '500g装');
INSERT INTO `t_order_item` VALUES (7, 8, 'ORD1773484110016660F4F43', 1599.00, 6, 'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=400&h=400&fit=crop', '智能手机青春版', 1, 0, '');
INSERT INTO `t_order_item` VALUES (8, 9, 'ORD1773488348614B239D0A8', 5499.00, 7, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop', '轻薄笔记本电脑', 1, 0, '');
INSERT INTO `t_order_item` VALUES (9, 10, 'ORD17734972693224D5B63A8', 1599.00, 6, 'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=400&h=400&fit=crop', '智能手机青春版', 1, 0, '');
INSERT INTO `t_order_item` VALUES (10, 11, 'ORD177356363659210535EA8', 299.00, 1, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop', '男士休闲商务西装外套', 1, 0, '');
INSERT INTO `t_order_item` VALUES (11, 12, 'ORD177356405778808F29065', 299.00, 1, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop', '男士休闲商务西装外套', 1, 0, '');
INSERT INTO `t_order_item` VALUES (12, 13, 'ORD17735646517897064C66E', 299.00, 1, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop', '男士休闲商务西装外套', 1, 1, '');
INSERT INTO `t_order_item` VALUES (13, 14, 'ORD17735820813254BE1C47C', 168.00, 3, 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=400&h=400&fit=crop', '女装连衣裙夏季新款', 1, 0, '');

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `audit_status` tinyint NOT NULL DEFAULT 0,
  `category_id` bigint NOT NULL,
  `create_time` datetime(6) NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `main_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `original_price` decimal(10, 2) NULL DEFAULT NULL,
  `price` decimal(10, 2) NOT NULL,
  `rating` decimal(3, 1) NULL DEFAULT 5.0,
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `sales_count` int NULL DEFAULT 0,
  `shop_id` bigint NOT NULL,
  `specs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `status` tinyint NOT NULL DEFAULT 0,
  `stock` int NOT NULL DEFAULT 0,
  `update_time` datetime(6) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_product
-- ----------------------------
INSERT INTO `t_product` VALUES (1, '审核通过', 1, 7, '2026-03-14 14:45:31.000000', '经典版型，修身设计，优质面料，适合商务休闲场合穿着。', '[\"https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1617137968427-85924c800a22?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1593030761757-71fae45fa0e7?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop', '男士休闲商务西装外套', 599.00, 299.00, 4.8, NULL, 358, 1, '{\"颜色\": \"黑色,深蓝,灰色\", \"尺码\": \"M,L,XL,XXL\"}', 1, 147, '2026-03-15 16:50:51.821984');
INSERT INTO `t_product` VALUES (2, '审核通过', 1, 7, '2026-03-14 14:45:31.000000', '精选纯棉面料，透气舒适，修身版型，百搭时尚。', '[\"https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1598033129183-c4f50c736f10?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=400&h=400&fit=crop', '韩版修身男士衬衫', 159.00, 89.00, 4.7, NULL, 520, 1, '{\"颜色\": \"白色,浅蓝,粉色\", \"尺码\": \"M,L,XL\"}', 1, 280, '2026-03-14 14:45:31.000000');
INSERT INTO `t_product` VALUES (3, '审核通过', 1, 8, '2026-03-14 14:45:31.000000', '优雅气质连衣裙，显瘦修身，适合约会聚会穿着。', '[\"https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=400&h=400&fit=crop', '女装连衣裙夏季新款', 328.00, 168.00, 4.9, NULL, 428, 1, '{\"颜色\": \"黑色,红色,蓝色\", \"尺码\": \"S,M,L,XL\"}', 1, 199, '2026-03-15 21:41:21.343356');
INSERT INTO `t_product` VALUES (4, '审核通过', 1, 8, '2026-03-14 14:45:31.000000', '柔软舒适针织面料，百搭开衫设计，春秋必备单品。', '[\"https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1485968579169-a6b6661bb856?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=400&h=400&fit=crop', '时尚女装针织开衫', 238.00, 128.00, 4.6, NULL, 315, 1, '{\"颜色\": \"米色,灰色,粉色\", \"尺码\": \"S,M,L\"}', 1, 180, '2026-03-14 14:45:31.000000');
INSERT INTO `t_product` VALUES (5, '审核通过', 1, 10, '2026-03-14 14:45:31.000000', '最新旗舰处理器，超清摄像，大容量电池，5G全网通。', NULL, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop', '智能手机旗舰版 5G', 4999.00, 4999.00, 4.9, NULL, 89, 2, NULL, 1, 50, '2026-03-15 13:11:38.791972');
INSERT INTO `t_product` VALUES (6, '审核通过', 1, 10, '2026-03-14 14:45:31.000000', '高性价比之选，全面屏设计，拍照清晰，续航持久。', '[\"https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1512054502232-10a0a035d672?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=400&h=400&fit=crop', '智能手机青春版', 1999.00, 1599.00, 4.7, NULL, 156, 2, '{\"颜色\": \"黑色,蓝色,绿色\", \"存储\": \"64GB,128GB\"}', 1, 118, '2026-03-14 22:07:49.335283');
INSERT INTO `t_product` VALUES (7, '审核通过', 1, 11, '2026-03-14 14:45:31.000000', '超薄金属机身，高性能处理器，长续航，办公娱乐两相宜。', '[\"https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1531297484001-80022131f5a1?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop', '轻薄笔记本电脑', 6499.00, 5499.00, 4.8, NULL, 67, 2, '{\"颜色\": \"银色,深空灰\", \"配置\": \"i5/16GB/512GB,i7/16GB/512GB\"}', 1, 34, '2026-03-14 19:39:08.717981');
INSERT INTO `t_product` VALUES (8, '审核通过', 1, 12, '2026-03-14 14:45:31.000000', '主动降噪技术，高清音质，长续航，舒适佩戴。', '[\"https://images.unsplash.com/photo-1583394838336-acd977736f90?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1583394838336-acd977736f90?w=400&h=400&fit=crop', '无线蓝牙耳机降噪版', 599.00, 399.00, 4.6, NULL, 892, 2, '{\"颜色\": \"黑色,白色,蓝色\"}', 1, 200, '2026-03-14 14:45:31.000000');
INSERT INTO `t_product` VALUES (9, '审核通过', 1, 13, '2026-03-14 14:45:31.000000', '简约北欧设计，优质布艺面料，舒适坐感，易拆洗。', '[\"https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1506439773649-6e0eb8cfb237?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400&h=400&fit=crop', '北欧风格布艺沙发', 3999.00, 2599.00, 4.7, NULL, 45, 3, '{\"颜色\": \"灰色,米色,蓝色\", \"尺寸\": \"双人位,三人位\"}', 1, 20, '2026-03-14 14:45:31.000000');
INSERT INTO `t_product` VALUES (10, '审核通过', 1, 14, '2026-03-14 14:45:31.000000', '100%纯棉面料，亲肤透气，精致刺绣，舒适睡眠。', '[\"https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=400&h=400&fit=crop', '纯棉四件套床上用品', 499.00, 299.00, 4.8, NULL, 268, 3, '{\"颜色\": \"白色,粉色,蓝色,灰色\", \"规格\": \"1.5m床,1.8m床\"}', 1, 150, '2026-03-14 14:45:31.000000');
INSERT INTO `t_product` VALUES (11, '审核通过', 1, 15, '2026-03-14 14:45:31.000000', '德国工艺，优质不锈钢，锋利耐用，人体工学手柄。', '[\"https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1566454419290-57a0589c9b17?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400&h=400&fit=crop', '不锈钢厨房刀具套装', 359.00, 199.00, 4.6, NULL, 156, 3, '{\"套装\": \"5件套,7件套,9件套\"}', 1, 80, '2026-03-14 14:45:31.000000');
INSERT INTO `t_product` VALUES (12, '审核通过', 1, 4, '2026-03-14 14:45:31.000000', '轻量化设计，减震回弹，透气网面，适合长跑训练。', '[\"https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop', '专业跑步运动鞋', 699.00, 399.00, 4.8, NULL, 456, 4, '{\"颜色\": \"黑色,白色,红色,蓝色\", \"尺码\": \"39,40,41,42,43,44\"}', 1, 100, '2026-03-14 14:45:31.000000');
INSERT INTO `t_product` VALUES (13, '审核通过', 1, 4, '2026-03-14 14:45:31.000000', '防水面料，多功能分区，人体工学背负系统，适合户外旅行。', '[\"https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1622260614153-03223fb72052?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=400&fit=crop', '户外登山背包大容量', 399.00, 259.00, 4.7, NULL, 189, 4, '{\"容量\": \"40L,50L,60L\", \"颜色\": \"黑色,军绿,橙色\"}', 1, 60, '2026-03-14 14:45:31.000000');
INSERT INTO `t_product` VALUES (14, '审核通过', 1, 4, '2026-03-14 14:45:31.000000', '环保TPE材质，加厚设计，防滑纹理，舒适缓冲。', '[\"https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=400&h=400&fit=crop', '瑜伽垫加厚防滑', 159.00, 89.00, 4.5, NULL, 328, 4, '{\"颜色\": \"紫色,粉色,蓝色,绿色\", \"厚度\": \"6mm,8mm,10mm\"}', 1, 200, '2026-03-14 14:45:31.000000');
INSERT INTO `t_product` VALUES (15, '审核通过', 1, 5, '2026-03-14 14:45:31.000000', '精选全球优质坚果，营养健康，送礼佳品。', '[\"https://images.unsplash.com/photo-1599599810769-bcde5a160d32?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1604068549290-dea0e4a305ca?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1599599810769-bcde5a160d32?w=400&h=400&fit=crop', '进口坚果礼盒装', 198.00, 128.00, 4.9, NULL, 678, 5, '{\"规格\": \"500g装,1kg装\"}', 1, 300, '2026-03-14 14:45:31.000000');
INSERT INTO `t_product` VALUES (16, '审核通过', 1, 5, '2026-03-14 14:45:31.000000', '高山有机茶园，手工采摘，清香回甘，健康养生。', '[\"https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400&h=400&fit=crop', '有机绿茶礼盒', 268.00, 168.00, 4.8, NULL, 423, 5, '{\"规格\": \"250g装,500g装\"}', 1, 150, '2026-03-14 14:45:31.000000');
INSERT INTO `t_product` VALUES (17, '审核通过', 1, 5, '2026-03-14 14:45:31.000000', '纯手工制作，进口黄油，多种口味，酥脆可口。', '[\"https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=800&h=800&fit=crop\",\"https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=800&h=800&fit=crop\"]', 'https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=400&h=400&fit=crop', '手工曲奇饼干礼盒', 98.00, 68.00, 4.7, NULL, 892, 5, '{\"规格\": \"300g装,500g装\"}', 1, 400, '2026-03-14 14:45:31.000000');

-- ----------------------------
-- Table structure for t_refund
-- ----------------------------
DROP TABLE IF EXISTS `t_refund`;
CREATE TABLE `t_refund`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(10, 2) NOT NULL,
  `create_time` datetime(6) NOT NULL,
  `handle_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `handle_time` datetime(6) NULL DEFAULT NULL,
  `order_id` bigint NOT NULL,
  `order_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` tinyint NOT NULL DEFAULT 0,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_refund
-- ----------------------------
INSERT INTO `t_refund` VALUES (1, 256.00, '2026-03-14 14:45:31.000000', '同意退款', '2026-03-14 14:45:31.000000', 6, '2026031300006', '不想要了', 1, 5);
INSERT INTO `t_refund` VALUES (2, 1599.00, '2026-03-14 22:05:24.191201', '', '2026-03-14 22:06:19.257316', 8, 'ORD1773484110016660F4F43', '不想要了', 1, 6);
INSERT INTO `t_refund` VALUES (3, 1599.00, '2026-03-14 22:09:58.720529', '', '2026-03-15 13:04:24.382001', 10, 'ORD17734972693224D5B63A8', '1111', 1, 6);
INSERT INTO `t_refund` VALUES (4, 5499.00, '2026-03-15 13:02:17.227535', '', '2026-03-15 13:04:21.113593', 9, 'ORD1773488348614B239D0A8', '不想要了', 1, 6);

-- ----------------------------
-- Table structure for t_review
-- ----------------------------
DROP TABLE IF EXISTS `t_review`;
CREATE TABLE `t_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `create_time` datetime(6) NOT NULL,
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `rating` int NOT NULL,
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `user_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_review
-- ----------------------------
INSERT INTO `t_review` VALUES (1, '质量很好，版型不错，穿着很合身，面料也很舒服，下次还会再来购买！', '2026-03-14 14:45:31.000000', '[\"https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&h=600&fit=crop\",\"https://images.unsplash.com/photo-1617137968427-85924c800a22?w=800&h=600&fit=crop\"]', 1, 1, 5, '感谢您的支持与认可，我们会继续努力提供更好的产品和服务！', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200&h=200&fit=crop', 1, 'zhangsan');
INSERT INTO `t_review` VALUES (2, '鞋子很轻便，跑起来很舒服，就是尺码稍微偏大一点，建议买小一码。', '2026-03-14 14:45:31.000000', '[\"https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&h=600&fit=crop\"]', 5, 16, 4, '感谢您的反馈，我们会改进尺码标注，期待您的再次光临！', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&h=200&fit=crop', 4, 'zhaoliu');
INSERT INTO `t_review` VALUES (3, '很好', '2026-03-15 17:08:15.200999', '', 13, 1, 5, NULL, NULL, 6, NULL);

-- ----------------------------
-- Table structure for t_shop
-- ----------------------------
DROP TABLE IF EXISTS `t_shop`;
CREATE TABLE `t_shop`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `audit_status` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime(6) NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `merchant_id` bigint NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `rating` decimal(3, 1) NULL DEFAULT 5.0,
  `sales_count` int NULL DEFAULT 0,
  `status` tinyint NOT NULL DEFAULT 1,
  `update_time` datetime(6) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_shop
-- ----------------------------
INSERT INTO `t_shop` VALUES (1, '浙江省杭州市西湖区文三路123号', '审核通过', 1, '2026-03-14 14:45:31.000000', '专注时尚潮流服饰，提供最新款的男女服装，品质保证，价格实惠。', 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=200&h=200&fit=crop', 1, '时尚潮流服饰店', '13900139001', 4.8, 1580, 1, '2026-03-14 14:45:31.000000');
INSERT INTO `t_shop` VALUES (2, '广东省深圳市南山区科技园路456号', '审核通过', 1, '2026-03-14 14:45:31.000000', '专业数码产品销售，正品保障，全国联保，售后无忧。', 'https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=200&h=200&fit=crop', 2, '数码科技专营店', '13900139002', 4.9, 2350, 1, '2026-03-15 13:12:02.185978');
INSERT INTO `t_shop` VALUES (3, '上海市浦东新区张江高科技园区789号', '审核通过', 1, '2026-03-14 14:45:31.000000', '精选家居好物，打造温馨舒适的家，品质生活从这里开始。', 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=200&h=200&fit=crop', 3, '家居生活馆', '13900139003', 4.7, 890, 1, '2026-03-14 14:45:31.000000');
INSERT INTO `t_shop` VALUES (4, '北京市朝阳区建国路101号', '审核通过', 1, '2026-03-14 14:45:31.000000', '专业运动户外装备，助您挑战极限，享受运动乐趣。', 'https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=200&h=200&fit=crop', 4, '运动户外专营', '13900139004', 4.6, 1200, 1, '2026-03-14 14:45:31.000000');
INSERT INTO `t_shop` VALUES (5, '四川省成都市武侯区天府大道666号', '审核通过', 1, '2026-03-14 14:45:31.000000', '精选各地美食零食，满足您的味蕾，美味送到家。', 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=200&h=200&fit=crop', 5, '美食零食铺', '13900139005', 4.5, 3200, 1, '2026-03-14 14:45:31.000000');
INSERT INTO `t_shop` VALUES (11, '28876586758@qq.com', NULL, 1, '2026-03-15 16:03:36.866063', 'YangSH', NULL, 11, 'YangSH', '19978665473', 5.0, 0, 1, '2026-03-15 16:08:03.837739');
INSERT INTO `t_shop` VALUES (12, '', NULL, 1, '2026-03-15 23:07:59.382108', NULL, NULL, 12, 'YangShuiHui', '18875896746', 5.0, 0, 1, '2026-03-15 23:08:14.972644');

-- ----------------------------
-- Table structure for t_shop_follow
-- ----------------------------
DROP TABLE IF EXISTS `t_shop_follow`;
CREATE TABLE `t_shop_follow`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `shop_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_shop_follow
-- ----------------------------
INSERT INTO `t_shop_follow` VALUES (2, '2026-03-15 18:33:22.142207', 1, 6);

-- ----------------------------
-- Table structure for t_system_settings
-- ----------------------------
DROP TABLE IF EXISTS `t_system_settings`;
CREATE TABLE `t_system_settings`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_lock_threshold` int NULL DEFAULT NULL,
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `default_shipping` double NULL DEFAULT NULL,
  `free_shipping_amount` double NULL DEFAULT NULL,
  `merchant_audit_required` bit(1) NULL DEFAULT NULL,
  `merchant_deposit` double NULL DEFAULT NULL,
  `new_user_coupon` bit(1) NULL DEFAULT NULL,
  `new_user_coupon_amount` double NULL DEFAULT NULL,
  `order_timeout` int NULL DEFAULT NULL,
  `platform_commission` double NULL DEFAULT NULL,
  `platform_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `platform_logo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `platform_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `refund_days` int NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_system_settings
-- ----------------------------
INSERT INTO `t_system_settings` VALUES (1, 5, 'support@example.com', '400-123-4567', '2026-03-15 23:18:38.348776', 10, 100, b'1', 1000, b'1', 10, 30, 5, '一个功能完整的电商平台', '', '好物购买系统', 7, '2026-03-15 23:18:46.656370');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime(6) NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `last_login_time` datetime(6) NULL DEFAULT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `update_time` datetime(6) NOT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_jhib4legehrm4yscx9t3lirqi`(`username` ASC) USING BTREE,
  UNIQUE INDEX `UK_i6qjjoe560mee5ajdg7v1o6mi`(`email` ASC) USING BTREE,
  UNIQUE INDEX `UK_m5bu5erj83eubjsa1nyms0t89`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200&h=200&fit=crop', '2026-03-14 14:45:31.000000', 'zhangsan@example.com', '2026-03-14 14:45:31.000000', '$2a$10$CvV.v4gMJAHDQ/dt9jkVU.CwsoJEy4Xb4cIYQHtPqChNOrePc7jzW', '13800138001', 1, '2026-03-14 14:45:31.000000', 'zhangsan');
INSERT INTO `t_user` VALUES (2, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&h=200&fit=crop', '2026-03-14 14:45:31.000000', 'lisi@example.com', '2026-03-14 14:45:31.000000', '$2a$10$CvV.v4gMJAHDQ/dt9jkVU.CwsoJEy4Xb4cIYQHtPqChNOrePc7jzW', '13800138002', 1, '2026-03-14 14:45:31.000000', 'lisi');
INSERT INTO `t_user` VALUES (3, 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&h=200&fit=crop', '2026-03-14 14:45:31.000000', 'wangwu@example.com', '2026-03-14 14:45:31.000000', '$2a$10$CvV.v4gMJAHDQ/dt9jkVU.CwsoJEy4Xb4cIYQHtPqChNOrePc7jzW', '13800138003', 1, '2026-03-14 14:45:31.000000', 'wangwu');
INSERT INTO `t_user` VALUES (4, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&h=200&fit=crop', '2026-03-14 14:45:31.000000', 'zhaoliu@example.com', '2026-03-14 14:45:31.000000', '$2a$10$CvV.v4gMJAHDQ/dt9jkVU.CwsoJEy4Xb4cIYQHtPqChNOrePc7jzW', '13800138004', 1, '2026-03-14 14:45:31.000000', 'zhaoliu');
INSERT INTO `t_user` VALUES (5, 'https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=200&h=200&fit=crop', '2026-03-14 14:45:31.000000', 'sunqi@example.com', NULL, '$2a$10$CvV.v4gMJAHDQ/dt9jkVU.CwsoJEy4Xb4cIYQHtPqChNOrePc7jzW', '13800138005', 1, '2026-03-15 14:01:56.632124', 'sunqi');
INSERT INTO `t_user` VALUES (6, '/api/files/avatar/5d649e36-c6ef-4ef1-bd72-9fedbbbba32a.jpg', '2026-03-14 14:54:37.790811', 'YangSH@qq,com', '2026-03-15 21:36:47.584418', '$2a$10$9.fMsbdm3WJxCgsLXcLN3Os1ARuqkANPGik6J4pCdsQdS5JmgJ.Qu', '19958735693', 1, '2026-03-15 21:36:47.589008', 'yangsh');
INSERT INTO `t_user` VALUES (7, '/api/files/avatar/25a99f45-732a-4889-baca-669a90d4a996.jpg', '2026-03-15 23:06:51.203742', '', '2026-03-15 23:07:11.585307', '$2a$10$.5Z6hDoAQuCptMATw4z7yOcTlAQyp6.pyFx9YWhFC4uN7jUn55Kai', '19987465378', 1, '2026-03-15 23:07:24.028710', 'YangShuHui');

-- ----------------------------
-- Table structure for t_user_coupon
-- ----------------------------
DROP TABLE IF EXISTS `t_user_coupon`;
CREATE TABLE `t_user_coupon`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coupon_id` bigint NOT NULL,
  `receive_time` datetime(6) NOT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `use_time` datetime(6) NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_coupon
-- ----------------------------
INSERT INTO `t_user_coupon` VALUES (1, 1, '2026-03-14 14:45:31.000000', 1, NULL, 1);
INSERT INTO `t_user_coupon` VALUES (2, 2, '2026-03-14 14:45:31.000000', 2, '2026-03-14 14:45:31.000000', 1);
INSERT INTO `t_user_coupon` VALUES (3, 4, '2026-03-14 14:45:31.000000', 1, NULL, 1);
INSERT INTO `t_user_coupon` VALUES (4, 1, '2026-03-14 14:45:31.000000', 1, NULL, 2);
INSERT INTO `t_user_coupon` VALUES (5, 3, '2026-03-14 14:45:31.000000', 1, NULL, 2);
INSERT INTO `t_user_coupon` VALUES (6, 2, '2026-03-14 14:45:31.000000', 1, NULL, 3);
INSERT INTO `t_user_coupon` VALUES (7, 5, '2026-03-14 14:45:31.000000', 1, NULL, 3);
INSERT INTO `t_user_coupon` VALUES (8, 1, '2026-03-14 14:45:31.000000', 1, NULL, 4);
INSERT INTO `t_user_coupon` VALUES (9, 7, '2026-03-14 14:45:31.000000', 1, NULL, 4);
INSERT INTO `t_user_coupon` VALUES (10, 1, '2026-03-14 14:45:31.000000', 1, NULL, 5);
INSERT INTO `t_user_coupon` VALUES (11, 8, '2026-03-14 14:45:31.000000', 1, NULL, 5);
INSERT INTO `t_user_coupon` VALUES (12, 1, '2026-03-14 16:04:36.586813', 1, NULL, 6);
INSERT INTO `t_user_coupon` VALUES (13, 2, '2026-03-14 16:04:53.888055', 1, NULL, 6);
INSERT INTO `t_user_coupon` VALUES (14, 3, '2026-03-14 16:04:54.720817', 2, NULL, 6);
INSERT INTO `t_user_coupon` VALUES (15, 9, '2026-03-15 16:26:44.801122', 1, NULL, 6);
INSERT INTO `t_user_coupon` VALUES (16, 4, '2026-03-15 21:41:01.245233', 2, NULL, 6);

SET FOREIGN_KEY_CHECKS = 1;
