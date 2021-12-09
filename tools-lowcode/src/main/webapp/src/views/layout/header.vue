<template>
  <div class="menu-container">

    <el-menu
        default-active="/report/list"
        mode="horizontal"
        background-color="#545c64"
        text-color="#fff"
        :router="true"
        active-text-color="#ffd04b">
      <template v-for="item in menuList" v-bind:key="item.title">
        <el-menu-item v-if="!item.sub" :index="item.route" v-bind:key="item.route">
          {{ item.title }}
        </el-menu-item>
        <el-submenu v-if="item.sub" :index="item.title">
          <template #title>{{ item.title }}</template>
          <el-menu-item v-for="sub in item.sub" :index="sub.route" v-bind:key="sub.route">
            {{ sub.title }}
          </el-menu-item>
        </el-submenu>
      </template>
    </el-menu>

    <div class="menu-right">
      <el-dropdown  trigger="click">
        <el-avatar icon="el-icon-user-solid"></el-avatar>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>
              <span style="display:block;" @click="logout">退出</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

    </div>

  </div>

</template>

<script>

import logo from '../../assets/logo.png'
import util from "@/utils";

export default {
  name: 'Header',
  computed: {},
  data() {
    return {
      menuList: [
        {
          route: '/',
          title: '首页'
        },
        {
          title: '报表',
          sub: [
            {
              route: '/report/home',
              title: '总览',
            },
            {
              route: '/report/dict',
              title: '字典'
            },
            {
              route: '/report/dataSource',
              title: '数据源'
            },
            {
              route: '/report/reportDownload',
              title: '报表下载'
            },
          ]
        },
        {
          route: '/visual',
          title: '可视化'
        },
        {
          route: '/cache',
          title: '缓存'
        },

      ],
      logo,
      searchContent: ''
    }
  },
  methods: {
    logout(){
      util.cookies.remove('ACCESS_TOKEN');
      location.reload();
    }
  }
}
</script>

<style lang="scss" scoped>
  .menu-container{
    .menu-right{
      position: absolute;
      top: 10px;
      right: 20px;
    }
  }

</style>
