<template>
  <router-view />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background-color: #f5f5f5;
  color: #333;
}

:root {
  --primary: #ff6b35;
  --primary-light: #ff8c5a;
  --primary-dark: #e55a28;
  --bg-light: #f8f9fa;
  --text-main: #333333;
  --text-sub: #666666;
  --text-muted: #999999;
  --border: #e8e8e8;
}

.el-button--primary {
  background-color: var(--primary) !important;
  border-color: var(--primary) !important;
}

.el-button--primary:hover {
  background-color: var(--primary-light) !important;
  border-color: var(--primary-light) !important;
}

a {
  text-decoration: none;
  color: inherit;
}
</style>
