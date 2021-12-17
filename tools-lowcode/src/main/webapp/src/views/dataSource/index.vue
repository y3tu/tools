<template>
  <div class="main-container">
    <div class="head-container">
      <label class="form-item-label">数据源名称</label>
      <el-input clearable v-model="pageInfo.entity.name" placeholder="请输入数据源名称" style="width:200px"
                class="form-item"
                @keyup.enter="queryHandle"/>
      <el-button class="form-item" size="mini" type="success" :icon="Search" plain @click="queryHandle">
        查询
      </el-button>
      <el-button class="form-item" size="mini" type="warning" :icon="Refresh" plain @click="resetHandle">
        重置
      </el-button>
      <el-button class="form-item" size="mini" type="primary" :icon="Plus" circle @click="addHandle"/>
    </div>

    <el-divider/>

    <el-table
        ref="table"
        border
        size="mini"
        v-loading="pageInfo.pageLoading"
        :data="pageInfo.records"
        style="width: 100%;">
      <el-table-column label="数据源名称" prop="name" align="center" show-overflow-tooltip min-width="120px">
        <template #default="scope">
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="驱动" :show-overflow-tooltip="true" align="center" min-width="150px">
        <template #default="scope">
          <span>{{ scope.row.dbDriver }}</span>
        </template>
      </el-table-column>
      <el-table-column label="数据库类型" align="center" min-width="100px">
        <template #default="scope">
          <span>{{ scope.row.dbType }}</span>
        </template>
      </el-table-column>
      <el-table-column label="数据源地址" align="center" show-overflow-tooltip min-width="200px">
        <template #default="scope">
          <span>{{ scope.row.dbUrl }}</span>
        </template>
      </el-table-column>
      <el-table-column label="登录用户名" align="center" min-width="100px">
        <template #default="scope">
          <span>{{ scope.row.dbUsername }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" show-overflow-tooltip min-width="100px">
        <template #default="scope">
          <span>{{ scope.row.remarks }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" min-width="120px">
        <template #default="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" min-width="110px">
        <template #default="scope">
          <span>{{ scope.row.updateTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" min-width="150px" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-icon class="table-operation" style="color: #87d068;" >
            <connection @click="testHandle(scope.row)"/>
          </el-icon>
          <el-icon class="table-operation" style="color: #2db7f5;" >
            <edit @click="editHandle(scope.row)"/>
          </el-icon>
          <el-icon class="table-operation" style="color: #f50;" >
            <delete @click="delHandle(scope.row)"/>
          </el-icon>
        </template>
      </el-table-column>
    </el-table>

    <!--分页组件-->
    <el-pagination
        style="margin-top: 20px;"
        background layout="prev, pager, next"
        :total="pageInfo.total"
        @current-change="pageChange"
        :page-size="pageInfo.pageSize"
        :current-page="pageInfo.current">
    </el-pagination>

    <el-drawer v-model='showEditor'
               size="30%"
               direction="ltr">
      <Editor ref="editor"
              @success="query"/>
    </el-drawer>

  </div>
</template>

<script setup>
import {defineAsyncComponent, reactive, ref} from "vue";
import {Search, Refresh, Plus,Connection,Edit,Delete} from '@element-plus/icons-vue'
import util from "../../utils";
import {query, testConnect, del} from './api'

const Editor = defineAsyncComponent(() => import('@/views/dataSource/editor.vue'))

let pageInfo = reactive({
  entity: {
    name: ''
  },
  pageLoading: false,
  current: 1,
  total: 1,
  pageSize: 10,
  records: [],
})

const showEditor = ref(false);

//查询
const queryHandle = function () {
  showEditor.value = false;
  pageInfo.pageLoading = true
  query(pageInfo).then(res => {
    debugger
    pageInfo.pageLoading = false

    pageInfo.records = res.data.records;
    pageInfo.total = res.data.size;

  }).catch(() => {
    pageInfo.pageLoading = false
  })
}

//重置查询参数
const resetHandle = function () {

}

//测试数据库连接
const testHandle = function (row) {
  testConnect(row.id).then(res => {
    if (res.data) {
      util.modal.notifySuccess('连接成功');
    } else {
      util.modal.notifyError('连接失败,请检查配置!');
    }
  })
}

//新增数据源
const addHandle = function () {
  showEditor.value = true;
}

//修改数据源
const editHandle = function () {
  showEditor.value = true;
}

//删除数据源
const delHandle = function (row) {
  util.modal.confirm('选中数据将被永久删除, 是否继续？').then(() => {
    del(row.id).then(() => {
      util.modal.notifySuccess('删除成功');
      query()
    })
  })
}

//页数改变
const pageChange = function (e) {
  pageInfo.current = e;
  query()
}


</script>

<style>

</style>