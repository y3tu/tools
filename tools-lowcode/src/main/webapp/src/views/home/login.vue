<template>
  <div>
    <Particles id="particles" :options="particlesConfig"/>
    <el-row type="flex" justify="center" :gutter="10">
      <el-col :span="12">
        <Lottie :options="lottieOption" :width="lottieWidth" :height="lottieHeight" style="position: relative;top:33%"/>
      </el-col>
      <el-col :span="6">
        <el-card style="position: relative;top:50%">
          <template #header>
            <div class="card-header">
              <span class="title">Tools Low Code</span>
            </div>
            <div class="card-header">
              <span class="title">低代码工具平台</span>
            </div>
          </template>
          <el-form ref="loginFormRef"
                   :model="loginForm"
                   :rules="rules"
                   autocomplete="off"
                   label-position="left">
            <el-form-item prop="username">
              <el-input
                  v-model="loginForm.username"
                  placeholder="用户名"
                  prefix-icon="el-icon-user"
                  name="username"
                  type="text"
                  autocomplete="off"
                  @keyup.enter="handleLogin"/>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                  v-model="loginForm.password"
                  prefix-icon="el-icon-key"
                  type="password"
                  placeholder="请输入密码"
                  name="password"
                  autocomplete="off"
                  :show-password="true"
                  @keyup.enter="handleLogin"/>
            </el-form-item>
            <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:14px;"
                       @click.prevent="handleLogin">
              登录
            </el-button>
          </el-form>
        </el-card>

      </el-col>
    </el-row>

  </div>

</template>

<script lang="ts">
import {particlesConfig} from './particles-config'
import {defineComponent, defineAsyncComponent, ref, reactive, onBeforeMount, onMounted} from "vue";
import {useRouter} from 'vue-router';
import {useWindowSize} from '@vueuse/core'
import service from '@/plugins/axios'
import util from "@/utils";
import animationJson1 from './animation/animation1.json'
import animationJson2 from './animation/animation2.json'
import animationJson3 from './animation/animation3.json'

export default defineComponent({
      name: 'login',
      components: {
        Lottie: defineAsyncComponent(() => import('@/components/Lottie/index.vue'))
      },
      setup() {


        //登录校验规则
        const rules = {
          username: {required: true, message: '用户名不能为空', trigger: 'blur'},
          password: {required: true, message: '密码不能为空', trigger: 'blur'}
        }

        const loginFormRef = ref({});
        const loginForm = reactive({
          username: '',
          password: ''
        })

        const loading = ref(false);
        const lottieOption = reactive({
          animationData: animationJson1
        })

        onBeforeMount(() => {
          let num = util.common.getRandomInt(1, 3);
          if (num == 1) {
            lottieOption.animationData = animationJson1
          } else if (num == 2) {
            lottieOption.animationData = animationJson2
          } else {
            lottieOption.animationData = animationJson3
          }
        })

        let lottieWidth = ref(0)
        let lottieHeight = ref(0)
        onMounted(() => {
          const {width, height} = useWindowSize();
          lottieWidth.value = width.value * 2 / 5;
          lottieHeight.value = height.value * 2 / 3;
        })

        const router = useRouter();
        const handleLogin = () => {
          let username_c = false;
          let password_c = false;
          loginFormRef.value.validate(valid => {
            if (valid) {
              username_c = true;
              password_c = true
            }
          })
          if (username_c && password_c) {
            loading.value = true;
            service({
              url: 'tools-lowcode/ui/login',
              method: 'post',
              data: loginForm
            }).then((res) => {
              util.cookies.set('ACCESS_TOKEN', res.data, {expires: 1});
              router.push({path: '/'})
            }).catch((error) => {
              console.error(error);
              loading.value = false;
            })
          }
        }

        return {
          rules,
          loginForm,
          loginFormRef,
          loading,
          lottieOption,
          lottieWidth,
          lottieHeight,
          handleLogin,
          particlesConfig
        }
      },
    }
)

</script>

<style lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center
}

.title {
  background-image: -webkit-linear-gradient(bottom, red, #fd8403, yellow);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: auto;
  font-size: 25px;
  font-weight: bold;
}
</style>