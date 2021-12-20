<template>
  <el-container>
    <el-header height="60px">
      <!-- 顶部start -->
      <Header />
      <!-- 顶部end -->
    </el-header>

    <el-container class="layout-container">
      <el-aside>
        <!-- 左侧组件start -->

        <!-- 左侧组件end -->
      </el-aside>

      <el-main>
        <!-- 中间编辑区域start -->

        <!-- 中间编辑区域end -->

        <!-- 右侧属性面板start -->

        <!-- 右侧属性面板end -->
      </el-main>

    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import Header from './components/header/index.vue'

</script>

<style lang="scss">
.el-header,
.el-footer {
  position: relative;
  z-index: 99;
  background-color: white;
}

.el-aside {
  background-color: white;
}

.layout-container {
  height: calc(100vh - 80px);
}

.el-main {
  position: relative;
  padding: 12px;
  background-color: #f5f5f5;
  @media (min-width: 1111px) {
    overflow-x: hidden;
  }
}
</style>