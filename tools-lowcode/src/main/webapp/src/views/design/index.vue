<template>
  <el-container>
    <el-main>
      <el-tabs>
        <el-tab-pane>
          <template #label>
            <span>列表</span>
          </template>
          <div class="main-container">
            <div class="head-container">
              <label class="form-item-label">报表名称</label>
              <el-input clearable v-model="pageInfo.entity.name" placeholder="请输入报表名称" style="width:200px"
                        class="form-item"
                        @keyup.enter="queryHandle"/>
              <el-button class="form-item" size="mini" type="success" :icon="Search" plain @click="queryHandle">
                查询
              </el-button>
              <el-button class="form-item" size="mini" type="warning" :icon="Refresh" plain @click="resetHandle">
                重置
              </el-button>
            </div>

            <el-divider/>

            <div v-loading="pageInfo.pageLoading" style="display: flex;flex-wrap: wrap;">

              <div class="page-view-item page-list-add" @click="addHandle">
                <el-icon class="page-list-add-icon">
                  <plus/>
                </el-icon>
              </div>

              <div v-for="(item,index) in pageInfo.records"
                   :key="index"
                   class="page-view-item"
                   @mouseover="item.editable=true"
                   @mouseout="item.editable=false">

                <!--缩略图-->
                <div class="thumb">
                  <img :src="excelImg"/>
                  <div v-loading="itemLoading" class="page-edit-container" v-show="item.editable">
                    <a @click="updateHandle(item)">
                      修改
                    </a>
                  </div>
                </div>

                <!--底部-->
                <div class="item-footer">
                  <span class="item-name">{{ item.name }}</span>
                  <div style="margin-left: 10%;">
                    <a class="opt-show">
                      <el-tooltip content="预览报表" placement="top">
                        <i @click="preview(item)" class="el-icon-view"
                           style="font-size: 16px"></i>
                      </el-tooltip>
                    </a>
                    <a class="opt-show">
                      <el-tooltip content="删除报表" placement="top">
                        <i @click="del(item)" class="el-icon-delete"
                           style="font-size: 16px"></i>
                      </el-tooltip>
                    </a>
                    <a class="opt-show">
                      <el-tooltip content="报表跳转" placement="top">
                        <i @click="goPublish(item)" class="el-icon-s-promotion" style="font-size: 16px"></i>
                      </el-tooltip>
                    </a>
                    <a class="opt-show">
                      <el-tooltip content="复制链接" placement="top">
                        <i @click="copyLink(item)" class="el-icon-copy-document" style="font-size: 16px"></i>
                      </el-tooltip>
                    </a>
                  </div>
                </div>

              </div>
            </div>

            <el-pagination
                style="margin-top: 20px;"
                background layout="prev, pager, next"
                :total="pageInfo.total"
                @current-change="pageChange"
                :page-size="pageInfo.pageSize"
                :current-page="pageInfo.current">
            </el-pagination>

          </div>
        </el-tab-pane>
        <el-tab-pane>
          <template #label>
            <span>模板</span>
          </template>
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script setup>
import {reactive} from "vue";
import {useRouter} from 'vue-router'
import {Search, Refresh, Plus, Connection, Edit, Delete} from '@element-plus/icons-vue'

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

const queryHandle = function () {

}

const resetHandle = function () {

}
const router = useRouter();
const addHandle = function () {
  router.push({path: '/designEditor'})
}

const pageChange = function (e) {

}
</script>

<style>
.page-view-item {
  position: relative;
  margin: 16px;
  display: flex;
  flex-direction: column;
  width: 260px;
  height: 170px;
  border: 1px solid #3a4659;
  overflow: hidden;
}

.page-list-add {
  height: 184px;
  width: 258px;
  border: 1px solid #00baff;
  font-size: 14px;
  color: #8eeeff;
  background-image: linear-gradient(-90deg, rgba(0, 222, 255, .39) 0, rgba(0, 174, 255, .19) 100%);
  box-shadow: 0 0 10px 0 rgba(55, 224, 255, .3);
  cursor: pointer;
}

.page-list-add-icon {
  height: 100%;
  color: black;
  flex-direction: column;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left: 45%
}

.page-view-item:hover {
  box-shadow: 0 0 20px 0 #000;
  border: 1px solid #00baff;
}

.page-view-item .thumb {
  position: relative;
  height: calc(100% - 36px);
}

.page-view-item .thumb img {
  width: 100%;
  height: 100%
}

.page-view-item .item-footer {
  font-size: 12px;
  width: 100%;
  height: 36px;
  display: flex;
  align-items: center;
  position: absolute;
  bottom: 0;
  justify-content: space-between;
  background: #1d262e;
  box-sizing: border-box;
  padding: 0 10px;
  color: #bcc9d4;
}

.page-view-item .item-name {
  width: 100px;
  padding: 0 5px;
  line-height: 28px;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  border: 1px solid transparent;
}

.opt-show {
  color: #bcc9d4;
  margin-right: 10px;
  cursor: pointer;
}

.page-edit-container {
  position: absolute;
  top: 0;
  left: 0;
  background-color: rgba(29, 38, 46, 0.8);
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-edit-container a {
  display: inline-block;
  vertical-align: middle;
  height: 32px;
  line-height: 32px;
  padding: 0 30px;
  box-sizing: border-box;
  outline: 0;
  text-align: center;
  font-size: 14px;
  background-image: linear-gradient(-225deg, #00d3f1 0, #12b3ff 100%);
  color: #293f52;
  border: none;
  transition: .3s ease;
  cursor: pointer;
}

/*1.显示滚动条：当内容超出容器的时候，可以拖动：*/
.el-drawer__body {
  overflow: auto;
}

/*2.隐藏滚动条，太丑了*/
.el-drawer__container ::-webkit-scrollbar {
  display: none;
}


</style>