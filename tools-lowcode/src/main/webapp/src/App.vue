<template>
  <router-view #="{ Component, route }">
    <component :is="Component" :key="route.path" />
  </router-view>
</template>

<script setup lang="ts">

</script>

<style lang="scss">
@import 'style/common';
</style>
