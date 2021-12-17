<template>
  <div class="home">
    <Toolbar/>

    <main>
      <!-- 左侧组件列表 -->
      <section class="left">
        <ComponentsList/>
      </section>
      <!-- 中间画布 -->
      <section class="center">
        <div
            class="content"
            @drop="handleDrop"
            @dragover="handleDragOver"
            @mousedown="handleMouseDown"
            @mouseup="deselectCurComponent"
        >
          <Editor/>
        </div>
      </section>
      <!-- 右侧属性列表 -->
      <section class="right">
        <el-tabs v-model="activeName">
          <el-tab-pane label="属性" name="attr">

          </el-tab-pane>
          <el-tab-pane label="动画" name="animation">

          </el-tab-pane>
          <el-tab-pane label="事件" name="events">

          </el-tab-pane>
        </el-tabs>
      </section>
    </main>
  </div>
</template>

<script setup>
import {defineAsyncComponent, ref} from "vue";
import {mapState, useStore} from 'vuex'

const store = useStore()
const Editor = defineAsyncComponent(() => import('./components/editor/index.vue'))
const Toolbar = defineAsyncComponent(() => import('./components/toolbar.vue'))
const ComponentsList = defineAsyncComponent(() => import('./components/componentsList.vue'))
import util from '@/utils'

import componentList from './custom-components/list'


const activeName = ref('')

const handleDrop = function (e) {
  //当组件移动画布上时，添加组件
  e.preventDefault()
  e.stopPropagation()
  const index = e.dataTransfer.getData('index')
  const rectInfo = util.common.$('#editor').getBoundingClientRect()
  if (index) {
    const component = util.common.deepCopy(componentList[index])
    component.style.top = e.clientY - rectInfo.y
    component.style.left = e.clientX - rectInfo.x
    component.id = util.common.createUUID();

    store.commit('design/addComponent', {component})
    store.commit('design/recordSnapshot')
  }

}

const handleDragOver = function (e) {
  e.preventDefault()
  e.dataTransfer.dropEffect = 'copy'
}

const handleMouseDown = function () {
}

const deselectCurComponent = function () {
}

</script>

<style lang="scss">
.home {
  height: 100vh;
  background: #fff;

  main {
    height: calc(100% - 64px);
    position: relative;

    .left {
      position: absolute;
      height: 100%;
      width: 200px;
      left: 0;
      top: 0;
      padding-top: 10px;
    }

    .right {
      position: absolute;
      height: 100%;
      width: 262px;
      right: 0;
      top: 0;
    }

    .center {
      margin-left: 200px;
      margin-right: 262px;
      background: #f5f5f5;
      height: 100%;
      overflow: auto;
      padding: 20px;

      .content {
        width: 100%;
        height: 100%;
        overflow: auto;
      }
    }
  }

  .placeholder {
    text-align: center;
    color: #333;
  }
}
</style>

