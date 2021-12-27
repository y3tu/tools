<template>
  <el-form ref="form" :model="dataSource" :rules="formRule" label-position="right" label-width="120px">
    <el-form-item label="数据源名称" prop="name">
      <el-input v-model="dataSource.name"/>
    </el-form-item>
    <el-form-item label="数据源类型" prop="dbType">
      <el-select v-model="dataSource.dbType" placeholder="请选择">
        <el-option value="mysql" label="mysql"/>
        <el-option value="oracle" label="oracle"/>
      </el-select>
    </el-form-item>
    <el-form-item label="驱动" prop="dbDriver">
      <el-input v-model="dataSource.dbDriver"/>
    </el-form-item>
    <el-form-item label="数据源地址" prop="dbUrl">
      <el-input v-model="dataSource.dbUrl"/>
    </el-form-item>
    <el-form-item label="登录用户名" prop="dbUsername">
      <el-input v-model="dataSource.dbUsername"/>
    </el-form-item>
    <el-form-item label="登录密码" prop="dbPassword">
      <el-input v-model="dataSource.dbPassword" show-password/>
    </el-form-item>

    <el-row type="flex" justify="end">
      <el-col :span="3">
        <el-tooltip effect="dark" content="保存" placement="top">
          <el-button
              :loading="buttonLoading"
              @click="submitForm"
              type="primary" :icon="Check" circle></el-button>
        </el-tooltip>
      </el-col>
      <el-col :span="3">
        <el-tooltip effect="dark" content="清空" placement="top">
          <el-button type="info" :icon="Refresh" circle></el-button>
        </el-tooltip>
      </el-col>
    </el-row>

  </el-form>


</template>

<script setup>
import {reactive, ref} from "vue";
import {Check, Refresh} from '@element-plus/icons-vue'

import util from '@/utils'
import {add, update} from "./api";


const data = reactive({
  dataSource: {},
  rules: {
    name: {required: true, message: "数据源名称不能为空", trigger: 'blur'},
    dbType: {required: true, message: "数据源类型不能为空", trigger: 'blur'},
    dbDriver: {required: true, message: "驱动不能为空", trigger: 'blur'},
    dbUrl: {required: true, message: "数据源地址不能为空", trigger: 'blur'},
    dbUsername: {required: true, message: "用户名不能为空", trigger: 'blur'},
    dbPassword: {required: true, message: "密码不能为空", trigger: 'blur'}
  },
  buttonLoading: false
})

const dataSource = reactive({
  id: '',
  name: '',
  dbType: {},
  dbDriver: '',
  dbUrl: '',
  dbUsername: '',
  dbPassword: ''
})


const form = ref({})
const formRule = reactive({
  name: {required: true, message: "数据源名称不能为空", trigger: 'blur'},
  dbType: {required: true, message: "数据源类型不能为空", trigger: 'blur'},
  dbDriver: {required: true, message: "驱动不能为空", trigger: 'blur'},
  dbUrl: {required: true, message: "数据源地址不能为空", trigger: 'blur'},
  dbUsername: {required: true, message: "用户名不能为空", trigger: 'blur'},
  dbPassword: {required: true, message: "密码不能为空", trigger: 'blur'}
})

let buttonLoading = ref(false)
const emit = defineEmits(['success'])

const submitForm = function () {
  form.value.validate((valid) => {
    if (valid) {
      buttonLoading.value = true;
      if (!dataSource.id) {
        // create
        add(dataSource).then(() => {
          buttonLoading.value = false;

          util.modal.notifySuccess('创建成功');
          emit('success')
        }).catch(() => {
          buttonLoading.value = false;
        });
      } else {
        // update
        update(dataSource).then(() => {
          buttonLoading.value = false;
          util.modal.notifySuccess('更新成功');
          emit('success')
        }).catch(() => {
          buttonLoading.value = false;
        });
      }
    } else {
      return false;
    }
  })
}

</script>

