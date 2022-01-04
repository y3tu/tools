<template>
  <el-container>
    <el-header height="50px">
      <!-- 顶部start -->
      <Header/>
      <!-- 顶部end -->
    </el-header>

    <el-container class="layout-container">
      <el-aside width="250px">
        <!-- 左侧组件start -->
        <LeftAside/>
        <!-- 左侧组件end -->
      </el-aside>

      <el-main>
        <!-- 中间编辑区域start -->
        <VisualEditorContent/>
        <!-- 中间编辑区域end -->

        <!-- 右侧属性面板start -->

        <!-- 右侧属性面板end -->
      </el-main>

    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import {provide} from 'vue'
import Header from './components/header/index.vue'
import LeftAside from './components/left-aside/index.vue'
import {VisualEditorContent} from './components/content/index'

import {VisualEventBusProvider} from './visual-editor.utils'
import {createEvent} from "./utils/event";
// 订阅事件，用于左侧组件拖拽时，联动给容器添加事件
const eventBus = createEvent();
VisualEventBusProvider.provide({eventBus});

import {initVisualData, injectKey} from './hooks/useVisualData'

const visualData = initVisualData()

// 注入可视化编辑器所有配置
provide(injectKey, visualData)

//当浏览器窗口关闭或者刷新时，会触发beforeunload事件
window.addEventListener('beforeunload', () => {

})

</script>

<style lang="scss">
.el-header {
  position: relative;
  z-index: 99;
  background-color: #222222;
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