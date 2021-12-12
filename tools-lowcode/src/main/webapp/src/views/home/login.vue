<template>
  <div>
    <el-row type="flex" justify="center" :gutter="10">
      <el-col :span="12">
        <Lottie :options="lottieOption" :width="900" :height="550" style="position: relative;top:33%"/>
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
          <el-form ref="loginForm"
                   :model="loginForm"
                   :rules="rules"
                   autocomplete="off"
                   label-position="left">
            <el-form-item prop="username">
              <el-input
                  ref="username"
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
                  ref="password"
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
import {defineComponent, defineAsyncComponent, ref, reactive, onBeforeMount} from "vue";
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
        const rules = reactive({
          username: {required: true, message: '用户名不能为空', trigger: 'blur'},
          password: {required: true, message: '密码不能为空', trigger: 'blur'}
        })

        const loginForm = ref({
          username: '',
          password: ''
        })

        const loading = ref(false);
        const lottieOption = reactive({
          animationData: animationJson1
        })

        onBeforeMount(() => {
          let num = util.common.getRandomInt(1, 3);
          switch (num) {
            case 1:
              lottieOption.animationData = animationJson1
              break
            case 2:
              lottieOption.animationData = animationJson2
              break;
            case 3:
              lottieOption.animationData = animationJson3
              break
            default :
              lottieOption.animationData = animationJson1
              break
          }
        })


        const handleLogin = () => {
          let username_c = false;
          let password_c = false;
          this.$refs.loginForm.validateField('username', e => {
            if (!e) {
              username_c = true
            }
          });
          this.$refs.loginForm.validateField('password', e => {
            if (!e) {
              password_c = true
            }
          });

          if (username_c && password_c) {
            this.loading = true;
            service({
              url: 'tools-lowcode/ui/login',
              method: 'post',
              data: loginForm
            }).then((res) => {
              util.db.set('ACCESS_TOKEN', res.data, {expires: 1});
              this.$router.push({path: '/'})
            }).catch((error) => {
              console.error(error);
              loading.value = false;
            })
          }
        }

        return {
          rules,
          loginForm,
          loading,
          lottieOption,
          handleLogin
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