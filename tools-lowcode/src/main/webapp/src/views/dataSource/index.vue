<template>
  <div class="main-container">
    <div class="head-container">
      <label class="form-item-label">数据源名称</label>
      <el-input clearable v-model="pageInfo.entity.name" placeholder="请输入数据源名称" style="width:200px"
                class="form-item"
                @keyup.enter="query"/>
      <el-button class="form-item" size="mini" type="success" :icon="Search" plain @click="query">
        查询
      </el-button>
      <el-button class="form-item" size="mini" type="warning" :icon="Refresh" plain @click="reset">
        重置
      </el-button>
      <el-button class="form-item" size="mini" type="primary" :icon="Plus" circle @click="add"/>
    </div>

    <el-divider/>
  </div>
</template>

<script setup>
import {reactive,ref} from "vue";
import {Search,Refresh,Plus} from '@element-plus/icons-vue'
import util from "../../utils";

const pageInfo = reactive({
  entity: {
    name: ''
  },
  pageLoading: false,
  current: 1,
  total: 0,
  pageSize: 10,
  records: [],
})

const showEditor = ref(false);

const query = function () {
  util.modal.msgError('test')
}

const reset = function (){

}

const add = function (){

}

</script>

<style>

</style>