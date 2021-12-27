<template>
  <el-row class="header">
    <!--    左侧logo start-->
    <el-col :span="6">
      <div class="logo"></div>
    </el-col>
    <!--    左侧logo end-->

    <!--    中间操作页面部分 start-->
    <el-col :span="12" class="tool-button">
      <el-button type="warning" size="small" :icon="RefreshLeft" round>撤销</el-button>
      <el-button type="warning" size="small" :icon="Brush" round>重做</el-button>
      <el-button type="primary" size="small" :icon="View" round>预览</el-button>
      <el-button type="primary" size="small" :icon="Cloudy" round>保存</el-button>
    </el-col>

  </el-row>
</template>

<script lang="ts">
import {defineComponent, reactive, toRefs} from 'vue'
import {RefreshLeft,View,Cloudy,Brush} from '@element-plus/icons-vue'

export default defineComponent({
  name: 'Header',
  setup() {
    return {
      RefreshLeft,
      View,
      Cloudy,
      Brush
    }
  }
})
</script>

<style lang="scss" scoped>
.header {
  width: 100%;

  .logo {
    width: 60px;
    height: 60px;
    background-image: url('@/assets/logo.png');
    background-repeat: no-repeat;
    background-size: contain;
  }

  .tool-button {
    margin-top: 10px;
  }

}
</style>