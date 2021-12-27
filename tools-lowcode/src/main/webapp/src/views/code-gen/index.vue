<template>
  <Developing/>
</template>

<script setup>
  import {defineAsyncComponent} from "vue";

  const Developing = defineAsyncComponent(() => import('@/components/Developing/index.vue'))

</script>