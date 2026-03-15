# 好物购买系统 API 接口文档

> 基础URL：`http://localhost:8080/api`
> 请求头：`Content-Type: application/json`
> 认证方式：JWT Token，在请求头中携带 `Authorization: Bearer <token>`

---

## 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 200=成功，400=参数错误，401=未登录，403=无权限，500=服务器错误 |
| message | String | 响应消息 |
| data | Object | 响应数据 |

---

## 一、用户端接口

### 1.1 用户注册

- **POST** `/user/register`

**请求体：**
```json
{
  "username": "string",
  "password": "string",
  "phone": "string",
  "email": "string",
  "captcha": "string"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "注册成功",
  "data": { "userId": 1 }
}
```

---

### 1.2 用户登录

- **POST** `/user/login`

**请求体：**
```json
{
  "username": "string",
  "password": "string"
}
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGci...",
    "userInfo": {
      "id": 1,
      "username": "张三",
      "avatar": "https://...",
      "phone": "138****1234",
      "email": "xxx@example.com"
    }
  }
}
```

---

### 1.3 获取用户信息

- **GET** `/user/info`（需登录）

**响应：**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "张三",
    "avatar": "https://...",
    "phone": "13812345678",
    "email": "xxx@example.com",
    "createTime": "2024-01-10 10:00:00"
  }
}
```

---

### 1.4 更新用户信息

- **PUT** `/user/info`（需登录）

**请求体：**
```json
{
  "username": "string",
  "avatar": "string",
  "phone": "string",
  "email": "string"
}
```

---

### 1.5 修改密码

- **PUT** `/user/password`（需登录）

**请求体：**
```json
{
  "oldPassword": "string",
  "newPassword": "string"
}
```

---

### 1.6 获取商品列表

- **GET** `/products`

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 否 | 搜索关键词 |
| categoryId | Integer | 否 | 分类ID |
| minPrice | Number | 否 | 最低价格 |
| maxPrice | Number | 否 | 最高价格 |
| sortBy | String | 否 | 排序字段：price/sales/createTime |
| sortOrder | String | 否 | 排序方式：asc/desc |
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认12 |

**响应：**
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "name": "无线蓝牙耳机",
        "price": "299.00",
        "originalPrice": "399.00",
        "mainImage": "https://...",
        "shopId": 1,
        "shopName": "数码旗舰店",
        "salesCount": 1256,
        "rating": 4.8,
        "status": 1
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 12
  }
}
```

---

### 1.7 获取商品详情

- **GET** `/products/{id}`

**响应：**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "name": "无线蓝牙耳机",
    "description": "商品详细描述...",
    "price": "299.00",
    "originalPrice": "399.00",
    "stock": 100,
    "mainImage": "https://...",
    "images": ["https://...", "https://..."],
    "shopId": 1,
    "shopName": "数码旗舰店",
    "shopLogo": "https://...",
    "categoryId": 2,
    "categoryName": "数码电子",
    "salesCount": 1256,
    "rating": 4.8,
    "specs": [
      { "name": "颜色", "values": ["黑色", "白色", "银色"] },
      { "name": "版本", "values": ["标准版", "Pro版"] }
    ],
    "comments": [
      {
        "id": 1,
        "userId": 10,
        "username": "用户A",
        "avatar": "https://...",
        "rating": 5,
        "content": "很好用！",
        "createTime": "2024-02-01"
      }
    ]
  }
}
```

---

### 1.8 获取商品分类

- **GET** `/categories`

**响应：**
```json
{
  "code": 200,
  "data": [
    { "id": 1, "name": "服装", "icon": "https://...", "parentId": 0 },
    { "id": 2, "name": "数码电子", "icon": "https://...", "parentId": 0 }
  ]
}
```

---

### 1.9 购物车 - 获取列表

- **GET** `/cart`（需登录）

**响应：**
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "productId": 101,
        "productName": "无线蓝牙耳机",
        "productImage": "https://...",
        "price": "299.00",
        "quantity": 2,
        "spec": "黑色/标准版",
        "shopId": 1,
        "shopName": "数码旗舰店",
        "checked": true
      }
    ],
    "totalAmount": "598.00",
    "totalCount": 2
  }
}
```

---

### 1.10 购物车 - 加入商品

- **POST** `/cart`（需登录）

