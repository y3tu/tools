<template>
  <el-container>
    <el-header height="50px">
      <!-- 顶部start -->
      <Header/>
      <!-- 顶部end -->
    </el-header>

    <el-container class="layout-container">
      <el-aside width="380px">
        <!-- 左侧组件start -->
        <LeftAside/>
        <!-- 左侧组件end -->
      </el-aside>

      <el-main>
        <!-- 中间编辑区域start -->
        <SimulatorEditor/>
        <!-- 中间编辑区域end -->

        <!-- 右侧属性面板start -->
        <RightAttributePanel/>
        <!-- 右侧属性面板end -->
      </el-main>

    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import {provide} from 'vue'
import Header from './components/header/index.vue'
import LeftAside from './components/left-aside/index.vue'
import SimulatorEditor from './components/simulator-editor/simulator-editor.vue'
import RightAttributePanel from './components/right-attribute-panel'
import {initVisualData, injectKey,localKey} from './hooks/useVisualData'

const visualData = initVisualData()

// 注入可视化编辑器所有配置
provide(injectKey, visualData)

const { jsonData } = visualData


//当浏览器窗口关闭或者刷新时，会触发beforeunload事件
window.addEventListener('beforeunload', () => {
  sessionStorage.setItem(localKey, JSON.stringify(jsonData))
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