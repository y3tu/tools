import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import '@/styles/index.scss' //全局css

import EventBus from '@/utils/eventBus'
import util from "@/utils";

const app = createApp(App)

app.config.globalProperties.$bus = new EventBus();
app.config.globalProperties.$toast = util.toast.msg;
app.config.globalProperties.$isEmpty = util.validate.isEmpty;
app.config.globalProperties.$isNotEmpty = util.validate.isNotEmpty;
app.config.globalProperties.$deepClone = util.common.deepClone;


import plugin from './plugin'
import componentLib from '@/views/visual-design/component-lib' // 注册自定义组件

app.use(store)
app.use(router)
app.use(componentLib);
app.use(plugin)

app.mount('#app')