-- ============================================
-- 好物购买系统 测试数据SQL脚本
-- 数据库：MySQL 8.0
-- 字符集：utf8mb4
-- 生成时间：2026-03-13
-- ============================================

-- 清空所有表数据（按外键依赖顺序删除）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE t_user_coupon;
TRUNCATE TABLE t_coupon;
TRUNCATE TABLE t_refund;
TRUNCATE TABLE t_review;
TRUNCATE TABLE t_order_item;
TRUNCATE TABLE t_order;
TRUNCATE TABLE t_cart;
TRUNCATE TABLE t_favorite;
TRUNCATE TABLE t_address;
TRUNCATE TABLE t_product;
TRUNCATE TABLE t_category;
TRUNCATE TABLE t_shop;
TRUNCATE TABLE t_merchant;
TRUNCATE TABLE t_user;
TRUNCATE TABLE t_admin;
TRUNCATE TABLE t_banner;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 一、管理员数据
-- 密码均为：admin123（BCrypt加密）
-- ============================================
INSERT INTO t_admin (username, password, role, status, create_time, last_login_time) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'super_admin', 1, NOW(), NOW()),
('operator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin', 1, NOW(), NULL);

-- ============================================
-- 二、用户数据
-- 密码均为：123456（BCrypt加密）
-- ============================================
INSERT INTO t_user (username, password, phone, email, avatar, status, create_time, update_time, last_login_time) VALUES
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138001', 'zhangsan@example.com', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200&h=200&fit=crop', 1, NOW(), NOW(), NOW()),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138002', 'lisi@example.com', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&h=200&fit=crop', 1, NOW(), NOW(), NOW()),
('wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138003', 'wangwu@example.com', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&h=200&fit=crop', 1, NOW(), NOW(), NOW()),
('zhaoliu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138004', 'zhaoliu@example.com', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&h=200&fit=crop', 1, NOW(), NOW(), NOW()),
('sunqi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138005', 'sunqi@example.com', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=200&h=200&fit=crop', 1, NOW(), NOW(), NULL);

-- ============================================
-- 三、商家数据
-- 密码均为：123456（BCrypt加密）
-- ============================================
INSERT INTO t_merchant (username, password, shop_id, shop_name, phone, email, status, create_time, update_time) VALUES
('merchant001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', NULL, '时尚潮流服饰店', '13900139001', 'merchant001@shop.com', 1, NOW(), NOW()),
('merchant002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', NULL, '数码科技专营店', '13900139002', 'merchant002@shop.com', 1, NOW(), NOW()),
('merchant003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', NULL, '家居生活馆', '13900139003', 'merchant003@shop.com', 1, NOW(), NOW()),
('merchant004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', NULL, '运动户外专营', '13900139004', 'merchant004@shop.com', 1, NOW(), NOW()),
('merchant005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', NULL, '美食零食铺', '13900139005', 'merchant005@shop.com', 1, NOW(), NOW());

-- ============================================
-- 四、店铺数据
-- ============================================
INSERT INTO t_shop (merchant_id, name, logo, description, address, phone, status, audit_status, audit_remark, rating, sales_count, create_time, update_time) VALUES
(1, '时尚潮流服饰店', 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=200&h=200&fit=crop', '专注时尚潮流服饰，提供最新款的男女服装，品质保证，价格实惠。', '浙江省杭州市西湖区文三路123号', '13900139001', 1, 1, '审核通过', 4.8, 1580, NOW(), NOW()),
(2, '数码科技专营店', 'https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=200&h=200&fit=crop', '专业数码产品销售，正品保障，全国联保，售后无忧。', '广东省深圳市南山区科技园路456号', '13900139002', 1, 1, '审核通过', 4.9, 2350, NOW(), NOW()),
(3, '家居生活馆', 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=200&h=200&fit=crop', '精选家居好物，打造温馨舒适的家，品质生活从这里开始。', '上海市浦东新区张江高科技园区789号', '13900139003', 1, 1, '审核通过', 4.7, 890, NOW(), NOW()),
(4, '运动户外专营', 'https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=200&h=200&fit=crop', '专业运动户外装备，助您挑战极限，享受运动乐趣。', '北京市朝阳区建国路101号', '13900139004', 1, 1, '审核通过', 4.6, 1200, NOW(), NOW()),
(5, '美食零食铺', 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=200&h=200&fit=crop', '精选各地美食零食，满足您的味蕾，美味送到家。', '四川省成都市武侯区天府大道666号', '13900139005', 1, 1, '审核通过', 4.5, 3200, NOW(), NOW());

-- 更新商家的shop_id和shop_name
UPDATE t_merchant SET shop_id = 1, shop_name = '时尚潮流服饰店' WHERE id = 1;
UPDATE t_merchant SET shop_id = 2, shop_name = '数码科技专营店' WHERE id = 2;
UPDATE t_merchant SET shop_id = 3, shop_name = '家居生活馆' WHERE id = 3;
UPDATE t_merchant SET shop_id = 4, shop_name = '运动户外专营' WHERE id = 4;
UPDATE t_merchant SET shop_id = 5, shop_name = '美食零食铺' WHERE id = 5;

-- ============================================
-- 五、商品分类数据
-- ============================================
INSERT INTO t_category (name, icon, parent_id, sort, status) VALUES
-- 一级分类
('服装', 'https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=100&h=100&fit=crop', 0, 1, 1),
('数码电子', 'https://images.unsplash.com/photo-1498049794561-7780e7231661?w=100&h=100&fit=crop', 0, 2, 1),
('家居生活', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=100&h=100&fit=crop', 0, 3, 1),
('运动户外', 'https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=100&h=100&fit=crop', 0, 4, 1),
('食品饮料', 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=100&h=100&fit=crop', 0, 5, 1),
('美妆护肤', 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=100&h=100&fit=crop', 0, 6, 1),
-- 二级分类（服装）
('男装', 'https://images.unsplash.com/photo-1617137968427-85924c800a22?w=100&h=100&fit=crop', 1, 1, 1),
('女装', 'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=100&h=100&fit=crop', 1, 2, 1),
('童装', 'https://images.unsplash.com/photo-1519238263530-99bdd11df2ea?w=100&h=100&fit=crop', 1, 3, 1),
-- 二级分类（数码电子）
('手机', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=100&h=100&fit=crop', 2, 1, 1),
('电脑', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=100&h=100&fit=crop', 2, 2, 1),
('配件', 'https://images.unsplash.com/photo-1583394838336-acd977736f90?w=100&h=100&fit=crop', 2, 3, 1),
-- 二级分类（家居生活）
('家具', 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=100&h=100&fit=crop', 3, 1, 1),
('家纺', 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=100&h=100&fit=crop', 3, 2, 1),
('厨具', 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=100&h=100&fit=crop', 3, 3, 1);

-- ============================================
-- 六、商品数据
-- ============================================
INSERT INTO t_product (shop_id, category_id, name, description, price, original_price, stock, main_image, images, specs, status, audit_status, audit_remark, sales_count, rating, create_time, update_time) VALUES
-- 服装类商品（店铺1）
(1, 7, '男士休闲商务西装外套', '经典版型，修身设计，优质面料，适合商务休闲场合穿着。', 299.00, 599.00, 150, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1617137968427-85924c800a22?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1593030761757-71fae45fa0e7?w=800&h=800&fit=crop"]', '{"颜色": "黑色,深蓝,灰色", "尺码": "M,L,XL,XXL"}', 1, 1, '审核通过', 358, 4.8, NOW(), NOW()),
(1, 7, '韩版修身男士衬衫', '精选纯棉面料，透气舒适，修身版型，百搭时尚。', 89.00, 159.00, 280, 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1598033129183-c4f50c736f10?w=800&h=800&fit=crop"]', '{"颜色": "白色,浅蓝,粉色", "尺码": "M,L,XL"}', 1, 1, '审核通过', 520, 4.7, NOW(), NOW()),
(1, 8, '女装连衣裙夏季新款', '优雅气质连衣裙，显瘦修身，适合约会聚会穿着。', 168.00, 328.00, 200, 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=800&h=800&fit=crop"]', '{"颜色": "黑色,红色,蓝色", "尺码": "S,M,L,XL"}', 1, 1, '审核通过', 428, 4.9, NOW(), NOW()),
(1, 8, '时尚女装针织开衫', '柔软舒适针织面料，百搭开衫设计，春秋必备单品。', 128.00, 238.00, 180, 'https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1485968579169-a6b6661bb856?w=800&h=800&fit=crop"]', '{"颜色": "米色,灰色,粉色", "尺码": "S,M,L"}', 1, 1, '审核通过', 315, 4.6, NOW(), NOW()),

-- 数码电子类商品（店铺2）
(2, 10, '智能手机旗舰版 5G', '最新旗舰处理器，超清摄像，大容量电池，5G全网通。', 3999.00, 4999.00, 50, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1580910051074-3eb694886f5b?w=800&h=800&fit=crop"]', '{"颜色": "星空黑,极光蓝,珍珠白", "存储": "128GB,256GB,512GB"}', 1, 1, '审核通过', 89, 4.9, NOW(), NOW()),
(2, 10, '智能手机青春版', '高性价比之选，全面屏设计，拍照清晰，续航持久。', 1599.00, 1999.00, 120, 'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1512054502232-10a0a035d672?w=800&h=800&fit=crop"]', '{"颜色": "黑色,蓝色,绿色", "存储": "64GB,128GB"}', 1, 1, '审核通过', 156, 4.7, NOW(), NOW()),
(2, 11, '轻薄笔记本电脑', '超薄金属机身，高性能处理器，长续航，办公娱乐两相宜。', 5499.00, 6499.00, 35, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1531297484001-80022131f5a1?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=800&h=800&fit=crop"]', '{"颜色": "银色,深空灰", "配置": "i5/16GB/512GB,i7/16GB/512GB"}', 1, 1, '审核通过', 67, 4.8, NOW(), NOW()),
(2, 12, '无线蓝牙耳机降噪版', '主动降噪技术，高清音质，长续航，舒适佩戴。', 399.00, 599.00, 200, 'https://images.unsplash.com/photo-1583394838336-acd977736f90?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1583394838336-acd977736f90?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&h=800&fit=crop"]', '{"颜色": "黑色,白色,蓝色"}', 1, 1, '审核通过', 892, 4.6, NOW(), NOW()),

-- 家居生活类商品（店铺3）
(3, 13, '北欧风格布艺沙发', '简约北欧设计，优质布艺面料，舒适坐感，易拆洗。', 2599.00, 3999.00, 20, 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1506439773649-6e0eb8cfb237?w=800&h=800&fit=crop"]', '{"颜色": "灰色,米色,蓝色", "尺寸": "双人位,三人位"}', 1, 1, '审核通过', 45, 4.7, NOW(), NOW()),
(3, 14, '纯棉四件套床上用品', '100%纯棉面料，亲肤透气，精致刺绣，舒适睡眠。', 299.00, 499.00, 150, 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?w=800&h=800&fit=crop"]', '{"颜色": "白色,粉色,蓝色,灰色", "规格": "1.5m床,1.8m床"}', 1, 1, '审核通过', 268, 4.8, NOW(), NOW()),
(3, 15, '不锈钢厨房刀具套装', '德国工艺，优质不锈钢，锋利耐用，人体工学手柄。', 199.00, 359.00, 80, 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1566454419290-57a0589c9b17?w=800&h=800&fit=crop"]', '{"套装": "5件套,7件套,9件套"}', 1, 1, '审核通过', 156, 4.6, NOW(), NOW()),

-- 运动户外类商品（店铺4）
(4, 4, '专业跑步运动鞋', '轻量化设计，减震回弹，透气网面，适合长跑训练。', 399.00, 699.00, 100, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800&h=800&fit=crop"]', '{"颜色": "黑色,白色,红色,蓝色", "尺码": "39,40,41,42,43,44"}', 1, 1, '审核通过', 456, 4.8, NOW(), NOW()),
(4, 4, '户外登山背包大容量', '防水面料，多功能分区，人体工学背负系统，适合户外旅行。', 259.00, 399.00, 60, 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1622260614153-03223fb72052?w=800&h=800&fit=crop"]', '{"容量": "40L,50L,60L", "颜色": "黑色,军绿,橙色"}', 1, 1, '审核通过', 189, 4.7, NOW(), NOW()),
(4, 4, '瑜伽垫加厚防滑', '环保TPE材质，加厚设计，防滑纹理，舒适缓冲。', 89.00, 159.00, 200, 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=800&h=800&fit=crop"]', '{"颜色": "紫色,粉色,蓝色,绿色", "厚度": "6mm,8mm,10mm"}', 1, 1, '审核通过', 328, 4.5, NOW(), NOW()),

-- 食品饮料类商品（店铺5）
(5, 5, '进口坚果礼盒装', '精选全球优质坚果，营养健康，送礼佳品。', 128.00, 198.00, 300, 'https://images.unsplash.com/photo-1599599810769-bcde5a160d32?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1599599810769-bcde5a160d32?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1604068549290-dea0e4a305ca?w=800&h=800&fit=crop"]', '{"规格": "500g装,1kg装"}', 1, 1, '审核通过', 678, 4.9, NOW(), NOW()),
(5, 5, '有机绿茶礼盒', '高山有机茶园，手工采摘，清香回甘，健康养生。', 168.00, 268.00, 150, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=800&h=800&fit=crop"]', '{"规格": "250g装,500g装"}', 1, 1, '审核通过', 423, 4.8, NOW(), NOW()),
(5, 5, '手工曲奇饼干礼盒', '纯手工制作，进口黄油，多种口味，酥脆可口。', 68.00, 98.00, 400, 'https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=400&h=400&fit=crop', '["https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=800&h=800&fit=crop","https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=800&h=800&fit=crop"]', '{"规格": "300g装,500g装"}', 1, 1, '审核通过', 892, 4.7, NOW(), NOW());

-- ============================================
-- 七、收货地址数据
-- ============================================
INSERT INTO t_address (user_id, receiver, phone, province, city, district, detail, is_default, create_time) VALUES
(1, '张三', '13800138001', '浙江省', '杭州市', '西湖区', '文三路123号阳光小区1栋101室', 1, NOW()),
(1, '张三', '13800138001', '浙江省', '杭州市', '滨江区', '滨盛路456号创新大厦A座1502', 0, NOW()),
(2, '李四', '13800138002', '广东省', '深圳市', '南山区', '科技园路789号腾讯大厦', 1, NOW()),
(3, '王五', '13800138003', '上海市', '上海市', '浦东新区', '张江高科技园区博云路2号', 1, NOW()),
(4, '赵六', '13800138004', '北京市', '北京市', '朝阳区', '建国路101号国贸中心', 1, NOW()),
(5, '孙七', '13800138005', '四川省', '成都市', '武侯区', '天府大道666号天府软件园', 1, NOW());

-- ============================================
-- 八、购物车数据
-- ============================================
INSERT INTO t_cart (user_id, product_id, shop_id, quantity, spec, checked, create_time, update_time) VALUES
(1, 1, 1, 1, '黑色,L', 1, NOW(), NOW()),
(1, 5, 2, 1, '星空黑,256GB', 1, NOW(), NOW()),
(2, 3, 1, 2, '红色,M', 1, NOW(), NOW()),
(2, 13, 3, 1, '灰色,三人位', 1, NOW(), NOW()),
(3, 14, 3, 1, '蓝色,1.8m床', 1, NOW(), NOW()),
(4, 16, 4, 1, '黑色,42', 1, NOW(), NOW()),
(5, 19, 5, 2, '500g装', 1, NOW(), NOW());

-- ============================================
-- 九、订单数据
-- ============================================
INSERT INTO t_order (order_no, user_id, shop_id, total_amount, pay_amount, pay_method, status, receiver_name, receiver_phone, receiver_address, remark, express_company, express_no, pay_time, send_time, confirm_time, cancel_time, create_time, update_time) VALUES
('2026031300001', 1, 1, 598.00, 299.00, 'alipay', 3, '张三', '13800138001', '浙江省杭州市西湖区文三路123号阳光小区1栋101室', '请尽快发货', '顺丰速运', 'SF1234567890', NOW(), NOW(), NOW(), NULL, NOW(), NOW()),
('2026031300002', 1, 2, 3999.00, 3999.00, 'wechat', 2, '张三', '13800138001', '浙江省杭州市西湖区文三路123号阳光小区1栋101室', '', '圆通快递', 'YT9876543210', NOW(), NOW(), NULL, NULL, NOW(), NOW()),
('2026031300003', 2, 1, 336.00, 336.00, 'alipay', 1, '李四', '13800138002', '广东省深圳市南山区科技园路789号腾讯大厦', '周末送货', NULL, NULL, NOW(), NULL, NULL, NULL, NOW(), NOW()),
('2026031300004', 3, 3, 299.00, 299.00, 'wechat', 0, '王五', '13800138003', '上海市浦东新区张江高科技园区博云路2号', '', NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
('2026031300005', 4, 4, 399.00, 399.00, 'alipay', 3, '赵六', '13800138004', '北京市朝阳区建国路101号国贸中心', '包装完好', '中通快递', 'ZT1122334455', NOW(), NOW(), NOW(), NULL, NOW(), NOW()),
('2026031300006', 5, 5, 256.00, 256.00, 'wechat', 4, '孙七', '13800138005', '四川省成都市武侯区天府大道666号天府软件园', '', NULL, NULL, NULL, NULL, NULL, NOW(), NOW(), NOW());

-- ============================================
-- 十、订单商品数据
-- ============================================
INSERT INTO t_order_item (order_id, order_no, product_id, product_name, product_image, price, quantity, spec, reviewed) VALUES
(1, '2026031300001', 1, '男士休闲商务西装外套', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop', 299.00, 1, '黑色,L', 1),
(2, '2026031300002', 5, '智能手机旗舰版 5G', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop', 3999.00, 1, '星空黑,256GB', 0),
(3, '2026031300003', 3, '女装连衣裙夏季新款', 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=400&h=400&fit=crop', 168.00, 2, '红色,M', 0),
(4, '2026031300004', 14, '纯棉四件套床上用品', 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=400&h=400&fit=crop', 299.00, 1, '蓝色,1.8m床', 0),
(5, '2026031300005', 16, '专业跑步运动鞋', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop', 399.00, 1, '黑色,42', 1),
(6, '2026031300006', 19, '进口坚果礼盒装', 'https://images.unsplash.com/photo-1599599810769-bcde5a160d32?w=400&h=400&fit=crop', 128.00, 2, '500g装', 0);

-- ============================================
-- 十一、商品评价数据
-- ============================================
INSERT INTO t_review (order_id, product_id, user_id, user_name, user_avatar, rating, content, images, reply, create_time) VALUES
(1, 1, 1, 'zhangsan', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200&h=200&fit=crop', 5, '质量很好，版型不错，穿着很合身，面料也很舒服，下次还会再来购买！', '["https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&h=600&fit=crop","https://images.unsplash.com/photo-1617137968427-85924c800a22?w=800&h=600&fit=crop"]', '感谢您的支持与认可，我们会继续努力提供更好的产品和服务！', NOW()),
(5, 16, 4, 'zhaoliu', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&h=200&fit=crop', 4, '鞋子很轻便，跑起来很舒服，就是尺码稍微偏大一点，建议买小一码。', '["https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&h=600&fit=crop"]', '感谢您的反馈，我们会改进尺码标注，期待您的再次光临！', NOW());

-- ============================================
-- 十二、收藏数据
-- ============================================
INSERT INTO t_favorite (user_id, product_id, create_time) VALUES
(1, 3, NOW()),
(1, 5, NOW()),
(1, 14, NOW()),
(2, 1, NOW()),
(2, 16, NOW()),
(3, 5, NOW()),
(3, 19, NOW()),
(4, 3, NOW()),
(4, 13, NOW()),
(5, 1, NOW());

-- ============================================
-- 十三、优惠券数据
-- ============================================
INSERT INTO t_coupon (name, type, shop_id, value, min_amount, start_time, end_time, total_count, remaining_count, status, create_time, update_time) VALUES
-- 平台优惠券
('新人专享优惠券', 1, NULL, 20.00, 100.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1000, 850, 1, NOW(), NOW()),
('全场通用满减券', 1, NULL, 50.00, 300.00, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 500, 420, 1, NOW(), NOW()),
('限时特惠优惠券', 1, NULL, 100.00, 500.00, NOW(), DATE_ADD(NOW(), INTERVAL 15 DAY), 200, 180, 1, NOW(), NOW()),
-- 店铺优惠券
('服装店专属优惠券', 2, 1, 30.00, 200.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 300, 250, 1, NOW(), NOW()),
('数码店专属优惠券', 2, 2, 200.00, 2000.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 100, 85, 1, NOW(), NOW()),
('家居店专属优惠券', 2, 3, 50.00, 500.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 200, 160, 1, NOW(), NOW()),
('运动店专属优惠券', 2, 4, 40.00, 300.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 150, 120, 1, NOW(), NOW()),
('美食店专属优惠券', 2, 5, 15.00, 80.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 400, 350, 1, NOW(), NOW());

-- ============================================
-- 十四、用户优惠券数据
-- ============================================
INSERT INTO t_user_coupon (user_id, coupon_id, status, receive_time, use_time) VALUES
(1, 1, 1, NOW(), NULL),
(1, 2, 2, NOW(), NOW()),
(1, 4, 1, NOW(), NULL),
(2, 1, 1, NOW(), NULL),
(2, 3, 1, NOW(), NULL),
(3, 2, 1, NOW(), NULL),
(3, 5, 1, NOW(), NULL),
(4, 1, 1, NOW(), NULL),
(4, 7, 1, NOW(), NULL),
(5, 1, 1, NOW(), NULL),
(5, 8, 1, NOW(), NULL);

-- ============================================
-- 十五、轮播图数据
-- ============================================
INSERT INTO t_banner (title, subtitle, image_url, link_url, sort, status, create_time, update_time) VALUES
('春季新品上市', '全场5折起，限时抢购', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?w=1200&h=400&fit=crop', '/pages/product/list?category=1', 1, 1, NOW(), NOW()),
('数码狂欢节', '爆款手机低至1999元', 'https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=1200&h=400&fit=crop', '/pages/product/list?category=2', 2, 1, NOW(), NOW()),
('家居生活节', '品质家居，温馨生活', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=1200&h=400&fit=crop', '/pages/product/list?category=3', 3, 1, NOW(), NOW()),
('运动户外季', '运动装备全场8折', 'https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=1200&h=400&fit=crop', '/pages/product/list?category=4', 4, 1, NOW(), NOW()),
('美食嘉年华', '进口零食满99减20', 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=1200&h=400&fit=crop', '/pages/product/list?category=5', 5, 1, NOW(), NOW());

-- ============================================
-- 十六、退款申请数据
-- ============================================
INSERT INTO t_refund (order_id, order_no, user_id, amount, reason, status, handle_remark, create_time, handle_time) VALUES
(6, '2026031300006', 5, 256.00, '不想要了', 1, '同意退款', NOW(), NOW());

-- ============================================
-- 数据插入完成
-- ============================================
