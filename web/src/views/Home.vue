<template>
  <div class="home-portal">
    <div class="hero">
      <h1 class="title">好物购买系统</h1>
      <p class="subtitle">优质商品，一键购达</p>
      <div class="portal-cards">
        <div class="portal-card user-card" @click="$router.push('/user/home')">
          <div class="card-icon">
            <el-icon size="48"><User /></el-icon>
          </div>
          <h3>用户端</h3>
          <p>浏览商品 · 购物下单 · 订单管理</p>
          <el-button type="primary" size="large" round>进入购物</el-button>
        </div>
        <div class="portal-card merchant-card" @click="$router.push('/merchant/login')">
          <div class="card-icon">
            <el-icon size="48"><Shop /></el-icon>
          </div>
          <h3>商家端</h3>
          <p>店铺管理 · 商品发布 · 订单处理</p>
          <el-button type="warning" size="large" round>进入商家后台</el-button>
        </div>
        <div class="portal-card admin-card" @click="$router.push('/admin/login')">
          <div class="card-icon">
            <el-icon size="48"><Setting /></el-icon>
          </div>
          <h3>管理员端</h3>
          <p>平台管控 · 数据统计 · 权限管理</p>
          <el-button type="danger" size="large" round>进入管理后台</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { User, Shop, Setting } from '@element-plus/icons-vue'
// 入口选择页
</script>

<style scoped>
.home-portal {
  min-height: 100vh;
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c5a 50%, #ffad7a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.hero {
  text-align: center;
  padding: 40px 20px;
}
.title {
  font-size: 48px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 12px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.2);
}
.subtitle {
  font-size: 18px;
  color: rgba(255,255,255,0.9);
  margin-bottom: 60px;
}
.portal-cards {
  display: flex;
  gap: 32px;
  justify-content: center;
  flex-wrap: wrap;
}
.portal-card {
  background: #fff;
  border-radius: 20px;
  padding: 48px 40px;
  width: 260px;
  cursor: pointer;
  box-shadow: 0 8px 40px rgba(0,0,0,0.15);
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}
.portal-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0,0,0,0.2);
}
.card-icon {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.user-card .card-icon { background: #fff3ee; color: #ff6b35; }
.merchant-card .card-icon { background: #fdf6e3; color: #e6a23c; }
.admin-card .card-icon { background: #fff0f0; color: #f56c6c; }
.portal-card h3 { font-size: 22px; font-weight: 700; color: #333; }
.portal-card p { font-size: 13px; color: #999; line-height: 1.6; text-align: center; }
</style>
