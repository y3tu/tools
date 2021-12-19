<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script setup lang="ts">
import { provide } from 'vue'
import {injectKey,initDesignData} from './views/design/hooks/useDesignData'

const designData = initDesignData()

provide(injectKey,designData)
</script>

<style lang="scss">
@import 'style/common';
</style>
