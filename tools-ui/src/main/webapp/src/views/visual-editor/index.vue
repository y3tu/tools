<template>
  <el-container>
    <!--顶部-->
    <el-header height="80px">
      <Header/>
    </el-header>

    <!--中部-->
    <el-container class="layout-container">
      <!--左侧-->
      <el-aside width="380px">

      </el-aside>

      <!--中部-->
      <el-main>

      </el-main>

    </el-container>

  </el-container>
</template>

<script setup lang="ts">
import Header from './components/header/index'

</script>

<style lang="scss">

.layout-container {
  height: calc(100vh - 80px);
}

</style>