**请求体：**
```json
{
  "productId": 101,
  "quantity": 1,
  "spec": "黑色/标准版"
}
```

---

### 1.11 购物车 - 修改数量

- **PUT** `/cart/{id}`（需登录）

**请求体：**
```json
{ "quantity": 3 }
```

---

### 1.12 购物车 - 删除商品

- **DELETE** `/cart/{id}`（需登录）

---

### 1.13 创建订单

- **POST** `/orders`（需登录）

**请求体：**
```json
{
  "cartIds": [1, 2],
  "addressId": 1,
  "remark": "备注信息",
  "couponId": null
}
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "orderId": 1,
    "orderNo": "ORD20240301001",
    "totalAmount": "598.00",
    "payUrl": "https://..."
  }
}
```

---

### 1.14 获取订单列表

- **GET** `/orders`（需登录）

**查询参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| status | Integer | 0=待付款 1=待发货 2=已发货 3=已完成 4=已取消 5=退款中 |
| page | Integer | 页码 |
| pageSize | Integer | 每页数量 |

---

### 1.15 获取订单详情

- **GET** `/orders/{id}`（需登录）

**响应：**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "orderNo": "ORD20240301001",
    "status": 2,
    "totalAmount": "598.00",
    "payTime": "2024-03-01 10:05:00",
    "sendTime": "2024-03-02 09:00:00",
    "address": {
      "receiver": "张三",
      "phone": "13812345678",
      "fullAddress": "北京市朝阳区xxx路1号"
    },
    "items": [
      {
        "productId": 101,
        "productName": "无线蓝牙耳机",
        "productImage": "https://...",
        "price": "299.00",
        "quantity": 2,
        "spec": "黑色/标准版"
      }
    ],
    "expressNo": "SF1234567890",
    "expressCompany": "顺丰速运"
  }
}
```

---

### 1.16 支付订单

- **POST** `/orders/{id}/pay`（需登录）

**请求体：**
```json
{ "payMethod": "alipay" }
```

---

### 1.17 取消订单

- **POST** `/orders/{id}/cancel`（需登录）

---

### 1.18 确认收货

- **POST** `/orders/{id}/confirm`（需登录）

---

### 1.19 申请退款

- **POST** `/orders/{id}/refund`（需登录）

**请求体：**
```json
{ "reason": "退款原因描述" }
```

---

### 1.20 提交评价

- **POST** `/orders/{id}/review`（需登录）

**请求体：**
```json
{
  "productId": 101,
  "rating": 5,
  "content": "商品很好！",
  "images": ["https://..."]
}
```

---

### 1.21 收货地址 CRUD

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/user/addresses` | 获取地址列表 |
| POST | `/user/addresses` | 新增地址 |
| PUT | `/user/addresses/{id}` | 修改地址 |
| DELETE | `/user/addresses/{id}` | 删除地址 |
| PUT | `/user/addresses/{id}/default` | 设为默认 |

**新增/修改地址请求体：**
```json
{
  "receiver": "张三",
  "phone": "13812345678",
  "province": "北京市",
  "city": "北京市",
  "district": "朝阳区",
  "detail": "xxx路1号101室",
  "isDefault": true
}
```

---

### 1.22 收藏管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/user/favorites` | 获取收藏列表 |
| POST | `/user/favorites` | 收藏商品 |
| DELETE | `/user/favorites/{productId}` | 取消收藏 |

---

## 二、商家端接口

### 2.1 商家登录

- **POST** `/merchant/login`

**请求体：**
```json
{
  "username": "string",
  "password": "string"
}
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGci...",
    "merchantInfo": {
      "id": 1,
      "username": "merchant01",
      "shopId": 1,
      "shopName": "好物精选店",
      "shopLogo": "https://..."
    }
  }
}
```

---

### 2.2 获取店铺信息

- **GET** `/merchant/shop`（商家登录）

**响应：**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "name": "好物精选店",
    "logo": "https://...",
    "description": "专注精品好物",
    "address": "北京市朝阳区xxx路",
    "phone": "010-12345678",
    "status": 1,
    "auditStatus": 1,
    "createTime": "2024-01-01"
  }
}
```

---

### 2.3 更新店铺信息

- **PUT** `/merchant/shop`（商家登录）

**请求体：**
```json
{
  "name": "string",
  "logo": "string",
  "description": "string",
  "address": "string",
  "phone": "string"
}
```

---

### 2.4 商家商品管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/merchant/products` | 获取商品列表（支持keyword/status/page） |
| POST | `/merchant/products` | 新增商品 |
| PUT | `/merchant/products/{id}` | 修改商品 |
| DELETE | `/merchant/products/{id}` | 删除商品 |
| PUT | `/merchant/products/{id}/shelf` | 上架/下架 |

