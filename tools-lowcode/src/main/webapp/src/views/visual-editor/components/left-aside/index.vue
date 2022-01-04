<template>
  <el-tabs v-model="activeName" tab-position="left" class="left-aside">
    <template v-for="tabItem in tabs" :key="tabItem.name">
      <el-tab-pane :name="tabItem.name" lazy>
        <template #label>
          <div class="tab-item">
            <i :class="tabItem.icon"></i>
            {{ tabItem.label }}
          </div>
        </template>
        <component :is="tabItem.name" v-bind="$attrs"/>
      </el-tab-pane>
    </template>
  </el-tabs>
</template>

<script lang="ts">
import {defineComponent, reactive, toRefs} from 'vue'
import components from './components'
const tabs = Object.keys(components)
    .map((name) => {
      const {label, icon, order} = components[name]
      return {label, icon, name, order}
    })
    .sort((a, b) => a.order - b.order)

export default defineComponent({
  name: 'LeftAside',
  components,
  setup() {
    const state = reactive({
      activeName: tabs[0].name
    })

    return {
      ...toRefs(state),
      tabs
    }
  }
})
</script>

<style lang="scss" scoped>
.left-aside {
  height: 100%;
}
</style>