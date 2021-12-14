import { createApp } from 'vue'
import App from './App.vue'


import router from './router/'
import store from './store/'

const app = createApp(App)
//插件
import plugins from './plugins'
app.use(plugins)

app.use(router).use(store)
// 路由准备完毕再挂载
router.isReady().then(() => app.mount('#app'))