**新增/修改商品请求体：**
```json
{
  "name": "商品名称",
  "description": "商品描述",
  "price": "299.00",
  "originalPrice": "399.00",
  "stock": 100,
  "categoryId": 2,
  "mainImage": "https://...",
  "images": ["https://..."],
  "specs": [
    { "name": "颜色", "values": ["黑色", "白色"] }
  ]
}
```

---

### 2.5 商家订单管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/merchant/orders` | 获取订单列表（支持keyword/status/page） |
| GET | `/merchant/orders/{id}` | 获取订单详情 |
| POST | `/merchant/orders/{id}/ship` | 发货 |
| POST | `/merchant/orders/{id}/refund/handle` | 处理退款 |

**发货请求体：**
```json
{
  "expressCompany": "顺丰速运",
  "expressNo": "SF1234567890"
}
```

---

### 2.6 商家数据统计

- **GET** `/merchant/stats`（商家登录）

**查询参数：** `period` = today / week / month / year

**响应：**
```json
{
  "code": 200,
  "data": {
    "totalSales": "12580.00",
    "totalOrders": 86,
    "totalProducts": 32,
    "newOrders": 5,
    "salesTrend": [
      { "date": "2024-03-01", "amount": "1200.00" }
    ],
    "topProducts": [
      { "productId": 1, "name": "无线耳机", "salesCount": 56, "amount": "16744.00" }
    ]
  }
}
```

---

## 三、管理员端接口

### 3.1 管理员登录

- **POST** `/admin/login`

**请求体：**
```json
{
  "username": "admin",
  "password": "string"
}
```

---

### 3.2 平台数据统计

- **GET** `/admin/stats`（管理员登录）

**响应：**
```json
{
  "code": 200,
  "data": {
    "totalUsers": 1280,
    "totalMerchants": 56,
    "totalProducts": 1024,
    "totalOrders": 8650,
    "totalSales": "258600.00",
    "todayOrders": 35,
    "todaySales": "8960.00",
    "orderTrend": [...],
    "salesTrend": [...],
    "categoryDistribution": [...]
  }
}
```

---

### 3.3 商品审核管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/admin/products` | 获取商品列表（支持keyword/auditStatus/page） |
| POST | `/admin/products/{id}/audit` | 审核商品 |

**审核请求体：**
```json
{
  "auditStatus": 1,
  "auditRemark": "审核通过"
}
```
> auditStatus: 1=通过，2=拒绝

---

### 3.4 商家管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/admin/merchants` | 获取商家列表（支持keyword/status/page） |
| GET | `/admin/merchants/{id}` | 获取商家详情 |
| POST | `/admin/merchants/{id}/audit` | 审核商家入驻 |
| PUT | `/admin/merchants/{id}/status` | 启用/禁用商家 |

---

### 3.5 用户管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/admin/users` | 获取用户列表（支持keyword/status/page） |
| GET | `/admin/users/{id}` | 获取用户详情 |
| PUT | `/admin/users/{id}/status` | 启用/禁用用户 |

---

### 3.6 订单管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/admin/orders` | 获取订单列表（支持keyword/status/page） |
| GET | `/admin/orders/{id}` | 获取订单详情 |
| POST | `/admin/orders/{id}/refund/handle` | 处理退款申请 |

**处理退款请求体：**
```json
{
  "action": "approve",
  "remark": "退款理由合理，同意退款"
}
```
> action: approve=同意，reject=拒绝

---

## 四、文件上传接口

### 4.1 上传图片

- **POST** `/upload/image`（需登录）

**请求体：** `multipart/form-data`，字段名 `file`

**响应：**
```json
{
  "code": 200,
  "data": {
    "url": "https://your-cdn.com/images/xxx.jpg"
  }
}
```

---

## 五、错误码说明

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或Token失效 |
| 403 | 无操作权限 |
| 404 | 资源不存在 |
| 409 | 数据冲突（如用户名重复） |
| 500 | 服务器内部错误 |
