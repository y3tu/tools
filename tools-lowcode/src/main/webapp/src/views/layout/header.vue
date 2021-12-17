<template>
  <div class="menu-container">

    <div class="menu-left">
      <img :src="logo"/>
    </div>

    <div class="menu-center">
      <el-menu
          :default-active="activeIndex"
          mode="horizontal"
          background-color="#292f36"
          text-color="#fff"
          :router="true"
          @select="handleSelect"
          active-text-color="#ffd04b">
        <template v-for="item in menuList" v-bind:key="item.title">
          <el-menu-item v-if="!item.sub" :index="item.route" v-bind:key="item.route">
            {{ item.title }}
          </el-menu-item>
          <el-sub-menu v-if="item.sub" :index="item.route">
            <template #title>{{ item.title }}</template>
            <el-menu-item v-for="sub in item.sub" :index="sub.route" v-bind:key="sub.route">
              {{ sub.title }}
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </div>

    <div class="menu-right">
      <el-dropdown trigger="click">
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
import {defineComponent, ref, onBeforeMount, onMounted} from 'vue'
import {useRouter} from 'vue-router'

export default defineComponent({
      setup() {
        const menuList = ref([
          {
            route: '/',
            title: '首页'
          },
          {
            route: '/ds',
            title: '数据源'
          },
          {
            route: '/codeGen',
            title: '代码生成'
          },
          {
            route: '/design',
            title: '设计器'
          },
        ])

        //当前菜单页index
        const activeIndex = ref('/')

        //当点击菜单是触发处理，把当前页信息存入localStorage中
        const handleSelect = (index) => {
          activeIndex.value = index
        }

        onBeforeMount(() => {
          const router = useRouter().currentRoute.value;
          activeIndex.value = router.path
        })

        /**
         * 退出登录
         */
        const logout = function () {
          util.cookies.remove('ACCESS_TOKEN');
          location.reload();
        }

        return {
          activeIndex,
          handleSelect,
          menuList,
          logo,
          logout
        }

      }
    }
)
</script>

<style lang="scss" scoped>
.menu-container {

  .menu-left {
    position: absolute;
    width: 50px;
    height: 80px;
    margin-top: 9px;

    img {
      max-width: 100%;
    }
  }

  .menu-center {
    margin-left: 50px;
  }

  .menu-right {
    position: absolute;
    top: 10px;
    right: 20px;
  }
}

</style>
