<template>
  <el-row type="flex" class="header">
    <!--左侧logo-->
    <el-col :span="6">
      <div class="logo"></div>
      <h3>可视化页面定制</h3>
    </el-col>

    <!--中间-->
    <el-col :span="12">

    </el-col>

    <!--右侧工具栏-->
    <el-col :span="6">

    </el-col>

  </el-row>
</template>
<script lang="ts">
import {defineComponent, reactive, toRefs} from 'vue'
import {BASE_URL} from '@/views/visual-editor/utils'


export default defineComponent({
  name: 'Header',
  setup() {
    const state = reactive({
      //是否显示预览
      isShowH5Preview: false
    })

    return {
      ...toRefs(state),
      BASE_URL
    }
  }
})
</script>

<style lang="scss" scoped>
.header {
  width: 100%;

  .logo {

  }

}

</style>